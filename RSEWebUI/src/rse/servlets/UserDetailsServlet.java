package rse.servlets;

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
import java.time.LocalDateTime;

public class UserDetailsServlet extends HttpServlet {

    private enum operation {
        Transfer("transfer",0),
        Add("add",1);

        private String name;
        private int num;
        operation(String name, int num) {
            this.name = name;
            this.num = num;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public static operation getByName(String name) {
            for(operation op: operation.values()) {
                if(op.name.equals(name))
                    return op;
            }
            return null;
        }

        public static operation getByNum(int num) {
            for(operation op: operation.values()) {
                if(op.num == num)
                    return op;
            }
            return null;
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if(session==null){

            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User must enter the system first.");
            return;
        }
        else{
            User user = Engine.getInstance().getUsersManager().getUser(session.getAttribute("username").toString());
            if(user==null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not found.");
                return;
            }
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
        HttpSession session = request.getSession(false);
        User curUser = Engine.getInstance().getUsersManager().getUser(session.getAttribute("username").toString());
        float amount = Float.parseFloat(request.getParameter("amount"));
        operation op = operation.getByName(request.getParameter("op"));
        switch (op){
            case Transfer:
                String otherUserStr = request.getParameter("to");
                User otherUser = Engine.getInstance().getUsersManager().getUser(otherUserStr);
                transfer(curUser,otherUser,amount);
                break;
            case Add:
                add(curUser,amount);
                break;
        }
    }

    private String getUserBalance(User user){
        return ("\"balance\":"+user.getUserBalance());
    }

    private String getUserCurrent(User user){
        StringBuilder jsonResult = new StringBuilder();
        jsonResult.append("\"actions\":[");
        if(!user.getActions().isEmpty()) {
            user.getActions().stream()
                    .forEach((a) -> {
                        jsonResult.append(a.toJson() + ",");
                    });
            jsonResult.deleteCharAt(jsonResult.lastIndexOf(","));
        }
        jsonResult.append("]");
        return jsonResult.toString();
    }

    private void transfer(User from, User to, float amount){
        from.getActions().add(new UserAction(("Transfer to " + to.getUserName()), LocalDateTime.now(),amount, from.getUserBalance() , from.getUserBalance()-amount));
        to.getActions().add(new UserAction(("Transfer from " + from.getUserName()), LocalDateTime.now(),amount, to.getUserBalance() , to.getUserBalance()+amount));
        from.setUserBalance(from.getUserBalance()-amount);
        to.addToUserBalance(amount);
    }

    private void add(User user, float amount){
        user.getActions().add(new UserAction("Addition to balance",LocalDateTime.now(),amount, user.getUserBalance(),user.getUserBalance()+amount));
        user.addToUserBalance(amount);
    }
}
