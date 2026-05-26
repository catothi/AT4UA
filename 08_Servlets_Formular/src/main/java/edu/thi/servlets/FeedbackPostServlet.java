package edu.thi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Variante zu FeedbackServlet, die NUR doPost implementiert. Zweck: zeigen,
 * dass bei method="post" die Formulareingaben nicht in der URL stehen,
 * sondern im BODY der HTTP-Anfrage uebertragen werden.
 *
 * Dazu wird der rohe Request-Body einmal komplett ausgelesen und auf der
 * Antwortseite angezeigt; anschliessend wird er manuell geparst (Format:
 * application/x-www-form-urlencoded, identisch zum Query-String bei GET).
 */
public class FeedbackPostServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // Rohen Body einlesen. Achtung: nach getReader() liefert
        // request.getParameter(...) keine Body-Parameter mehr -> deshalb
        // unten manuell parsen.
        String rawBody = request.getReader().lines().collect(Collectors.joining("\n"));

        // Body parsen: key1=value1&key2=value2&... (URL-encoded)
        Map<String, String> params = parseFormUrlEncoded(rawBody);

        // Felder in Variablen ablegen (wie im GET-Servlet)
        String titel     = params.get("titel");
        String name      = params.get("name");
        String vorname   = params.get("vname");
        String email     = params.get("mail");
        String geboren   = params.get("geboren");
        String nachricht = params.get("nachricht");
        boolean rueckmeldung = params.containsKey("reply");

        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"de\"><head><meta charset=\"UTF-8\">");
        out.println("<title>Formularausgabe (POST)</title></head><body>");

        out.println("<h1>HTML-Formularausgabe mittels Servlet (POST)</h1>");

        // Beweis, dass die Daten NICHT in der URL stehen:
        out.println("<h3>Beweis: Daten stecken im Body, nicht in der URL</h3>");
        out.println("<ul>");
        out.println("<li>HTTP-Methode: <b>" + request.getMethod() + "</b></li>");
        out.println("<li>Request-URI: <b>" + request.getRequestURI() + "</b></li>");
        out.println("<li>Query-String: <b>" + safe(request.getQueryString()) + "</b>"
                + " (bei POST leer, weil die Daten im Body uebertragen werden)</li>");
        out.println("<li>Content-Type: <b>" + safe(request.getContentType()) + "</b></li>");
        out.println("<li>Content-Length: <b>" + request.getContentLength() + "</b> Bytes</li>");
        out.println("</ul>");

        out.println("<h3>Roher Request-Body (URL-encoded):</h3>");
        out.println("<pre style=\"background:#eee;padding:0.5em\">"
                + escapeHtml(rawBody) + "</pre>");

        out.println("<h3>In Variablen gespeicherte Werte (nach Body-Parsing):</h3>");
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

    /** Parst einen application/x-www-form-urlencoded String in eine Map. */
    private static Map<String, String> parseFormUrlEncoded(String body) {
        Map<String, String> result = new LinkedHashMap<>();
        if (body == null || body.isEmpty()) return result;
        for (String pair : body.split("&")) {
            int eq = pair.indexOf('=');
            String k = eq < 0 ? pair : pair.substring(0, eq);
            String v = eq < 0 ? ""   : pair.substring(eq + 1);
            result.put(
                URLDecoder.decode(k, StandardCharsets.UTF_8),
                URLDecoder.decode(v, StandardCharsets.UTF_8));
        }
        return result;
    }

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
