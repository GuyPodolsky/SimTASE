package rse.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engine.logic.Engine;
import engine.users.User;
import engine.users.UsersManager;
import rse.logger.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

/*@WebServlet(name = "ActiveUsersServlet", urlPatterns = "/servlets/ActiveUsersServlet")*/
public class ActiveUsersServlet extends HttpServlet {

    private Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        if(session == null) {
            Logger.getServerLogger().post("User must enter the system first.");
            response.setHeader("errorMessage","User must enter the system first.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User must enter the system first.");
            return;
        }
        try {
            String username = session.getAttribute("username").toString();
            boolean isAdmin = Boolean.getBoolean(session.getAttribute("is_admin").toString());

            UsersManager um = Engine.getInstance().getUsersManager();
            String users = this.gson.toJson(new ArrayList<>(um.getUsers().values()));

            PrintWriter out = response.getWriter();
            Logger.getServerLogger().post(users);
            out.println(users);
            out.flush();
        }catch (Exception e){
            Logger.getServerLogger().post(e.getMessage());
            response.setHeader("errorMessage",e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
        }
    }
}
