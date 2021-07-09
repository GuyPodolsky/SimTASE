package rse.servlets;

import engine.logic.Engine;
import engine.users.User;
import rse.logger.Logger;
import sun.rmi.runtime.Log;

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
        if(session == null){
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User must enter the system first.");
            return;
        }
        if(Boolean.parseBoolean(session.getAttribute("is_admin").toString())){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Admin is unauthorized to add stocks to the system");
            return;
        }

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
            Logger.getServerLogger().post(e.getMessage());
            response.setHeader("errorMessage",e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request,response);
    }
    }
