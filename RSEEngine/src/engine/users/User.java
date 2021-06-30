package engine.users;

import engine.logic.Stock;
import engine.logic.TradeCommand;
import engine.logic.Transaction;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.management.openmbean.InvalidKeyException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This class saves all the data about a specific user in the system
 */
public class User {
    private final String userName;
    private boolean isAdmin;
    private int userBalance;
    private Map<String, UserHoldings> userStocks;
    private SortedMap<LocalDateTime,Float> totalHoldingsValue;
    private List<Transaction> userTransactions;
    private Map<LocalDateTime, TradeCommand> userBuyCommands;
    private Map<LocalDateTime,TradeCommand> userSellCommands;


    User(String _username,boolean _isAdmin){
        this.userName = _username;
        this.userStocks = new TreeMap<>();
        totalHoldingsValue = new TreeMap<>();
        totalHoldingsValue.put(LocalDateTime.now(),Float.valueOf(0));
        this.userTransactions = new LinkedList<>();
        this.userBuyCommands = new TreeMap<>();
        this.userSellCommands = new TreeMap<>();
        updateWorth();
        this.isAdmin =_isAdmin;
        userBalance =0;
    }
    public User(String name,Map<String, UserHoldings> stocks){
        this.userName = name;
        userBalance =0;
        this.userTransactions = new LinkedList<>();
        userBuyCommands = new TreeMap<>();
        userSellCommands = new TreeMap<>();
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

    public void addUserTradeCommand(TradeCommand command,TradeCommand.direction dir){
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
        if(transaction.getSeller().getUserName().equals(userName))      // if the user is the seller - we need to add the turnover to his balance
            userBalance+=transaction.getTurnover();
        else        // else - the user is the buyer and we need to reduce the turnover from the user balance
            userBalance-= transaction.getTurnover();
        userTransactions.add(transaction);
    }

    public String getUserName() {
        return userName;
    }


    public float getTotalHoldingsValue() {
        return totalHoldingsValue.get(totalHoldingsValue.lastKey());
    }

    public void setTotalHoldingsValue(LocalDateTime timestamp,float newValue) {
        totalHoldingsValue.put(timestamp,newValue);
    }

    /**
     *
     * @param stock the stock we want to get the user holdings of
     * @return the number of shares the user have of the given stock
     * @throws InvalidKeyException The method throws exception if the user do not hold shares of the desired stock
     */
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



    public Map<LocalDateTime, TradeCommand> getUserBuyCommands() {
        return userBuyCommands;
    }

    public List<Transaction> getUserTransactions() {
        return userTransactions;
    }

    public Map<LocalDateTime, TradeCommand> getUserSellCommands() {
        return userSellCommands;
    }

    public Map<String, UserHoldings> getUserStocks() {
        return userStocks;
    }

    public SortedMap<LocalDateTime,Float> getWorthHistory(){
        return this.totalHoldingsValue;
    }

    @Override
    public String toString() {
        return userName;
    }

    public int getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(int userBalance) {
        this.userBalance = userBalance;
    }
    public void addToUserBalance(int addition){
        userBalance+=addition;
    }
}
