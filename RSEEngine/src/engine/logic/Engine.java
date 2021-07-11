package engine.logic;

import engine.dto.StockDT;
import engine.dto.TradeCommandDT;
import engine.users.UserAction;
import engine.users.UserHoldings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import engine.jaxb.schema.generated.*;
import engine.users.User;
import engine.users.UsersManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.*;


/**
 * This class main purpose is to handle all the data to execute the sell and buy commands of all the companies stocks.
 * The class Implements a singleton design pattern.
 */
public class Engine implements Trader {
    private static Engine instance = new Engine();                                 //the single instance of the class.
    private static final String JAXB_XML_PACKAGE_NAME = "engine.jaxb.schema.generated";

    private MultiKeyMap<String, Stock> stocks = new MultiKeyMap<String, Stock>();//will be search with their symbol
    private Object lock2 = new Object();
    private UsersManager users = UsersManager.getInstance();

    private Engine(){}//private to prevent a creation of instances

    public void addNewStock(String companyName, String symbol, int sharesQuantity,int  estimatedValue, String username){
        if(isSymbolExists(symbol))
            throw new IllegalArgumentException("There's already a stock with this symbol. The public offering was canceled.");

        int sharePrice = estimatedValue/sharesQuantity;
        Stock newStock = new Stock(companyName,symbol,sharePrice);
        stocks.put(symbol,companyName,newStock);    // todo: check if the order of symbol first and then companyName is correct
        users.getUser(username).getActions().add(new UserAction(("IPO of "+symbol+" stock"),LocalDateTime.now(),estimatedValue,users.getUser(username).getUserBalance(),(estimatedValue+users.getUser(username).getUserBalance())));
        users.getUser(username).addNewStock(newStock,sharesQuantity,estimatedValue);

    }

    public UsersManager getUsersManager(){
        return users;
    }
    /**
     * A method that let access to the single instance of the class.
     *
     * @return the single instance of the class.
     */
    public static Engine getInstance() {
        return instance;
    }

    /**
     * A getter of the list of stocks.
     *
     * @return
     */
    private Collection<Stock> getListOfStocks() {
        return stocks.values();
    }

    /**
     * A method that adds a new stock to the MultiKeyMap of stocks.
     *
     * @param stock the stock that we would like to add to the MultiKeyMap of stocks.
     * @throws InputMismatchException will be thrown in case that the stock is already exists.
     */
    public void addStock(Stock stock) throws InputMismatchException {
        if (stocks.containsKey(stock.getSymbol()))                                       //checks if the stock is already exists in the MultiKeyMap of stocks.
            throw new InputMismatchException("This company already have stocks in the system.");
        stocks.put(stock.getSymbol(), stock.getCompanyName(), stock);    //adds the stock to the MultiKeyMap of stocks.
    }

    /**
     * A method that return the number of stocks we have in the system.
     *
     * @return the number of stocks that the MultiKeyMap of stocks contains.
     */
    public int stocksCount() {
        return stocks.size();
    }

    /**
     * A method that return an instance of single stock.
     *
     * @param symbol the symbol of the desired stock.
     * @return the stock that matches the symbol.
     * @throws InputMismatchException will be thrown in case there isn't a stock with this symbol.
     */
    private Stock getSingleStock(String symbol) throws InputMismatchException {
        if (!(stocks.containsKey(symbol.toUpperCase()))) // check if there is a company with this symbol
            throw new InputMismatchException("There is no stock with this symbol. ");
        return stocks.get(symbol.toUpperCase());
    }

    public StockDT getSingleStockData(String symbol) throws InputMismatchException {
        if (!(stocks.containsKey(symbol.toUpperCase()))) // check if there is a company with this symbol
            throw new InputMismatchException("There is no stock with this symbol. ");
        return new StockDT(stocks.get(symbol.toUpperCase()));
    }

    /**
     * A method that checks by a stock symbol if the stock exists in the system.
     *
     * @param symbol the symbol of the stock that we would like to check it existence.
     * @return return true if the stock exists, and false otherwise.
     */
    public boolean isExistingStock(String symbol) {
        return stocks.containsKey(symbol.toUpperCase());
    }

    /**
     * Saves the data of the system into a xml file.
     *
     * @param path
     * @throws IOException
     * @throws JAXBException
     * @throws IllegalArgumentException
     */
    /*public void saveDataToFile(String path) throws IOException, JAXBException, IllegalArgumentException {
        String tmpPath = "./tmpSave.xml";
        try {
            File XMLFilePath = new File(path);
            File tmpXMLFilePath = new File(tmpPath);
            if (!isXMLFile(path))
                throw new IllegalArgumentException("The given file is not a xml file.");
            if (!tmpXMLFilePath.createNewFile()) {
                tmpXMLFilePath.delete();
                if (!tmpXMLFilePath.createNewFile())
                    throw new IOException("Error occurred in the creation of a temporary XML file.");
            }
            OutputStream outputStream = new FileOutputStream(new File(tmpPath));
            serializeFrom(outputStream, tmpPath);
            Files.copy(tmpXMLFilePath.toPath(), XMLFilePath.toPath(), StandardCopyOption.REPLACE_EXISTING);
            tmpXMLFilePath.delete();
        } catch (JAXBException e) {
            throw new JAXBException("JAXB Exception detected.");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("There is no such a XML file.");
        }
    }*/

    /**
     * serializes the data into a xml file.
     *
     * @param out
     * @param tmpPath
     * @throws JAXBException
     */
    /*private void serializeFrom(OutputStream out, String tmpPath) throws JAXBException {
        RizpaStockExchangeDescriptor rse = new RizpaStockExchangeDescriptor();
        Collection<Stock> stocksCollection = stocks.values();
        RseStocks rseStocks = new RseStocks();
        for (Stock s : stocksCollection) {
            RseStock newRseStock = new RseStock();
            newRseStock.setRseCompanyName(s.getCompanyName());  //casts the data from the program classes into the generated classes.
            newRseStock.setRsePrice(s.getSharePrice().intValue());
            newRseStock.setRseSymbol(s.getSymbol());

            List<TradeCommandDT> buyTradeCommands = s.getBuyCommandsList(); //cast the but commands
            List<RseTradeCommand> rseBuyTradeCommands = new LinkedList<>();
            for (TradeCommandDT tc : buyTradeCommands) {
                RseTradeCommand newRseTC = new RseTradeCommand();
                newRseTC.setRseDir(tc.getDirection().name());
                newRseTC.setRseDateTime(tc.getDateTimeStamp().toString());
                newRseTC.setRsePrice(tc.getPrice());
                newRseTC.setRseQuantity(tc.getQuantity());
                newRseTC.setRseSymbol(tc.getSymbol());
                newRseTC.setRseType(tc.getCommandType().name());
                rseBuyTradeCommands.add(newRseTC);
            }
            newRseStock.setRseBuyCommands(rseBuyTradeCommands);

            List<TradeCommandDT> sellTradeCommands = s.getSellCommandsList(); //casts the sell commands
            List<RseTradeCommand> rseSellTradeCommands = new LinkedList<>();
            for (TradeCommandDT tc : sellTradeCommands) {
                RseTradeCommand newRseTC = new RseTradeCommand();
                newRseTC.setRseDir(tc.getDirection().name());
                newRseTC.setRseDateTime(tc.getDateTimeStamp().toString());
                newRseTC.setRsePrice(tc.getPrice());
                newRseTC.setRseQuantity(tc.getQuantity());
                newRseTC.setRseSymbol(tc.getSymbol());
                newRseTC.setRseType(tc.getCommandType().name());
                rseSellTradeCommands.add(newRseTC);
            }
            newRseStock.setRseSellCommands(rseSellTradeCommands);

            List<Transaction> transactions = s.getStockTransactions(); // casts the transactions
            List<RseTransactions> rseTransactions = new LinkedList<>();
            for (Transaction tran : transactions) {
                RseTransactions newRseTran = new RseTransactions();
                newRseTran.setRsePrice(tran.getPrice());
                newRseTran.setRseTurnover(tran.getTurnover());
                newRseTran.setRseDateTime(tran.getDateStamp().toString());
                newRseTran.setRseQuantity(tran.getQuantity());
                rseTransactions.add(newRseTran);
            }
            newRseStock.setRseTransactions(rseTransactions);

            rseStocks.getRseStock().add(newRseStock);
        }
        rse.setRseStocks(rseStocks);

        JAXBContext jc = JAXBContext.newInstance(RizpaStockExchangeDescriptor.class);
        Marshaller m = jc.createMarshaller();

        m.setProperty(m.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(rse, new File(tmpPath)); //marshals the data into a xml file.

    }*/

    /**
     * A method that uploads a system instance from an xml file.
     *
     * @throws FileNotFoundException    will be thrown in case the file isn't exits.
     * @throws JAXBException            will be thrown in case the JAXB process failed.
     * @throws IllegalArgumentException will be thrown in case the the file isn't a xml file.
     */
   /* public void uploadDataFromFile(String path, DoubleProperty doubleProperty, StringProperty stringProperty) throws FileNotFoundException, JAXBException, IllegalArgumentException // first option
    {
        // need to upload all the stocks from xml file
        MultiKeyMap<String,Stock> tmpStocks = new MultiKeyMap<String, Stock>();
        Map<String,Integer> tmpHoldings = new TreeMap<>();
        try {
            File xmlPath = new File(path);
            InputStream inputStream = new FileInputStream(new File(path));
            stringProperty.setValue("Opening file.. ");
            if (!isXMLFile(path))
                throw new IllegalArgumentException("The given file is not a xml file.");
            deserializeFrom(inputStream, tmpStocks);
            //stocks.clear();
            stocks = tmpStocks;
            stringProperty.setValue("Fetching data from file ended.");
        } catch (JAXBException e) {
            throw new JAXBException("JAXB Exception detected.");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("There is no such a XML file.");
        }
    }*/
    public void uploadDataFromFile(InputStream in, User user) throws FileNotFoundException, JAXBException, IllegalArgumentException // first option
    {
        // need to upload all the stocks from xml file
        MultiKeyMap<String,Stock> tmpStocks = new MultiKeyMap<String, Stock>();
        Map<String,Integer> tmpHoldings = new TreeMap<>();
        try {
            deserializeFrom(in, tmpStocks,tmpHoldings);
            for(Stock stock: tmpStocks.values()){
                if(!stocks.containsKey(stock.getSymbol()))
                    stocks.put(stock.getSymbol(),stock.getCompanyName(),stock);
            }
            float pre = user.getUserBalance();
            int amount = 0;
            for(String symbol: tmpHoldings.keySet()){
                if(user.getUserStockHoldings(symbol)>0) {
                    user.updateUserHolding(symbol, tmpHoldings.get(symbol));
                    int value = tmpHoldings.get(symbol) * stocks.get(symbol).getSharePrice().intValue();
                    amount += value;
                    user.addToUserBalance(value);
                }
                else
                {
                    int quantity = tmpHoldings.get(symbol);
                    Stock stock = stocks.get(symbol);
                    int value = quantity*stock.getSharePrice().intValue();
                    user.addNewStock(stock,quantity,value);
                    amount+=value;
                }
            }
            user.getActions().add(new UserAction("XML file load",LocalDateTime.now(),amount,pre,(pre+amount)));

        } catch (JAXBException e) {
            throw new JAXBException("JAXB Exception detected.");
        }/* catch (FileNotFoundException e) {
            throw new FileNotFoundException("There is no such a XML file.");
        }*/
    }

    /**
     * A method that checks if a file is an XML file.
     *
     * @param path a path to the file that we would like to check.
     * @return returns true if it's a XML file, and false otherwise.
     */
    private boolean isXMLFile(String path) {
        String extension = "";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i + 1);
        }
        if (extension.toLowerCase().equals("xml"))
            return true;
        else
            return false;
    }

    /**
     * A method that deserialize the XML file that contains the system's data.
     *
     * @param in        an FileInputStream that is contacted to the XML file.
     * @param tmpStocks a temporary MultiKeyMap of system's stocks (prevents a deletion of the previous system data in case there will be a failure).
     * @throws JAXBException will be thrown in case there is a problem in the process of JAXB.
     */
    private void deserializeFrom(InputStream in, MultiKeyMap<String, Stock> tmpStocks, Map<String,Integer> tmpHoldings) throws JAXBException, IllegalArgumentException, InputMismatchException, DateTimeException {
        synchronized (lock2) {
            JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
            Unmarshaller u = jc.createUnmarshaller();
            RizpaStockExchangeDescriptor rse = (RizpaStockExchangeDescriptor) u.unmarshal(in); //Converts the XML file content into an instance of the generated class.
            List<RseStock> rseStocks = rse.getRseStocks().getRseStock();                       //gets a list of all the stocks
            List<RseItem> rseHoldings = rse.getRseHoldings().getRseItem();
            for (RseStock s : rseStocks){                                                      //casts each generated stock class instance to the system's stock class and inserts them into the MultiKeyMap.
                Stock tmp = castRseStockToStock(s, tmpStocks);                                 //casts the generated stock class to system's stock class.
                tmpStocks.put(tmp.getSymbol(), tmp.getCompanyName(), tmp);                     //inserts the stock into the MultiKeyMap.
            }
            for (RseItem i : rseHoldings){
                if(!(tmpStocks.containsKey(i.getSymbol())||stocks.containsKey(i.getSymbol())))
                    throw new IllegalArgumentException("The file loading failed!, since the " + i.getSymbol() + " symbol doesn't exist!");
                if(i.getQuantity()<1)
                    throw new IllegalArgumentException("The file loading failed!, since the " + i.getQuantity() + " isn't a valid quantity!");
                tmpHoldings.put(i.getSymbol(), i.getQuantity());
            }
        }
    }

    /**
     * A method that casts a generated stock class instance to a system's stock class instance.
     *
     * @param rs  a generated stock class instance.
     * @param map a MultiKeyMap of the existing stocks (required in order to check prevent a creation of an exiting stock.
     * @return a stock instance.
     * @throws IllegalArgumentException will be thrown in case that the stock is invalid application wise.
     */
    private Stock castRseStockToStock(RseStock rs, MultiKeyMap<String, Stock> map) throws IllegalArgumentException, InputMismatchException {
        String symbol = rs.getRseSymbol();
        if (map.containsKey(symbol))             //checks if there is a stock with this symbol.
            throw new IllegalArgumentException("The file loading failed!, since the " + symbol + " symbol is already exist, the stock's symbol should be unique!");

        String company = rs.getRseCompanyName();
        if (map.containsKey(company))            //checks if there is a stock that is owned by this company.
            throw new IllegalArgumentException("The file loading failed!, since the " + company + " company is already exist, each company should have a single stock!");

        float price = rs.getRsePrice();
        if (price <= 0)                            //checks that the stock price is valid (positive real number).
            throw new IllegalArgumentException("The file loading failed!, since the price (" + price + ") is not a positive number, stock's price should be a real positive number.");

        Queue<TradeCommand> buyCommands = null;
        List<RseTradeCommand> rseBuyCommands = null;
        if (rs.getRseBuyCommands() != null) {
            buyCommands = new PriorityQueue<>(1, Collections.reverseOrder());
            rseBuyCommands = rs.getRseBuyCommands();
            for (RseTradeCommand tc : rseBuyCommands)
                buyCommands.add(new TradeCommand(TradeCommand.Direction.valueOf(tc.getRseDir()), TradeCommand.CommandType.valueOf(tc.getRseType()), tc.getRseQuantity(), tc.getRsePrice(), tc.getRseSymbol(), LocalDateTime.parse(tc.getRseDateTime()), users.getUser(tc.getRseUser())));
        }
        Queue<TradeCommand> sellCommands = null;
        List<RseTradeCommand> rseSellCommands = null;
        if (rs.getRseSellCommands() != null) {
            sellCommands = new PriorityQueue<>(1);
            rseSellCommands = rs.getRseSellCommands();
            for (RseTradeCommand tc : rseSellCommands)
                sellCommands.add(new TradeCommand(TradeCommand.Direction.valueOf(tc.getRseDir()), TradeCommand.CommandType.valueOf(tc.getRseType()), tc.getRseQuantity(), tc.getRsePrice(), tc.getRseSymbol(), LocalDateTime.parse(tc.getRseDateTime()), users.getUser(tc.getRseUser())));
        }
        List<Transaction> transactions = null;
        List<RseTransactions> rseTransactions = null;
        if (rs.getRseTransactions() != null) {
            transactions = new LinkedList<>();
            rseTransactions = rs.getRseTransactions();
            for (RseTransactions tran : rseTransactions)
                transactions.add(new Transaction(tran.getRseQuantity(), tran.getRsePrice(), LocalDateTime.parse(tran.getRseDateTime()), users.getUser(tran.getRseBuyer()), users.getUser(tran.getRseSeller()), tran.getRseStock()));
        }
        return new Stock(company, symbol, price, buyCommands, sellCommands, transactions);
    }

    /**
     * A method makes a list that acts as an output buffer of the existing stocks in the system.
     *
     * @return a list of the existing stocks in systems.
     */
    public List<StockDT> showAllStocks() // second option in the main menu
    {
        List<StockDT> res = new ArrayList<>(stocksCount()); // make list in the size of the current number of stocks
        for (Stock cs : getListOfStocks()) {
            res.add(new StockDT(cs.getCompanyName(), cs.getSymbol(), cs.getSharePrice(), cs.getStockTransactions(), cs.getTransactionsTurnOver(), cs.getBuyCommandsList(), cs.getSellCommandsList()));
        }
        return res;
    }

    /**
     * A method that makes a data transfer stock instance that acts like an output buffer of the stock in the system
     *
     * @param companySymbol a symbol of the stock that we would like to show.
     * @return an instance of data transfer type of the stock class.
     * @throws InputMismatchException will be thrown in case there isn't a stock with this symbol.
     */
    public StockDT showStock(String companySymbol) throws InputMismatchException {
        Stock s = getSingleStock(companySymbol);
        return new StockDT(s.getCompanyName(), s.getSymbol(), s.getSharePrice(), s.getStockTransactions(), s.getTransactionsTurnOver(), s.getBuyCommandsList(), s.getSellCommandsList());
    }

    /**
     * A method that adds a trade command to a stock.
     *
     * @param companySymbol a symbol of the stock that we would like to perform on it the command.
     * @param dir           the direction of the command (buy/sell).
     * @param command       the type of the trade command.
     * @param quantity      the quantity of stocks that we would like to trade.
     * @param wantedPrice   the desired price per stock.
     * @return a status message about the command addition (sometimes will be executed immediate and sometimes not)
     * @throws InputMismatchException will be thrown in case there isn't a stock will this symbol.
     */
    @Override
    public String addTradeCommand(String companySymbol, TradeCommand.Direction dir, TradeCommand.CommandType command, int quantity, float wantedPrice, User user) throws InputMismatchException {
        if (command != TradeCommand.CommandType.MKT && wantedPrice <= 0)
            throw new IllegalArgumentException("Command has been canceled. \n" + " Price must be a positive number.");
        if (quantity <= 0)
            throw new IllegalArgumentException("Command has been canceled. \n" + " Shares quantity must be a positive number.");
        Stock stock = getSingleStock(companySymbol);
        if (command != TradeCommand.CommandType.MKT)                             //in MKT command there isn't a need to ask the user for a desired price.
            return stock.addTradeCommand(dir, command, quantity, wantedPrice, user);
        else
            return stock.addTradeCommand(dir, command, quantity, stock.getSharePrice(), user);
    }

    /**
     * A method that creates a list that acts as an output buffer of all the trade commands in the system.
     *
     * @return a list of all the trade commands in the system.
     */
    public List<StockDT> showAllCommands() { // fifth option - for future use. for now just return the same as showAllStocks()
        return showAllStocks(); // same data is in both of them
    }


    public Map<LocalDateTime, Transaction> showAllTransactions() {
        Map<LocalDateTime, Transaction> transactions = new TreeMap<>();
        for (Stock s : stocks.values())
            for (Transaction tr : s.getStockTransactions())
                transactions.put(tr.getDateStamp(), tr);
        return transactions;
    }

    /**
     * A method that checks if there is a stock with this symbol.
     *
     * @param symbol the symbol that we would like to check.
     * @return true if it excites and false otherwise.
     */
    public boolean isSymbolExists(String symbol) {
        if (stocks.containsKey(symbol))
            return true;
        return false;
    }

    /**
     * A method that checks if there is a stock that is owned by this company.
     *
     * @param name a name of the company.
     * @return true if it excites and false otherwise.
     */
    public boolean isCompanyNameExists(String name) {
        if (stocks.containsKey(name))
            return true;
        return false;
    }
    public MultiKeyMap<String, Stock> getStocks() {
        return stocks;
    }

    public void setStocks(MultiKeyMap<String, Stock> stocks) {
        this.stocks = stocks;
    }
    public String getJaxbXmlPackageName() {
        return JAXB_XML_PACKAGE_NAME;
    }
}



