package engine.logic;

import com.sun.javaws.exceptions.InvalidArgumentException;
import engine.dto.StockDT;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import engine.users.User;
import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * An interface that defines all the required methods to a trade engine.
 */
public interface Trader {

 /**
  * A method that creates a list of all the stocks in the system.
  * @return list of all the stocks in the system.
  */
 List<StockDT> showAllStocks();

 /**
  * A method that creates an output buffer of a specific stock.
  * @param symbol a symbol of a stock.
  * @return output buffer of a specific stock.
  */
 StockDT showStock(String symbol);

 /**
  * A method loads a system instance from a XML file.
  * @param path the path to the XML file.
  * @throws FileNotFoundException will be thrown in case the file won't be found.
  * @throws JAXBException will be thrown in case JAXB will fail to read the data from the file.
  */
 void uploadDataFromFile(String path, DoubleProperty doubleProperty, StringProperty stringProperty) throws FileNotFoundException, JAXBException, InvalidArgumentException;

 /**
  * A method that creates a list of all stocks output buffers in order to show late all the trade commands in the system.
  * @return list of all stocks output buffers.
  */
 List<StockDT> showAllCommands();

 /**
  * A method that adds a new trade command to a specific stock.
  * @param symbol a symbol of a stock.
  * @param dir the direction of the trade command (buy/sell).
  * @param command the command type.
  * @param quantity the number of stock to trade with.
  * @param wantedPrice the desired price per share.
  * @return a string with the initial status of the stock (Example: the trade command was executed successfully).
  */
 String addTradeCommand(String symbol, TradeCommand.direction dir, TradeCommand.commandType command, int quantity, float wantedPrice, User user);

}
