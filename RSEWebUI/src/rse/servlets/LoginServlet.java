package rse.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import engine.logic.Engine;
import engine.users.UsersManager;
import rse.logger.Logger;

public class LoginServlet extends HttpServlet {

    private String dashboard_url = "/../../../pages/dashboard/dashboard.html";
    private String login_url = "/../../../pages/login/login.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        if(!username.isEmpty()) {
            String isAdminStr = request.getParameter("is_admin");
            boolean isAdmin = isAdminStr == null ? false : true;
            UsersManager um = Engine.getInstance().getUsersManager();
            if (um.isExists(username)) {
                //TODO: html reaction
            } else {
                try {
                    um.addUser(username, isAdmin);
                    Logger.getServerLogger().post("New user created (username: " + username + ", is admin: " + isAdmin + ")");
                    HttpSession session = request.getSession(true);
                    session.setAttribute("username", username);
                    session.setAttribute("is_admin", isAdmin);
                    Logger.getServerLogger().post("New session created (username: " + session.getAttribute("username") + ", is admin: " + session.getAttribute("is_admin") + ")");
                    Logger.getServerLogger().post("Redirects to the dashboard page");
                    //out.print("true");
                    request.setAttribute("is_ok",true);
                    //response.sendRedirect(dashboard_url);
                } catch (IllegalArgumentException e) {
                    response.sendError(600, e.getMessage());
                }
            }
        } else {
            Logger.getServerLogger().post("Empty username was entered");
            //TODO: html reaction
            //out.print("false");
            request.setAttribute("is_ok",false);
            //Logger.getServerLogger().post("Redirects to the login page");
            //response.sendRedirect(login_url);
        }
        //out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request,response);
    }

}
