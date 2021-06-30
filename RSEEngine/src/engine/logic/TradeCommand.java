package engine.logic;

import engine.users.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Objects;

/**
 * A class that defines a trade command.
 */
public class TradeCommand implements Comparable<TradeCommand>{

    public User getUser() {
        return user;
    }

    /**
     * A enum of possible trade directions.
     */
    public enum direction {BUY, SELL}

    /**
     * A enum of possible trade command types.
     */
    public enum commandType{LMT,MKT/*TODO:,FOK,IOC*/}

    /**
     * A variable that defines a format of showing the date and time
     */
    private static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    /**
     * The variables that defines a trade command.
     */
    final private direction direction;
    private int quantity;
    final private String symbol;
    private float wantedPrice;
    private LocalDateTime dateTimeStamp;
    final private commandType commandType;
    final private User user;

    /**
     * A ctor of trade command instance.
     * @param dir the direction of the trade command (buy/sell).
     * @param type the type of the trade command (LMT/MKT/FOK/IOC).
     * @param howMany the number of shares to trade with.
     * @param whatPrice the desired price per share.
     * @param symbol the symbol of the stock.
     * @throws InputMismatchException will be thrown in case one of the variables above is invalid. (Example: howMany variable is a negative number)
     */
    public TradeCommand(direction dir, commandType type, int howMany, float whatPrice, String symbol,User usr) throws InputMismatchException {
        if (!(howMany>=0)) {    //checks that the number of shares that we would like to trade with is a positive number.
            throw new InputMismatchException("Invalid quantity value!, should be a positive integer");
        } else if(!(whatPrice>=0)) { //checks that the desired price per share is a positive number.
            throw new InputMismatchException("Invalid price value!, should be a positive real number");
        } else if (!Stock.symbolCheck(symbol)) { //checks the validity of the stock symbol.
            throw new InputMismatchException("Invalid symbol, use upper letters only!");
        } else {
            this.direction = dir;
            this.quantity = howMany;
            this.symbol = symbol;
            this.wantedPrice = whatPrice;
            this.commandType = type;
            this.dateTimeStamp = LocalDateTime.now();
            this.user = usr;
            user.addUserTradeCommand(this,dir);
        }
    }

    public TradeCommand(direction dir, commandType type, int howMany, float whatPrice, String symbol,LocalDateTime dateTimeStamp,User usr) throws InputMismatchException {
        if (!(howMany>=0)) {    //checks that the number of shares that we would like to trade with is a positive number.
            throw new InputMismatchException("Invalid quantity value!, should be a positive integer");
        } else if(!(whatPrice>=0)) { //checks that the desired price per share is a positive number.
            throw new InputMismatchException("Invalid price value!, should be a positive real number");
        } else if (!Stock.symbolCheck(symbol)) { //checks the validity of the stock symbol.
            throw new InputMismatchException("Invalid symbol, use upper letters only!");
        } else {
            this.direction = dir;
            this.quantity = howMany;
            this.symbol = symbol;
            this.wantedPrice = whatPrice;
            this.commandType = type;
            this.dateTimeStamp = dateTimeStamp;
            this.user = usr;
            user.addUserTradeCommand(this,dir);
        }
    }

    /**
     * A getter of the trade direction.
     * @return the trade direction.
     */
    public direction getDirection() { return this.direction; }

    /**
     * A getter of the quantity of share we would like to trade with.
     * @return the quantity of share we would like to trade with.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * A getter of the symbol of the traded stock.
     * @return the symbol of the traded stock.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * A getter of the type of the trade command.
     * @return the type of the trade command.
     */
    public commandType getCommandType() { return commandType;}

    /**
     * A getter of the desired price per share.
     * @return the desired price per share.
     */
    public float getPrice() {return wantedPrice;}

    /**
     * A getter of the time and date of the trade command creation.
     * @return the time and date of the trade command creation.
     */
    public LocalDateTime getDate(){ return dateTimeStamp; }

    /**
     * A setter of the quantity of share we would like to trade with.
     * @param quantity the quantity of shares we would like to trade with.
     * @throws InputMismatchException will be thrown in case the number isn't a positive integer.
     */
    public void setQuantity(int quantity) throws InputMismatchException {
        if(quantity<=0)
            throw new InputMismatchException("The quantity of share to trade with should be a positive integer.");
        this.quantity = quantity;
    }

    /**
     * A method that creates an output of all the data about the trade command.
     * @return an output buffer of all the data about the trade command.
     */
    @Override
    public String toString() {
        return dateTimeStamp.format(dateTimeFormat) + " - This is " + commandType + " " + direction +" command for " +quantity +" "+ symbol +" stocks.";
    }

    /**
     * A method that compares the trade command with another objects.
     * @param o an object that will be compared to the trade command.
     * @return true if they are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradeCommand that = (TradeCommand) o;
        return quantity == that.quantity && Float.compare(that.wantedPrice, wantedPrice) == 0 && symbol.equals(that.symbol) && direction == that.direction && dateTimeStamp.equals(that.dateTimeStamp) && commandType == that.commandType;
    }

    /**
     * A method that generates a hash code from all the variables of the trade command.
     * @return a hash code from all the variables of the trade command.
     */
    @Override
    public int hashCode() {
        return Objects.hash(symbol, direction, quantity, wantedPrice, dateTimeStamp, commandType);
    }

    /**
     * A method that compares two trade commands.
     * @param other another instance of trade command.
     * @return negative number if other > this, positive number if this > other and zero if this == other.
     */
    @Override
    public int compareTo(TradeCommand other) {
        float res = this.wantedPrice - other.wantedPrice; //compares prices per share.
        if(res==0){
            if(other.getDirection() == direction.BUY) //since we save all the trade commands in priority queues (buy commands as maximum and sell commands as minimum)
                                                      //we adjust the result so it will priorities the commands in a right order.
                return (-this.dateTimeStamp.compareTo(other.dateTimeStamp));
            else
                return this.dateTimeStamp.compareTo(other.dateTimeStamp);
        }
        return (int)res;
    }

    public static DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormat;
    }
}
