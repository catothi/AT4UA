package edu.thi.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Variante zu FeedbackServlet, die NUR doPost implementiert. Zweck: zeigen,
 * dass bei method="post" die Formulareingaben nicht in der URL stehen,
 * sondern im BODY der HTTP-Anfrage uebertragen werden.
 *
 * Der Servlet-Container parst den Body automatisch; wir lesen die Felder
 * deshalb ganz normal mit request.getParameter(...) aus.
 */
public class FeedbackPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // UTF-8 fuer Sonderzeichen wie ae/oe/ue, Eurozeichen usw.
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // ---------------------------------------------------------------
        // 1) Formularfelder auslesen.
        //    request.getParameter(...) ist der normale, praxisuebliche Weg
        //    bei application/x-www-form-urlencoded: der Container parst den
        //    Body automatisch. (Das frueher hier gezeigte manuelle Parsen
        //    des Roh-Bodys war nur eine Veranschaulichung.)
        // ---------------------------------------------------------------
        String titel     = request.getParameter("titel");
        String name      = request.getParameter("name");
        String vorname   = request.getParameter("vname");
        String email     = request.getParameter("mail");
        String geboren   = request.getParameter("geboren");
        String nachricht = request.getParameter("nachricht");
        // Checkbox: Wert ist nur vorhanden, wenn angehakt
        boolean rueckmeldung = request.getParameter("reply") != null;

        // ---------------------------------------------------------------
        // 2) Antwortseite erzeugen
        // ---------------------------------------------------------------
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"de\"><head><meta charset=\"UTF-8\">");
        out.println("<title>Formularausgabe (POST)</title></head><body>");

        out.println("<h1>HTML-Formularausgabe mittels Servlet (POST)</h1>");

        // Beweis, dass die Daten NICHT in der URL stehen, sondern im Body.
        // Wir zeigen das ueber die Metadaten der Anfrage:
        //  - Methode ist POST
        //  - Query-String ist leer (bei GET stuenden hier die Felder)
        //  - Content-Type/-Length beschreiben den Body
        out.println("<h3>Beweis: Daten stecken im Body, nicht in der URL</h3>");
        out.println("<ul>");
        out.println("<li>HTTP-Methode: <b>" + safe(request.getMethod()) + "</b></li>");
        out.println("<li>Request-URI: <b>" + safe(request.getRequestURI()) + "</b></li>");
        out.println("<li>Query-String: <b>" + safe(request.getQueryString()) + "</b>"
                + " (bei POST leer, weil die Daten im Body uebertragen werden)</li>");
        out.println("<li>Content-Type: <b>" + safe(request.getContentType()) + "</b></li>");
        out.println("<li>Content-Length: <b>" + request.getContentLength() + "</b> Bytes</li>");
        out.println("</ul>");

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

        out.println("<p><a href=\"/feedback-post.html\">Zurueck zum POST-Formular</a> | ");
        out.println("<a href=\"/\">Zum GET-Formular</a></p>");
        out.println("</body></html>");
    }

    /** Ersetzt null/leer durch einen Platzhalter und schuetzt vor XSS. */
    private static String safe(String value) {
        return value == null || value.isBlank() ? "&lt;leer&gt;" : escapeHtml(value);
    }

    private static String escapeHtml(String s) {
        return s == null ? "" : s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
