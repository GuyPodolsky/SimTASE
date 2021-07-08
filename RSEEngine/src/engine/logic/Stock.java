package engine.logic;

import engine.dto.TradeCommandDT;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import engine.users.User;
import engine.users.UserHoldings;

import java.util.*;

/**
 * A class that defines a stock.
 */
public class Stock {
    /**
     * All the variables that defines a stock.
     */
    private final String companyName;
    private final String symbol;
    private FloatProperty sharePrice; // NEED TO BE UPDATED
    private List<Transaction> stockTransactions; // Transactions that already been done (to calculate the full value of the transactions)
    private Queue <TradeCommand> buyCommands;
    private Queue <TradeCommand> sellCommands;

    /**
     * A ctor of stock instance.
     * @param companyName the name of the company that holds the stock.
     * @param symbol the symbol of the stock.
     * @param startPrice the initial price of the stock.
     * @throws InputMismatchException will be thrown when one of the variables from above isn't valid.
     */
    Stock(String companyName, String symbol, float startPrice) throws InputMismatchException {
        if (!symbolCheck(symbol))   //checks if the symbol is valid.
            throw new InputMismatchException("Invalid symbol, use upper letters only!");
        else if(!(startPrice>=0))   //checks if the start price is valid.
            throw new InputMismatchException("Invalid stock's start price value!, should be a positive real number");
        else {
            this.companyName = companyName;
            this.symbol = symbol;
            this.sharePrice = new SimpleFloatProperty(startPrice);
            //this.sharePrice = startPrice;
            stockTransactions = new LinkedList<>();
            buyCommands = new PriorityQueue<>(1, Collections.reverseOrder()); //Max Queue
            sellCommands = new PriorityQueue<>(1);                            //Min Queue
        }
    }

    /**
     * A ctor of stock instance. (for load of a save)
     * @param companyName the name of the company that holds the stock.
     * @param symbol the symbol of the stock.
     * @param startPrice the initial price of the stock.
     * @param buyCommands max queue of buy trade commands.
     * @param sellCommands min queue of sell trade commands.
     * @param transactions list of the made transactions.
     * @throws InputMismatchException will be thrown when one of the variables from above isn't valid.
     */
    public Stock(String companyName, String symbol, float startPrice,Queue<TradeCommand> buyCommands,Queue<TradeCommand> sellCommands, List<Transaction> transactions) throws InputMismatchException {
        if (!symbolCheck(symbol))   //checks if the symbol is valid.
            throw new InputMismatchException("Invalid symbol, use upper letters only!");
        else if(!(startPrice>=0))   //checks if the start price is valid.
            throw new InputMismatchException("Invalid stock's start price value!, should be a positive real number");
        else {
            this.companyName = companyName;
            this.symbol = symbol;
            this.sharePrice = new SimpleFloatProperty(startPrice);
            //this.sharePrice = startPrice;
            this.stockTransactions = transactions==null ? new LinkedList<>():transactions;
            this.buyCommands = buyCommands==null ? new PriorityQueue<>(1,Comparator.reverseOrder()):buyCommands; //Max Queue
            this.sellCommands = sellCommands==null ? new PriorityQueue<>(1):sellCommands;                        //Min Queue
        }
    }

    /**
     * A method that checks the validity of a symbol.
     * @param symbol a symbol of a stock that we want to check.
     * @return true if valid, and false otherwise.
     */
    static public boolean symbolCheck(String symbol) {
        for (char ch : symbol.toCharArray()){
            if(!(Character.isUpperCase(ch) && Character.isLetter(ch)))
                return false;
        }
        return true;
    }

    /**
     * A method that creates a string with all the data about a stock.
     * @return string with all the data about a stock.
     */
    @Override
    public String toString() {
        return (symbol + " (" + companyName + ") shares current price is: " + sharePrice+ ". " +
                "Until now " + stockTransactions.size() + " transactions have been made. " +
                "Total transactions turn over is : " + getTransactionsTurnOver());
    }

    /**
     * A method that equalizes between an instance of a stock and an instance of another object
     * @param o an instance of another object.
     * @return true if they are equal, and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(companyName, stock.companyName);
    }

    /**
     * A method that generates a hash code from the company name that holds the stock.
     * @return a hash code from the company name that holds the stock.
     */
    @Override
    public int hashCode() {
        return Objects.hash(companyName);
    }

    /**
     * A getter of the company name
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * A getter of the share price
     * @return the share price
     */
    public FloatProperty getSharePriceProperty() {
        return sharePrice;
    }

    public Float getSharePrice() {
        return sharePrice.getValue();
    }


    /**
     * A getter of the symbol.
     * @return the symbol of the stock.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * A getter of the number of buy trade commands.
     * @return the number of buy trade commands.
     */
    public int getBuyCommandsCount(){ return buyCommands.size(); }

    /**
     * A getter of the number of sell trade commands.
     * @return the number of sell trade commands.
     */
    public int getSellCommandsCount(){ return sellCommands.size(); }

    /**
     * A getter of the list of buy commands.
     * @return list of buy commands.
     */
    public List<TradeCommandDT> getBuyCommandsList() {
        int size = getBuyCommandsCount();
        List<TradeCommandDT> res = new ArrayList<>(getBuyCommandsCount());
        Queue<TradeCommand> tmp = new PriorityQueue<>(1, Collections.reverseOrder()); //Temporary Queue that will hold the values so we can run over all the
        for(int i=0;i<size;++i) {
            TradeCommand command = buyCommands.poll();
            tmp.add(command);
            res.add(new TradeCommandDT(command.getDirection(),command.getQuantity(),command.getSymbol(), command.getPrice(),command.getDate(),command.getCommandType(),command.getUser()));
        }
        buyCommands = tmp;
        return res;
    }

    /**
     * A getter of the list of sell commands.
     * @return list of sell commands.
     */
    public List<TradeCommandDT> getSellCommandsList() {
        int size = getSellCommandsCount();
        List<TradeCommandDT> res = new ArrayList<>(size);
        Queue<TradeCommand> tmp = new PriorityQueue<>(1); // temporary min queue
        for(int i=0;i<size;++i){
            TradeCommand command = sellCommands.poll();
            tmp.add(command);
            res.add(new TradeCommandDT(command.getDirection(),command.getQuantity(),command.getSymbol(), command.getPrice(),command.getDate(),command.getCommandType(),command.getUser()));
        }
        sellCommands = tmp;
        return res;
    }

    /**
     * A getter of the list of the made transactions.
     * @return
     */
    public List<Transaction> getStockTransactions(){
        return stockTransactions;
    }

    /**
     * A method that calculates the made transactions turnover.
     * @return the made transactions turnover.
     */
    public float getTransactionsTurnOver() {
        float res =0;

        for(Transaction tr : stockTransactions){                               //Go through all the transactions and summing their turn over total
            res+= tr.getTurnover();
        }
        return res;
    }

    /**
     * A method that add a new made transaction to the list of transactions.
     * @param quantity the number of share that were traded.
     * @param price the price per share of the trade.
     * @param buyer
     * @param seller
     */
    public void addTransaction(int quantity, float price, User buyer, User seller){
        stockTransactions.add(0,new Transaction(quantity,price, buyer, seller,this));
        sharePrice.set(price);
        //sharePrice = price; //TODO:!
    }

    /**
     * A method that add an existing transaction to the list of transactions.
     * @param trans the existing made transaction.
     */
    public void addTransaction(Transaction trans){
        stockTransactions.add(0, trans);                                        //Adds to the beginning of the list.
        sharePrice.set(trans.getPrice());
        //sharePrice = trans.getPrice(); TODO:!
    }

    /**
     * A method that adds a new trade command to the relevant list of trade commands.
     * @param dir the trade direction.
     * @param command the type of the trade.
     * @param quantity the number of share to trade with.
     * @param wantedPrice the desired price per share.
     * @return A string with the initial status of the added trade.
     * @throws IllegalArgumentException if the user want to sell more shares of the stock then he really has.
     */
   public String addTradeCommand(TradeCommand.Direction dir, TradeCommand.CommandType command, int quantity, float wantedPrice, User user){
       TradeCommand tr;
       if(dir == TradeCommand.Direction.SELL && quantity> user.getUserFreeHoldings(this.getSymbol()))
           throw new IllegalArgumentException("Command has been canceled. \n"+user.getUserName() + " has only " + user.getUserFreeHoldings(this.getSymbol())+ " shares of this stock left to trade.");
       if(command != TradeCommand.CommandType.MKT)
           tr = new TradeCommand(dir, command, quantity, wantedPrice, this.getSymbol(),user);
        else{
            float mktPrice;
            if(dir == TradeCommand.Direction.BUY)
                mktPrice = getMKTSellPrice(quantity);
            else // sell command
                mktPrice = getMKTBuyPrice(quantity);

            tr = new TradeCommand(dir, command, quantity, mktPrice, this.getSymbol(),user);
        }

        // search here for a matching command
       return commandHandler(tr);
   }

    /**
     * A methods that adds an existing trade command.
     * @param tr an existing trade command.
     */
   public void addTradeCommand(TradeCommand tr){
       // search here for a matching command
       commandHandler(tr);
   }

    /**
     * A method that calculates the buy trade commands turnover.
     * @return the buy trade commands turnover.
     */
    public float getBuyTransTurnover() {
        float res = 0;
        for (TradeCommand tr : buyCommands)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    /**
     *  A method that calculates the sell trade commands turnover.
     * @return the sell trade commands turnover.
     */
    public float getSellTransTurnover() {
        float res = 0;
        for (TradeCommand tr : sellCommands)
            res += (tr.getPrice() * tr.getQuantity());
        return res;
    }

    /**
     * A method that searches for a matching trade command for a LMT trade command.
     * @param priorityFlag the direction of the last inserted trade, needed to set a priority to the price per share of the trade.
     * @return -1 if there isn't any matching commands, otherwise the number of shares that were traded.
     */
    public int searchMatchingLMTCommand(TradeCommand.Direction priorityFlag) {
        if (buyCommands.isEmpty() || sellCommands.isEmpty())
            return -1; // there isn't any matching command

        TradeCommand buy = buyCommands.peek(); // get the first buying trade
        TradeCommand sell = sellCommands.peek(); // get the first selling trade
        boolean flag = true;
        int sumShares = 0;
        boolean needToReturn = false;
        List<TradeCommand> save = new LinkedList<>();

        if (buy.getPrice() < sell.getPrice()&& !buy.getUser().equals(sell.getUser()))
            return -1;
        else if(buy.getUser().equals(sell.getUser())&& priorityFlag.equals(TradeCommand.Direction.BUY)) {
            needToReturn = true;
            while (!sellCommands.isEmpty() && buy.getPrice() >= sell.getPrice() && buy.getUser().equals(sell.getUser()) && priorityFlag.equals(TradeCommand.Direction.BUY)) {
                save.add(sell);
                sellCommands.remove();
                sell = sellCommands.peek();
            }
            if (sellCommands.isEmpty() && (sell == null || buy.getUser().equals(sell.getUser()))){  // the while loop stopped because there is no more commands to check
                while(!save.isEmpty())
                    sellCommands.add(save.remove(0)); // restore all the same user old sell commands
                return -1; // there isn't any matching command of a different user
            }
        }
        else if(buy.getUser().equals(sell.getUser())&& priorityFlag.equals(TradeCommand.Direction.SELL)) {
            needToReturn = true;
            while (!buyCommands.isEmpty() && buy.getPrice() >= sell.getPrice() && buy.getUser().equals(sell.getUser()) && priorityFlag.equals(TradeCommand.Direction.SELL)) {
                save.add(buy);
                buyCommands.remove();
                buy = buyCommands.peek();
            }
            if (buyCommands.isEmpty() && (buy == null || buy.getUser().equals(sell.getUser()))) { // the while loop stopped because there is no more commands to check
                while (!save.isEmpty())
                    buyCommands.add(save.remove(0)); // restore all the same user old buy commands
                return -1; // there isn't any matching command of a different user
            }
        }


        while (flag && !(buy.getPrice() < sell.getPrice())) {
            // get the minimum quantity for the trade
            int finalQuantity = Arrays.stream(new int[]{buy.getQuantity(), sell.getQuantity()}).min().getAsInt();
            float price = (priorityFlag== TradeCommand.Direction.BUY) ? sell.getPrice():buy.getPrice();
            Transaction transaction = new Transaction(finalQuantity, price, buy.getUser(), sell.getUser(),this);
            stockTransactions.add(0, transaction); // add the new transaction
            sharePrice.set(price);
            //sharePrice = price; TODO:!
            sumShares += finalQuantity; // add the shares that been traded until now

            // need to check if there's any leftover shares
            if (buy.getQuantity() - sell.getQuantity() > 0) { // there are more buying shares awaiting
                buy.setQuantity(buy.getQuantity() - sell.getQuantity()); // updating the number of shares to buy
                sell.getUser().removeUserTradeCommand(sell.getDate(), TradeCommand.Direction.SELL,sell);    // remove this trade command from the user sell commands list
                sellCommands.remove(); // removing the first sell command
                sell = sellCommands.peek(); // check if there are more shares to trade
                if (sell == null)
                    flag = false;
            } else if (sell.getQuantity() - buy.getQuantity() > 0) {
                sell.setQuantity(sell.getQuantity() - buy.getQuantity());
                buy.getUser().removeUserTradeCommand(buy.getDate(), TradeCommand.Direction.BUY,buy);     // remove this trade command from the user buy commands list
                buyCommands.remove();
                buy = buyCommands.peek(); // check if there are more shares to trade
                if (buy == null)
                    flag = false;
            } else { // the quantities were equal - remove both of them from the queue
                flag = false;
                buy.getUser().removeUserTradeCommand(buy.getDate(), TradeCommand.Direction.BUY,buy);
                buyCommands.remove();   // remove this trade command from the user buy commands list
                sell.getUser().removeUserTradeCommand(sell.getDate(), TradeCommand.Direction.SELL,sell);
                sellCommands.remove();  // remove this trade command from the user sell commands list

            }
        }
        if(needToReturn&&priorityFlag.equals(TradeCommand.Direction.BUY))
            while(!save.isEmpty())
                sellCommands.add(save.remove(0));
        else if(needToReturn&&priorityFlag.equals(TradeCommand.Direction.SELL))
            while(!save.isEmpty())
                buyCommands.add(save.remove(0));


        return sumShares;
    }

    /**
     * A method that handles with trad commands.
     * @param command the trade command that needed to be handled.
     * @return a string with the status of the trade command execution.
     * @throws IllegalArgumentException will be thrown in case there isn't a matching command type.
     */
    public String commandHandler(TradeCommand command) throws IllegalArgumentException{
        switch (command.getCommandType()) {
            case LMT:
            case MKT:
                return LMTHandler(command);
            case FOK:
                return FOKHandler(command);
            case IOC:
                return IOCHandler(command);
            default:
                throw new IllegalArgumentException("No such command type.");
        }
    }

    private String IOCHandler(TradeCommand command) {
        Queue<TradeCommand> TC = command.getDirection()== TradeCommand.Direction.BUY ? buyCommands:sellCommands;
        int saveQuantity = command.getQuantity();
        int res = searchMatchingLMTCommand(command.getDirection()); // generic method, for both buy\sell commands. returns the number of shares that been traded
        if(res == -1) {
            if(!TC.remove(command)) throw new UnknownError("Couldn't remove from waiting list.");
            return ("There isn't any opposite commands. The command wasn't entered to the waiting list.");
        } else if(res==0) {
            if(!TC.remove(command)) throw new UnknownError("Couldn't remove from waiting list.");
            return ("The existing command rates aren't high enough for trade. The command wasn't entered to the waiting list.");
        } else if(res == saveQuantity) {
            return ("The command was fully executed.");
        } else if(res < saveQuantity) {
            if(!TC.remove(command)) throw new UnknownError("Couldn't remove from waiting list.");
            return ("The command was partly executed. The rest of the " + command.getQuantity() + " shares wasn't entered to the waiting list.");
        }
        throw new UnknownError("Unknown Error Occurred In IOC command Handler: " + res);
    }

    private String FOKHandler(TradeCommand command) {
        int availableShares = 0;
        switch (command.getDirection()) { // this is the only command that always saves the command to the queue
            case BUY:
                availableShares = sellCommands.stream()
                        .filter(c -> c.getPrice()<=command.getPrice())
                        .mapToInt(c -> c.getQuantity())
                        .sum();
                if(availableShares>=command.getQuantity())
                    return LMTHandler(command);
                else
                    return("There isn't a possibility to execute this command right know. The command wasn't executed, and wasn't entered to the waiting");
            case SELL:
                availableShares = buyCommands.stream()
                        .filter(c -> c.getPrice()>=command.getPrice())
                        .mapToInt(c -> c.getQuantity())
                        .sum();
                if(availableShares>=command.getQuantity())
                    return LMTHandler(command);
                else
                    return("There isn't a possibility to execute this command right know. The command wasn't executed, and wasn't entered to the waiting");
        }
        throw new UnknownError("Unknown Error Occurred In FOK command Handler");
    }

    /**
     * A method that handles with LMT trade commands.
     * @param command an LMT trade command.
     * @return the status of the LMT trade command execution.
     */
    private String LMTHandler(TradeCommand command){
        switch (command.getDirection()){ // this is the only command that always saves the command to the queue
            case BUY:
                command.getUser().addUserTradeCommand(command,command.getDirection());
                buyCommands.add(command);
                break;
            case SELL:
                UserHoldings holdings = command.getUser().getUserStocks().get(command.getSymbol());
                holdings.setFreeShares(holdings.getFreeShares()-command.getQuantity());
                command.getUser().addUserTradeCommand(command,command.getDirection());
                sellCommands.add(command);
                break;
        }
        int saveQuantity = command.getQuantity();
        int res = searchMatchingLMTCommand(command.getDirection()); // generic method, for both buy\sell commands. returns the number of shares that been traded
        if(res == -1)
            return("There isn't any opposite commands. The command entered to the waiting " + command.getDirection() + " commands list.");
        else if(res==0)
            return("The existing command rates aren't high enough for trade.The command entered to the waiting " + command.getDirection() + " commands list.");
        else if(res == saveQuantity)
            return("The command was fully executed. ");
        else if(res < saveQuantity)
            return("The command was partly executed. The rest of the "+command.getQuantity() +" shares was entered to the waiting "+command.getDirection() + " commands list.");

        throw new UnknownError("Unknown Error Occurred In LMT command Handler: " + res);
    }

    /**
     * A method that gets the market sell price of a stock.
     * @param quantity the number of shares.
     * @return =the sell market price per share * number of shares.
     */
    public float getMKTSellPrice(int quantity){
        int count =0; // count the number of shares until it get to the wanted quantity
        float savePrice = sharePrice.get();
        //float savePrice=sharePrice; TODO:!
        boolean found = false;
        Queue<TradeCommand> tmp = new PriorityQueue<>(1);
        while(count<quantity && !sellCommands.isEmpty() && !found){
            TradeCommand command = sellCommands.poll();
            tmp.add(command); // saves the command
            savePrice = command.getPrice();
            count+=command.getQuantity();

            if(count >= quantity) // checks if we counted enough shares
                found = true;
        }
        while(!tmp.isEmpty())
            sellCommands.add(tmp.poll());       // enters the commands back to the main priority queue
        return savePrice;                       // returns the highest price between the counted shares
    }

    /**
     * A method that gets the market buy price of a stock.
     * @param quantity the number of shares.
     * @return =the buy market price per share * number of shares.
     */
    public float getMKTBuyPrice(int quantity){
        int count =0; // count the number of shares until it get to the wanted quantity
        float savePrice=sharePrice.get();
        //float savePrice=sharePrice; TODO:!
        boolean found = false;
        Queue<TradeCommand> tmp = new PriorityQueue<>(1,Collections.reverseOrder());
        while(count<quantity && !buyCommands.isEmpty()&& !found){
            TradeCommand command = buyCommands.poll();
            tmp.add(command); // saves the command
            savePrice = command.getPrice();
            count += command.getQuantity();

            if(count >= quantity) // checks if we counted enough shares
                found = true;
        }
        while(!tmp.isEmpty())
            buyCommands.add(tmp.poll());       // enters the commands back to the main priority queue
        return savePrice;              // returns the lowest price between the counted shares
    }

}



