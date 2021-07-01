package rse.servlets;

import rse.logger.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RSEServlet extends HttpServlet {

    private String dashboard_url = "/../../../pages/dashboard/dashboard.html";
    private String login_url = "/../../../pages/login/login.html";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if(session!=null) {
            Logger.getServerLogger().post("New entry of existing user (username: "+session.getAttribute("username")+",is admin:"+session.getAttribute("is_admin")+")");
            Logger.getServerLogger().post("Redirects to the dashboard page");
            response.sendRedirect(dashboard_url);
        } else {
            Logger.getServerLogger().post("New entry of unknown user");
            Logger.getServerLogger().post("Redirects to the login page");
            response.sendRedirect(login_url);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
