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
        if(session==null){
            Logger.getServerLogger().post("User must enter the system first.");
            response.setHeader("errorMessage","User must enter the system first.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User must enter the system first.");
            return;
        }
        if(Boolean.parseBoolean(session.getAttribute("is_admin").toString())){
            Logger.getServerLogger().post("Admin is unauthorized to add trade command to the system");
            response.setHeader("errorMessage","Admin is unauthorized to add trade command to the system");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Admin is unauthorized to add trade command to the system");
            return;
        }
        else {
            User user = Engine.getInstance().getUsersManager().getUser(session.getAttribute("username").toString());
            if(user==null) {
                Logger.getServerLogger().post("User not found.");
                response.setHeader("errorMessage","User not found.");
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not found.");
                return;
            }
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

