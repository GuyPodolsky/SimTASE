package rse.servlets;

import engine.logic.Engine;
import engine.users.User;
import rse.logger.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AddNewStockServlet extends HttpServlet {
    private Object lock = new Object();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession(false);
        if(session == null)
            throw new IllegalStateException("User must enter the system first.");
        try {
            synchronized (lock) {
                String name = String.valueOf(session.getAttribute("username"));
                String company = request.getParameter("company name");
                String symbol = request.getParameter("company symbol");
                int quantity = Integer.parseInt(request.getParameter("shares quantity"));
                int value = Integer.parseInt(request.getParameter("company estimated value"));
                Engine.getInstance().addNewStock(company, symbol, quantity, value, name);
                Logger.getServerLogger().post("Added new stock " + symbol + " to the system.");
                response.getWriter().println("Added new stock " + symbol + " to the system.");
            }
        }
        catch (Exception e){
            response.sendError(400,e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request,response);
    }
    }
