package rse.servlets;

import engine.logic.Engine;
import engine.logic.TradeCommand;
import engine.users.User;
import rse.logger.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Queue;

public class TradeCommandsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String symbol = request.getParameter("symbol");
        TradeCommand.Direction dir = TradeCommand.Direction.getByName(request.getParameter("dir"));
        TradeCommand.CommandType type = TradeCommand.CommandType.getByNum(Integer.parseInt(request.getParameter("type")));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        float price = Float.parseFloat(request.getParameter("price"));
        HttpSession session = request.getSession(false);
        if(session==null)
            response.sendError(603,"User must sign in to the system before using RSE.");
        else {
            User user = Engine.getInstance().getUsersManager().getUser(session.getAttribute("username").toString());
            if(user==null)
                response.sendError(604,"User not found.");
            else {
                Logger.getServerLogger().post(
                        "Symbol:" + symbol + "," +
                        "Direction:" + dir + "," +
                        "Type:" + type + "," +
                        "Quantity:" + quantity + "," +
                        "Price:" + price);
                Engine.getInstance().addTradeCommand(symbol, dir, type, quantity, price,user);
            }
        }
    }
}

