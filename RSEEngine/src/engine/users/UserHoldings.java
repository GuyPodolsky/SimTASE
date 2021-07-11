package engine.users;

import engine.logic.Stock;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.format.DateTimeFormatter;

public class UserHoldings {

    private final String symbol;
    private final Stock stock;
    private IntegerProperty quantity;
    private int freeShares;
    private FloatProperty totalHold;
    private FloatProperty sharePrice;

    public UserHoldings(String symbol, Stock stock, int quantity) {
        this.symbol = symbol;
        this.stock = stock;
        this.freeShares = quantity;
        this.quantity = new SimpleIntegerProperty();
        this.quantity.set(quantity);
        this.sharePrice = new SimpleFloatProperty();
        this.totalHold = new SimpleFloatProperty();
        this.sharePrice.bind(stock.getSharePriceProperty());
        totalHold.bind(sharePrice.multiply(this.quantity));

    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public float getTotalHold() {
        //totalHold sharePrice.get() * quantity.get();
        return totalHold.get();
    }

    public FloatProperty getTotalHoldProperty() {
        return totalHold;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public String getSymbol() {
        return symbol;
    }

    public Stock getStock() {
        return stock;
    }

    public int getFreeShares() {
        return freeShares;
    }

    public void setFreeShares(int freeShares) {
        this.freeShares = freeShares;
    }

    public void addQuantity(int quantity){
        this.freeShares += quantity;
        this.quantity.add(quantity);
    }
}
