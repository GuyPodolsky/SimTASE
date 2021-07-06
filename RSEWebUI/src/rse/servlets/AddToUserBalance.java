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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session == null)
            throw new IllegalStateException("User must enter to the system first.");
        String name = String.valueOf(session.getAttribute("username"));
        float addition = Float.parseFloat(request.getParameter("quantity"));
        Engine.getInstance().getUsersManager().getUser(name).addToUserBalance(addition);
        String jsonResult = "{\"message\":\"Added " + addition + " to " + name + " balance.\",\"addition\":" + Engine.getInstance().getUsersManager().getUser(name).getUserBalance() + "}";
        Logger.getServerLogger().post(jsonResult);

        response.getWriter().println(jsonResult);



    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        doPost(request,response);
    }
}
