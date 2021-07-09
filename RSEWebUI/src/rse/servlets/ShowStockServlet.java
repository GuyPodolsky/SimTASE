package rse.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    //private Gson gson = new Gson();
    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    /*
    This method returns json object in text that describing a single stock.
    Every this we want to display needs to be updated in here as well.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false);
        if(session == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User must enter the system first.");
            return;
        }

        try {
            String username = String.valueOf(session.getAttribute("username"));
            User user = Engine.getInstance().getUsersManager().getUser(username);
            String symbol = request.getParameter("symbol");
            StockDT stockDT = Engine.getInstance().showStock(symbol);

            //response.setContentType("text/json");
            PrintWriter out = response.getWriter();
            int userHoldings = user.getUserStockHoldings(symbol);
            String resStock = gson.toJson(stockDT);
            String stockTrans = gson.toJson(stockDT.getTransactions());
            String stockBuyCommands = gson.toJson(stockDT.getBuysCommands());
            String stockSellCommands = gson.toJson(stockDT.getSellsCommands());

            String res = "{\"username\":\""+username+"\",\"userHoldings\":\""+userHoldings+"\",\"stockDetails\":"+resStock+
                    ",\"stockTransactions\":"+stockTrans+",\"stockBuyCommands\":"+stockBuyCommands+",\"stockSellCommands\":"+stockSellCommands+"}";
            Logger.getServerLogger().post(res);
            out.println(res);
            out.flush();
        } catch (IllegalArgumentException | IllegalStateException e){
            Logger.getServerLogger().post(e.getMessage());
            response.setHeader("errorMessage",e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
        } catch (Exception e){
            Logger.getServerLogger().post(e.getMessage());
            response.setHeader("errorMessage",e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,e.getMessage());
        }


    }
}
