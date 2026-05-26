package edu.thi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Einfaches Servlet, das direkt eine HTML-Seite mit dem aktuellen
 * Datum und der aktuellen Uhrzeit erzeugt.
 *
 * Aufruf nach dem Start von ServerMain: http://localhost:8080/date
 */
public class DateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");

        // Erstes Servlet antwortet mit dem aktuellen Datum
        final PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<body>");
        out.println("<h3>Servlet zur Ausgabe des aktuellen Datums und der aktuellen Uhrzeit</h3>");
        out.println("Datum: <b>" + new Date() + "</b>");
        out.println("</body>");
        out.println("</html>");
    }
}
