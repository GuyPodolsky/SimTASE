package rse.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static Logger serverLogger = new Logger();
    private int logsCount;
    private DateTimeFormatter dtf;
    private Logger(){
        logsCount = 0;
        dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public void post(String message){
        System.out.println("["+dtf.format(LocalDateTime.now())+"] Server Log No."+ logsCount++ +": " + message);
    }

    public static Logger getServerLogger(){
        return serverLogger;
    }
}
