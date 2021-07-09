package engine.dto;

import com.google.gson.annotations.Expose;
import engine.logic.TradeCommand;
import engine.users.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class that defines an output buffer of a trade command instance.
 */
public class TradeCommandDT {

    /**
     * A format of showing the date and time.
     */
    final private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    /**
     * The variables that defines a trade command.
     */
    final private TradeCommand.Direction direction;
    @Expose
    final private int quantity;
    @Expose
    final private String symbol;
    @Expose
    final private float wantedPrice;
    final private LocalDateTime DateTimeStamp;
    @Expose
    final private String formattedDateTime;
    final private TradeCommand.CommandType commandType;
    private User user;
    @Expose
    final private float turnover;

    @Expose
    final private String username;
    @Expose
    final private String dirString;
    @Expose
    final private String typeString;

    /**
     * A ctor of class instance from the needed variables to define a trade command.
     * @param dir the direction of the trade command (sell/buy).
     * @param type the type of the trade command.
     * @param howMany the quantity of share to trade with.
     * @param whatPrice the desired price per share.
     * @param symbol the symbol of the traded stock.
     * @param dateTimeStamp the time and date of the trade command creation.
     */
    public TradeCommandDT(TradeCommand.Direction dir, TradeCommand.CommandType type, int howMany, float whatPrice, String symbol, LocalDateTime dateTimeStamp) {
        this.direction = dir;
        this.dirString = dir.getName();
        this.quantity = howMany;
        this.symbol = symbol;
        this.wantedPrice = whatPrice;
        this.commandType = type;
        this.typeString = type.getName();
        this.DateTimeStamp = dateTimeStamp;
        this.formattedDateTime = dateTimeStamp.format(dateTimeFormat);
        this.user = null;
        this.username = "";
        this.turnover = whatPrice * howMany;
    }

    public TradeCommandDT(TradeCommand.Direction direction, int quantity, String symbol, float wantedPrice, LocalDateTime dateTimeStamp, TradeCommand.CommandType commandType, User user) {
        this.direction = direction;
        this.dirString = direction.getName();
        this.quantity = quantity;
        this.symbol = symbol;
        this.wantedPrice = wantedPrice;
        DateTimeStamp = dateTimeStamp;
        this.formattedDateTime = dateTimeStamp.format(dateTimeFormat);
        this.commandType = commandType;
        this.typeString = commandType.getName();
        this.user = user;
        this.username = user.getUserName();
        this.turnover = wantedPrice*quantity;
    }

    /**
     * A getter of the trade command direction.
     * @return the trade command direction.
     */
    public TradeCommand.Direction getDirection(){
        return this.direction;
    }

    /**
     * A getter of the traded stock symbol.
     * @return the traded stock symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * A getter of the quantity of share to trade with.
     * @return  the quantity of share to trade with.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * A getter of the desired price per share.
     * @return the desired price per share.
     */
    public float getPrice() {
        return wantedPrice;
    }

    /**
     * A getter of the trade command type.
     * @return the trade command type.
     */
    public TradeCommand.CommandType getCommandType() {
        return commandType;
    }

    /**
     * A method that makes a string with all the data about the trade command.
     * @return string with all the data about the trade command.
     */
    public String toString(){
        return DateTimeStamp.format(dateTimeFormat) + " - " + quantity + " stocks for " + wantedPrice + " each. " +
                "Total turn over for command is " + (quantity*wantedPrice);
    }

    public String getFormattedDateTime() {
        return formattedDateTime;
    }

    public LocalDateTime getDateTimeStamp() {
        return DateTimeStamp;
    }

    public float getWantedPrice() {
        return wantedPrice;
    }

    public float getTurnover() {
        return turnover;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
