package rse.servlets;

import com.google.gson.Gson;
import engine.logic.Engine;
import engine.users.User;
import engine.users.UserAction;
import rse.logger.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MessageFromServer extends HttpServlet {

    private Gson gson = new Gson();

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
            User user = Engine.getInstance().getUsersManager().getUser(username);
            List<String> messages = user.getMsgsToUser();

            String msgs = this.gson.toJson(messages);
            user.emptyMsgsToUser();
            response.getWriter().println(msgs);
            Logger.getServerLogger().post(msgs);

        }catch (Exception e){
            Logger.getServerLogger().post(e.getMessage());
            response.setHeader("errorMessage",e.getMessage());
            response.sendError(HttpServletResponse.SC_FORBIDDEN,e.getMessage());
        }
    }
}
