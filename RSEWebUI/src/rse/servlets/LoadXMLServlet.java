package rse.servlets;

import engine.logic.Engine;
import rse.logger.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Collection;

public class LoadXMLServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);

        if(session == null){
            response.setHeader("errorMessage","User must enter the system first.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User must enter the system first.");
            return;
        }
            String username = String.valueOf(session.getAttribute("username"));
            Collection<Part> parts = request.getParts();
            Engine engine = Engine.getInstance();
            for(Part part:parts){
                try {
                    engine.uploadDataFromFile(part.getInputStream(),username);
                } catch (JAXBException e) {
                    Logger.getServerLogger().post("The given file is not valid.\n"+e.getMessage());
                    response.setHeader("errorMessage","The given file is not valid.\n"+e.getMessage());
                    response.sendError(400,"The given file is not valid.\n"+e.getMessage());
                }
            }

            Logger.getServerLogger().post("File loaded successfully");
            response.getWriter().println("File loaded successfully");



        }
    }
