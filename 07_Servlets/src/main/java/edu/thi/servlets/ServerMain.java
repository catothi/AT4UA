package edu.thi.servlets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Startet einen eingebetteten Jetty-Servlet-Container und registriert das
 * DateServlet unter dem Pfad /date. So lässt sich das Beispiel ohne
 * externen Tomcat oder WildFly direkt als Java-Anwendung ausführen –
 * einfach Rechtsklick → Run 'ServerMain' in Eclipse oder IntelliJ.
 */
public class ServerMain {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT); //Web Container

        ServletHandler handler = new ServletHandler(); //Verwalter aller Servlets, Komponente des Containers, Verwaltet Servlet-Mapping & Weiterleitungen
        server.setHandler(handler);
        //Alle eingehenden Anfragen werden an den handler weitergegeben

        handler.addServletWithMapping(new ServletHolder(new DateServlet()), "/date");
//Ein ServletHolder ist ein Behälter/Wrapper der ein Servlet-Objekt umhüllt und dem Web Container (Jetty) zusätzliche Metainformationen über das Servlet bereitstellt!

        server.start();
        System.out.println("Servlet erreichbar unter: http://localhost:" + PORT + "/date");
        System.out.println("Beenden mit Strg+C bzw. dem roten Stop-Button der IDE.");
        server.join(); //server.join() sorgt dafür, dass der Hauptthread (main-Thread) wartet, bis der Jetty-Server beendet wird.
    }
}
