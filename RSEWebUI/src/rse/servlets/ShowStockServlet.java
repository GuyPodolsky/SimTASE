package rse.servlets;

import com.google.gson.Gson;
import engine.dto.StockDT;
import engine.dto.TradeCommandDT;
import engine.logic.Engine;
import engine.logic.Transaction;
import engine.users.User;
import rse.logger.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ShowStockServlet extends HttpServlet {

    final private String StockViewPagePath = "/../../../pages/stockview/stockview.html";
    private Gson gson = new Gson();

    /*
    This method returns json object in text that describing a single stock.
    Every this we want to display needs to be updated in here as well.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false);
        if(session == null)
            throw new IllegalStateException("User must enter the system first.");
        String username = String.valueOf(session.getAttribute("username"));
        User user = Engine.getInstance().getUsersManager().getUser(username);
        String symbol = request.getParameter("symbol");
        StockDT stockDT = Engine.getInstance().showStock(symbol);
        try {
            //response.setContentType("text/json");
            PrintWriter out = response.getWriter();
            int userHoldings = user.getUserStockHoldings(symbol);
            String resStock = gson.toJson(stockDT);
            String res = "{username:"+username+",userHoldings:"+userHoldings+",stock:"+resStock+"}";
            Logger.getServerLogger().post(res);
            out.println(res);
            out.flush();
        } catch (Exception e){
            response.sendError(400,e.getMessage());
        }
/*
        // insert the basic data about the stock
        out.println("{symbol:"+stockDT.getSymbol()+", companyName:" +stockDT.getCompanyName()+
                ", sharePrice:"+stockDT.getSharePrice()+", sharesQuantity:" +stockDT.getQuantity()+
                ", transactionsTurnOver:" +stockDT.getTransactionsTurnOver()+", transactions:[");
        // insert the transactions that been made in this stock
        List<Transaction> trans = stockDT.getTransactions();
        for (Transaction tr:trans) {
            out.println("{date:"+tr.getFormattedTimestamp()+
                    ", quantity:" +tr.getQuantity()+
                    ", price:" +tr.getPrice()+
                    ", turnover:"+tr.getTurnover()+
                    ", buyer:"+tr.getBuyer().getUserName()+
                    ", seller:" +tr.getSeller()+"}");
        }
        // insert the buy commands awaiting in this stock
        out.println("], buyCommands:[");
        List<TradeCommandDT> buycmd = stockDT.getBuysCommands();
        for(TradeCommandDT tc:buycmd) {
            out.println("{date:"+tc.getFormattedDateTime()+
                    ", quantity:"+tc.getQuantity()+
                    ", wantedPrice:"+tc.getPrice()+
                    ", turnover:"+tc.getTurnover()+
                    ", commandType:"+tc.getCommandType().toString()+
                    ", user:" +tc.getUser().getUserName()+"}");
        }
        // insert the sell commands awaiting in this stock
        out.println("], sellCommands:[");
        List<TradeCommandDT> sellcmd = stockDT.getSellsCommands();
        for(TradeCommandDT tc:sellcmd) {
            out.println("{date:"+tc.getFormattedDateTime()+
                    ", quantity:"+tc.getQuantity()+
                    ", wantedPrice:"+tc.getPrice()+
                    ", turnover:"+tc.getTurnover()+
                    ", commandType:"+tc.getCommandType().toString()+
                    ", user:" +tc.getUser().getUserName()+"}");
        }
        out.println("]}");*/

    }
}
