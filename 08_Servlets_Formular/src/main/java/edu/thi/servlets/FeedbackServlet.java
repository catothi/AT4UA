package edu.thi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Nimmt die Daten des HTML-Formulars (feedback.html) entgegen, speichert sie
 * in Variablen auf dem Server und erzeugt als Antwort eine HTML-Seite, die
 * die empfangenen Werte anzeigt – passend zur Folie
 * "HTML-Formularausgabe mittels Servlet".
 *
 * Das Formular nutzt method="get"; mit dem zusaetzlich implementierten
 * doPost koennte das Formular ohne Codeaenderung auch per method="post"
 * abgesendet werden.
 */
public class FeedbackServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleRequest(request, response);
    }

    private void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // UTF-8 fuer Sonderzeichen wie ae/oe/ue, Eurozeichen usw.
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // ---------------------------------------------------------------
        // 1) Einzelne Formularfelder gezielt auslesen und in Variablen
        //    auf dem Server speichern (request.getParameter liefert null,
        //    wenn das Feld nicht uebertragen wurde).
        // ---------------------------------------------------------------
        String titel     = request.getParameter("titel");
        String name      = request.getParameter("name");
        String vorname   = request.getParameter("vname");
        String email     = request.getParameter("mail");
        String geboren   = request.getParameter("geboren");
        String nachricht = request.getParameter("nachricht");
        System.out.println("Name: " + name);
        // Checkbox: Wert ist nur vorhanden, wenn angehakt
        boolean rueckmeldung = request.getParameter("reply") != null;

        // ---------------------------------------------------------------
        // 2) Antwortseite erzeugen
        // ---------------------------------------------------------------
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"de\"><head><meta charset=\"UTF-8\">");
        out.println("<title>Formularausgabe</title></head><body>");

        out.println("<h1>HTML-Formularausgabe mittels Servlet</h1>");
        out.println("<h2>Ihre Formulareingaben</h2>");

        out.println("<h3>In Variablen gespeicherte Werte:</h3>");
        out.println("<ul>");
        out.println("<li>titel: <b>"     + safe(titel)     + "</b></li>");
        out.println("<li>name: <b>"      + safe(name)      + "</b></li>");
        out.println("<li>vname: <b>"     + safe(vorname)   + "</b></li>");
        out.println("<li>mail: <b>"      + safe(email)     + "</b></li>");
        out.println("<li>geboren: <b>"   + safe(geboren)   + "</b></li>");
        out.println("<li>nachricht: <b>" + safe(nachricht) + "</b></li>");
        out.println("<li>Rueckmeldung erwuenscht: <b>" + rueckmeldung + "</b></li>");
        out.println("</ul>");

        // ---------------------------------------------------------------
        // 3) Zusatz: Alle uebertragenen Parameter generisch auflisten
        //    (zeigt, was tatsaechlich an den Server gesendet wurde).
        // ---------------------------------------------------------------
        out.println("<h3>Ausgefuellte Felder (alle Parameter):</h3>");
        Enumeration<String> namen = request.getParameterNames();

        // request.getParameterNames() liefert eine Enumeration (= eine Art Liste/Iterator)
// mit ALLEN Parameter-Namen, die der Browser mitgeschickt hat.
// Beispiel-Inhalt: ["titel", "name", "vname", "mail", "geboren", "nachricht", "reply"]
// Die Reihenfolge ist NICHT garantiert!
        while (namen.hasMoreElements()) {
            String key = namen.nextElement();
            String wert = String.join(", ", request.getParameterValues(key));
            out.println(key + ": " + wert + "<br>");
        }

        out.println("<p><a href=\"/\">Zurueck zum Formular</a></p>");
        out.println("</body></html>");
    }
// Enumeration funktioniert wie ein Zeiger auf eine Liste:
//
//  namen → [ "titel" | "name" | "vname" | "mail" | ... ]
//               ▲
//           Zeiger startet hier
//
// hasMoreElements() = "Gibt es noch ein nächstes Element?"
// nextElement()     = "Gib mir das nächste Element und rücke den Zeiger vor"


    /** Ersetzt null durch einen Platzhalter, damit die Ausgabe lesbar bleibt. */
    private static String safe(String value) {
        return value == null || value.isBlank() ? "&lt;leer&gt;" : value;
    }
}
