package rse.servlets;

import com.google.gson.Gson;
import engine.dto.StockDT;
import engine.logic.Engine;
import rse.logger.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ShowAllStocksServlet extends HttpServlet {

    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session == null)         // if the user didn't sign correctly to the system
            throw new IllegalAccessError("User must sign in to the system before using RSE.");



       //int systemStocks = Engine.getInstance().stocksCount();

       List<StockDT> stocks = Engine.getInstance().showAllStocks();
       //List<StockDT> newStocks = stocks.subList(stocksNum,systemStocks);
       String res = this.gson.toJson(stocks);
       PrintWriter out = response.getWriter();
       Logger.getServerLogger().post(res);
       out.println(res);
       out.flush();



    }
}
