package rse.servlets;

import engine.dto.StockDT;
import engine.dto.TradeCommandDT;
import engine.logic.Engine;
import engine.logic.Transaction;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ShowStockServlet extends HttpServlet {

    final private String StockViewPagePath = "/../../../pages/stockview/stockview.html";


    /*
    This method returns json object in text that describing a single stock.
    Every this we want to display needs to be updated in here as well.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        StockDT stockDT = Engine.getInstance().showStock(request.getParameter("symbol"));

        response.setContentType("text/json");
        PrintWriter out = response.getWriter();

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
        out.println("]}");

    }
}
