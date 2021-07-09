package engine.logic;

import com.google.gson.annotations.Expose;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import engine.users.User;
import engine.users.UserHoldings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Objects;

/**
 * A class that defines a made transaction.
 */
public class Transaction {

    /**
     * A format of showing the date and time of when the transaction was made.
     */
    final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    /**
     * All the variables that defines a made transaction
     */

    final private LocalDateTime dateStamp;
    @Expose
    final private String formattedTimestamp;
    @Expose
    final private String symbol;
    @Expose
    final private int quantity;
    @Expose
    final private float turnover; // =quantity * price
    @Expose
    final private float price; // save the price the shares really sold for
    final private User buyer;
    final private User seller;
    @Expose
    final private String buyerName;
    @Expose
    final private String sellerName;

    public String getSymbol() { return symbol; }

    public User getBuyer() {
        return buyer;
    }

    public User getSeller() {
        return seller;
    }

    /**
     * A ctor of a transaction instance.
     * @param quantity the quantity of share that were traded
     * @param soldPrice the per share price of the trade.
     * @param buyer
     * @param seller
     */
    public Transaction(int quantity, float soldPrice, User buyer, User seller,Stock stock){

        if(quantity<=0)
            throw new InputMismatchException("Invalid number of traded shares, should be a positive integer.");
        else if(soldPrice<=0)
            throw new InputMismatchException("Invalid price per share, should be a positive real number.");
        else {
            dateStamp = LocalDateTime.now();
            formattedTimestamp = dateStamp.format(dateTimeFormat);
            this.symbol = stock.getSymbol();
            this.quantity = quantity;
            this.price = soldPrice;
            Engine.getInstance().getStocks().get(symbol).getSharePriceProperty().set(soldPrice);
            this.turnover = quantity * soldPrice;
            this.buyer = buyer;
            this.buyerName = buyer.getUserName();
            this.seller = seller;
            this.sellerName = seller.getUserName();

            this.buyer.addUserTransaction(this);
            this.seller.addUserTransaction(this);

            if(seller.getUserStockHoldings(stock.getSymbol()) == quantity)
                seller.getUserStocks().remove(stock.getSymbol());
            else
                seller.getUserStocks().get(stock.getSymbol()).setQuantity( seller.getUserStocks().get(stock.getSymbol()).getQuantity()-quantity);

            if(!buyer.getUserStocks().containsKey(stock.getSymbol())) {
                buyer.getUserStocks().put(stock.getSymbol(), new UserHoldings(stock.getSymbol(), stock, quantity));
                buyer.setTotalHoldingsValue(LocalDateTime.now(),buyer.getTotalHoldingsValue()+turnover);
                buyer.getUserStocks().get(stock.getSymbol()).getTotalHoldProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        buyer.setTotalHoldingsValue(LocalDateTime.now(),buyer.getTotalHoldingsValue()-oldValue.floatValue()+newValue.floatValue());
                    }
                });
            }
            else{
                    UserHoldings holdings = buyer.getUserStocks().get(stock.getSymbol());
                    holdings.setQuantity(holdings.getQuantity()+quantity);
                    holdings.setFreeShares(holdings.getFreeShares()+quantity);
            }



        }
    }

    public Transaction(int quantity, float soldPrice, LocalDateTime datetimeStamp, User buyer, User seller, String stock) {

        if(quantity<=0)
            throw new InputMismatchException("Invalid number of traded shares, should be a positive integer.");
        else if(soldPrice<=0)
            throw new InputMismatchException("Invalid price per share, should be a positive real number.");
        else {
            dateStamp = datetimeStamp;
            formattedTimestamp = dateStamp.format(dateTimeFormat);
            this.symbol = stock;
            this.quantity = quantity;
            this.price = soldPrice;
            this.turnover = quantity * soldPrice;
            this.buyer = buyer;
            this.buyerName = buyer.getUserName();
            this.seller = seller;
            this.sellerName= seller.getUserName();

            this.buyer.addUserTransaction(this);
            this.seller.addUserTransaction(this);
            buyer.setTotalHoldingsValue(LocalDateTime.now(),buyer.getTotalHoldingsValue()+turnover);
            seller.setTotalHoldingsValue( LocalDateTime.now(),seller.getTotalHoldingsValue()-turnover);
        }
    }

    /**
     * A method that creates a string with all the data about the transaction.
     * @return all the data about the transaction.
     */
    @Override
    public String toString() {
        return "Engine.Transaction made at "+ dateStamp.format(dateTimeFormat) + " of " + quantity +" shares at the price of " + price +" for each. ";
    }

    /**
     * A getter of the made transaction turnover.
     * @return
     */
    public float getTurnover() {
        return turnover;
    }

    /**
     * A getter of the price per share of the made trade.
     * @return
     */
    public float getPrice() { return price; }

    /**
     * A methods that equalize an instance of transaction with an instance of another object.
     * @param o an instance of anther object.
     * @return true if they are equal, and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return quantity == that.quantity && Float.compare(that.price, price) == 0 && dateStamp.equals(that.dateStamp);
    }

    /**
     * A method that generates a hash code from the instance variables.
     * @return a hash code from the instance variables.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dateStamp, quantity, price);
    }

    public LocalDateTime getDateStamp() {
        return dateStamp;
    }

    public int getQuantity() {
        return quantity;
    }

    public static DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormat;
    }

    public String getFormattedTimestamp() {
        return formattedTimestamp;
    }
}
