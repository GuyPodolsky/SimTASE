package rse.servlets;

import com.google.gson.Gson;
import engine.logic.Engine;
import rse.logger.Logger;

import javax.jms.Session;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

public class AddToUserBalance extends HttpServlet {
    Gson gson = new Gson();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session == null)
            throw new IllegalStateException("User must enter to the system first.");
        String name = String.valueOf(session.getAttribute("username"));
        int addition = Integer.parseInt(request.getParameter("quantity"));
        Logger.getServerLogger().post(String.valueOf(addition));
        Engine.getInstance().getUsersManager().getUser(name).addToUserBalance(addition);
        Logger.getServerLogger().post("{ \"massage\":\"Added "+addition +" to "+name+" balance.\"");
        Logger.getServerLogger().post("\"addition\":"+ Engine.getInstance().getUsersManager().getUser(name).getUserBalance()+"}");

        response.getWriter().println("{ \"massage\":\"Added "+addition +" to "+name+" balance.\",");
        response.getWriter().println("\"addition\":"+ Engine.getInstance().getUsersManager().getUser(name).getUserBalance()+"}");



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        doPost(request,response);
    }
}
