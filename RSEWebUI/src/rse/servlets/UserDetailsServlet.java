package rse.servlets;

import engine.logic.Engine;
import engine.users.User;
import rse.logger.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class UserDetailsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session==null)
            response.sendError(603,"User must sign in to the system before using RSE.");
        else{
            User user = Engine.getInstance().getUsersManager().getUser(session.getAttribute("username").toString());
            if(user==null)
                response.sendError(604,"User not found.");
            else {
                String jsonResult = "{";
                jsonResult += (getUserBalance(user)+",");
                jsonResult += (getUserCurrent(user)+"}");
                PrintWriter out = response.getWriter();
                Logger.getServerLogger().post(jsonResult);
                out.print(jsonResult);
            }
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        //TODO: set data to the system.
    }

    private String getUserBalance(User user){
        return ("\"balance\":"+user.getUserBalance());
    }

    private String getUserCurrent(User user){
        StringBuilder jsonResult = new StringBuilder();
        jsonResult.append("\"actions\":[");
        user.getActions().stream()
                .forEach((a) -> {
                    jsonResult.append(a.toJson()+",");
                });
        jsonResult.deleteCharAt(jsonResult.lastIndexOf(","));
        jsonResult.append("]");
        return jsonResult.toString();
    }
}
