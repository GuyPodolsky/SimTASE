package engine.users;

import engine.logic.Stock;
import engine.logic.TradeCommand;
import engine.logic.Transaction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.management.openmbean.InvalidKeyException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class saves all the data about a specific user in the system
 */
public class User {
    private final String userName;
    private boolean isAdmin;
    private float userBalance;
    private Map<String, UserHoldings> userStocks;
    private SortedMap<LocalDateTime,Float> totalHoldingsValue;
    private List<Transaction> userTransactions;
    private Map<LocalDateTime, TradeCommand> userBuyCommands;
    private Map<LocalDateTime,TradeCommand> userSellCommands;
    private List<UserAction> actions;

//Constructors:

    User(String _username,boolean _isAdmin){
        this.userName = _username;
        if(!_isAdmin){                              // if the user is admin - he can't trade at all it the system
            this.userStocks = new TreeMap<>();
            totalHoldingsValue = new TreeMap<>();
            totalHoldingsValue.put(LocalDateTime.now(),Float.valueOf(0));
            this.userTransactions = new LinkedList<>();
            this.userBuyCommands = new TreeMap<>();
            this.userSellCommands = new TreeMap<>();
            this.actions = new ArrayList<>();
            updateWorth();
        }
        this.isAdmin =_isAdmin;
        userBalance =0;
    }

    public User(String name,Map<String, UserHoldings> stocks){
        this.userName = name;
        isAdmin = false;                // because we send stocks to the ctor, the user can't be admin
        userBalance =0;
        this.userTransactions = new LinkedList<>();
        userBuyCommands = new TreeMap<>();
        userSellCommands = new TreeMap<>();
        this.actions = new ArrayList<>();
        if(!stocks.equals(null))
            this.userStocks = stocks;
        else
            this.userStocks = new TreeMap<>();
        totalHoldingsValue = new TreeMap<>();
        float initTotalWorth = 0;
        for(UserHoldings hold: stocks.values())
            initTotalWorth += hold.getTotalHold();
        totalHoldingsValue.put(LocalDateTime.now(),initTotalWorth);
        updateWorth();
    }


//Getters:

    public String getUserName() {
        return userName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public float getUserBalance() {
        return userBalance;
    }

    public Map<String, UserHoldings> getUserStocks() {
        return userStocks;
    }

    public SortedMap<LocalDateTime,Float> getWorthHistory(){
        return this.totalHoldingsValue;
    }

    public List<Transaction> getUserTransactions() {
        return userTransactions;
    }

    public Map<LocalDateTime, TradeCommand> getUserBuyCommands() {
        return userBuyCommands;
    }

    public Map<LocalDateTime, TradeCommand> getUserSellCommands() {
        return userSellCommands;
    }

    public List<UserAction> getActions() {
        return actions;
    }

    //Special getters:

    public float getTotalHoldingsValue() {
        return totalHoldingsValue.get(totalHoldingsValue.lastKey());
    }

    public int getUserStockHoldings(Stock stock){
        if(!userStocks.containsKey(stock.getSymbol()))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stock.getSymbol());
        return userStocks.get(stock.getSymbol()).getQuantity();
    }

    public int getUserStockHoldings(String stockSymbol){
        if(!userStocks.containsKey(stockSymbol))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stockSymbol);
        return userStocks.get(stockSymbol).getQuantity();
    }

    public int getUserFreeHoldings(Stock stock){
        if(!userStocks.containsKey(stock.getSymbol()))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stock.getSymbol());
        return userStocks.get(stock.getSymbol()).getFreeShares();
    }

    public int getUserFreeHoldings(String stockSymbol){
        if(!userStocks.containsKey(stockSymbol))
            throw new InvalidKeyException("The user "+ userName +" don't have shares of the stock " + stockSymbol);
        return userStocks.get(stockSymbol).getFreeShares();
    }

//Setters:

    public void setUserBalance(float userBalance) {
        this.userBalance = userBalance;
    }

    public void addUserTradeCommand(TradeCommand command,TradeCommand.direction dir){
        if(isAdmin)
            throw new IllegalAccessError("Admin can't add trade commands.");
        if(dir == TradeCommand.direction.SELL)
            userSellCommands.put(command.getDate(),command);
        else
            userBuyCommands.put(command.getDate(),command);
    }

    public void removeUserTradeCommand(LocalDateTime time, TradeCommand.direction dir, TradeCommand command){
        if(dir == TradeCommand.direction.SELL)
            userSellCommands.remove(time,command);
        else
            userBuyCommands.remove(time,command);
    }

    public void addUserTransaction(Transaction transaction){
        if(transaction.getSeller().getUserName().equals(userName)) {      // if the user is the seller - we need to add the turnover to his balance
            float pre = userBalance;
            userBalance += transaction.getTurnover();
            actions.add(new UserAction(actionType.SELL,transaction.getDateStamp().toString(),transaction.getTurnover(),pre, userBalance));
        }
        else {        // else - the user is the buyer and we need to reduce the turnover from the user balance
            float pre = userBalance;
            userBalance -= transaction.getTurnover();
            actions.add(new UserAction(actionType.BUY,transaction.getDateStamp().toString(),-transaction.getTurnover(), pre,userBalance));
        }
        userTransactions.add(transaction);
    }

    public void setTotalHoldingsValue(LocalDateTime timestamp,float newValue) {
        totalHoldingsValue.put(timestamp,newValue);
    }

    private void updateWorth(){
        for(UserHoldings hold: userStocks.values())
        {
            hold.getTotalHoldProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    setTotalHoldingsValue(LocalDateTime.now(),getTotalHoldingsValue()-oldValue.floatValue()+newValue.floatValue());
                }
            });
        }
    }

    public void addToUserBalance(float addition){
        LocalDateTime dateStamp = LocalDateTime.now();
        String formattedTimestamp = dateStamp.format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        float pre = userBalance;
        userBalance+=addition;
        actions.add(new UserAction(actionType.ADD,formattedTimestamp,addition, pre,userBalance));

    }

    public void addNewStock(Stock stock, int sharesQuantity){
/*        LocalDateTime dateStamp = LocalDateTime.now();
        String formattedTimestamp = dateStamp.format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
        float pre = userBalance;
        userBalance+=totalValue;*/
        UserHoldings holdings = new UserHoldings(stock.getSymbol(), stock, sharesQuantity);
        userStocks.put(stock.getSymbol(), holdings);
        //updateWorth();    // todo: ask guy if i need to call this


    }

//Overrides:

    @Override
    public String toString() {
        return userName;
    }

}
