package rse.servlets;

import com.google.gson.Gson;
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

    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        if(session == null)
            throw new IllegalAccessError("User must sign in to the system before using RSE.");

        // i dont really sure if we need this in here
        String username = session.getAttribute("username").toString();
        boolean isAdmin = Boolean.getBoolean(session.getAttribute("is_admin").toString()); // todo: check how do we save this attribute

        UsersManager um = Engine.getInstance().getUsersManager();
        String users = this.gson.toJson(new ArrayList<>(um.getUsers().values()));

        //response.setContentType("text/json");
        PrintWriter out = response.getWriter();
        Logger.getServerLogger().post(users);
        out.print(users);
        out.flush();
     /*   *//*
        This servlet will return a text describing json object
        this will be a list with every user name and is if he's admin or trader.
         *//*
        out.print("[");
        for(User user: users.values()){
            out.print("{username:"+user.getUserName()+
                    ",is_admin:"+user.isAdmin()+"}");
        }
        out.print("]");*/

    }

}