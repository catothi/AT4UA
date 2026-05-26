# 07_Servlets – Einfaches Java-Servlet-Beispiel

Ein minimales Servlet-Beispiel passend zur Folie *„Servlet: Direkte Erzeugung
der HTML-Seite"*: Das `DateServlet` erzeugt auf Anfrage eine HTML-Seite mit
dem aktuellen Datum und der aktuellen Uhrzeit.

Damit das Beispiel ohne externen Application-Server (Tomcat, WildFly, …)
auskommt, läuft es in einem **eingebetteten Jetty-Servlet-Container**. Die
Klasse `ServerMain` startet Jetty per `main()`-Methode und registriert das
Servlet unter `/date`. Aufruf nach dem Start:

<http://localhost:8080/date>

## Inhalt

| Datei                                                   | Zweck                                                              |
|---------------------------------------------------------|--------------------------------------------------------------------|
| `pom.xml`                                               | Maven-Konfiguration (Jakarta Servlet 5 + Jetty 11)                 |
| `src/main/java/edu/thi/servlets/DateServlet.java`       | `HttpServlet`, das in `doGet` HTML mit `new Date()` ausgibt        |
| `src/main/java/edu/thi/servlets/ServerMain.java`        | Startet eingebetteten Jetty auf Port 8080, mappt Servlet auf `/date` |

## Voraussetzungen

- **JDK 17** (oder neuer)
- **Apache Maven 3.8+** (in Eclipse und IntelliJ jeweils integriert)
- Internetzugang beim ersten Build (Maven lädt Jetty + Jakarta Servlet API)

## Import in der IDE

Das Projekt ist als reines **Maven-Projekt** angelegt und enthält bewusst
keine Eclipse-spezifischen `.project`/`.classpath`-Dateien. Beide IDEs
erkennen Maven-Projekte direkt anhand der `pom.xml`.

### Eclipse

1. **File → Import… → Maven → Existing Maven Projects**.
2. Bei *Root Directory* den Ordner `07_Servlets/` wählen.
3. Eclipse erkennt die `pom.xml`, legt das Projekt an und lädt die Jetty-
   Abhängigkeiten beim ersten Build automatisch.

### IntelliJ IDEA

1. **File → Open…** und den Ordner `07_Servlets/` wählen.
2. IntelliJ erkennt die `pom.xml` und legt ein Maven-Modul an. Den Hinweis
   *Load Maven Project* bestätigen.
3. Project SDK auf JDK 17 stellen (**File → Project Structure → Project**).

## Ausführen

1. Klasse `edu.thi.servlets.ServerMain` öffnen.
2. Rechtsklick → **Run 'ServerMain.main()'** (IntelliJ) bzw.
   **Run As → Java Application** (Eclipse).
3. In der Konsole erscheint:
   ```
   Servlet erreichbar unter: http://localhost:8080/date
   ```
4. Im Browser <http://localhost:8080/date> aufrufen – die Seite zeigt das
   aktuelle Datum.
5. Beenden mit dem roten Stop-Button der IDE bzw. `Strg+C` im Terminal.

Alternativ über die Kommandozeile:

```bash
mvn compile exec:java
```

## Hinweise zum Code

- Das Servlet nutzt das **Jakarta-Servlet-API** (`jakarta.servlet.*`).
  Das ist der aktuelle Namespace ab Jakarta EE 9 – ältere Beispiele aus
  dem Web nutzen oft noch `javax.servlet.*` (Java EE 8 und früher).
- Die Mapping-URL (`/date`) wird hier **programmatisch** in `ServerMain`
  registriert. Alternativ ginge das per Annotation (`@WebServlet("/date")`)
  oder in einer `web.xml` – beides setzt aber einen Servlet-Container mit
  Annotation-Scanning bzw. WAR-Deployment voraus.
- Jetty ist hier nur das *Laufzeit-Werkzeug*, um das Servlet aufzurufen.
  Inhaltlich relevant ist ausschließlich die Methode
  `DateServlet.doGet(...)` – exakt das, was auf der Vorlesungsfolie steht.
