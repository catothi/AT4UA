package edu.thi.servlets;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Startet einen eingebetteten Jetty-Servlet-Container.
 *
 *   /            -> liefert statische HTML-Dateien aus src/main/resources/web
 *                   (insbesondere feedback.html mit dem Formular)
 *   /feedback    -> FeedbackServlet, das die Formulardaten entgegennimmt
 *                   und in Variablen auf dem Server speichert
 *
 * Aufruf nach dem Start: http://localhost:8080/
 */
public class ServerMain {

    private static final int PORT = Integer.getInteger("port", 8080);

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");

        // Document-Root: Inhalt von src/main/resources/web (im Classpath unter /web)
        // wird unter "/" ausgeliefert -> feedback.html ist als /feedback.html

        context.setBaseResource(org.eclipse.jetty.util.resource.Resource.newClassPathResource("/web"));
        // Welcome-File: ruft jemand "/" (also keine konkrete Datei) auf,
        // liefert Jetty automatisch feedback.html aus dem Document-Root aus.
        context.setWelcomeFiles(new String[] { "feedback.html" });

        // Formular-Verarbeitung (GET: Daten im Query-String der URL)
        context.addServlet(new ServletHolder(new FeedbackServlet()), "/feedback");
        // Zweite Variante (POST: Daten im Request-Body), aufgerufen aus feedback-post.html
        context.addServlet(new ServletHolder(new FeedbackPostServlet()), "/feedbackPost");
        // Default-Servlet bedient die statischen Dateien aus der BaseResource
        context.addServlet(new ServletHolder("default", DefaultServlet.class), "/");

        server.setHandler(context);

        server.start();
        System.out.println("Formular erreichbar unter: http://localhost:" + PORT + "/");
        System.out.println("Beenden mit Strg+C bzw. dem roten Stop-Button der IDE.");
        server.join();
    }
}
