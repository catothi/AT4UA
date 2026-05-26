# 08_Servlets_Formular – Datentransfer HTML-Formular -> Servlet

Beispiel passend zu den Folien *"Transfer von Daten zum Server"* und
*"HTML-Formularausgabe mittels Servlet"*: Eine HTML-Seite mit einem
Feedback-Formular sendet ihre Eingaben an ein Java-Servlet. Das Servlet
liest die einzelnen Felder per `request.getParameter(...)` aus, **speichert
sie in lokalen Variablen** und erzeugt als Antwort eine HTML-Seite, die die
empfangenen Werte zurueckgibt.

Damit das Beispiel ohne externen Application-Server (Tomcat, WildFly, …)
auskommt, laeuft es in einem **eingebetteten Jetty-Servlet-Container**.

## Inhalt

| Datei                                                       | Zweck                                                                 |
|-------------------------------------------------------------|-----------------------------------------------------------------------|
| `pom.xml`                                                   | Maven-Konfiguration (Jakarta Servlet 5 + Jetty 11)                    |
| `src/main/resources/web/feedback.html`                      | GET-Formular (`action="feedback"`, `method="get"`)                    |
| `src/main/java/edu/thi/servlets/FeedbackServlet.java`       | Verarbeitet das GET-Formular, speichert Felder in Variablen           |
| `src/main/resources/web/feedback-post.html`                 | POST-Variante (`action="feedbackPost"`, `method="post"`)              |
| `src/main/java/edu/thi/servlets/FeedbackPostServlet.java`   | Verarbeitet das POST-Formular, zeigt zusaetzlich den rohen Request-Body |
| `src/main/java/edu/thi/servlets/ServerMain.java`            | Startet Jetty auf Port 8080, mappt Servlets auf `/feedback` und `/feedbackPost` |

## Voraussetzungen

- **JDK 17** (oder neuer)
- **Apache Maven 3.8+** (in Eclipse und IntelliJ integriert)
- Internetzugang beim ersten Build (Maven laedt Jetty + Jakarta Servlet API)

## Import in der IDE

Reines **Maven-Projekt** – beide IDEs erkennen das Modul anhand der
`pom.xml`. Jedes Unterprojekt einzeln importieren.

### Eclipse
1. **File -> Open Projects from File System…** (bzw. *Import…
   -> General -> Projects from Folders or Archive*).
2. Bei *Import source* den Ordner `08_Servlets_Formular/` waehlen –
   also die Ebene, auf der die `pom.xml` liegt – und mit **Finish**
   importieren. Eclipse erkennt das Maven-Projekt automatisch und
   laedt die Jetty-Abhaengigkeiten beim ersten Build.

> Hinweis: Der klassische Weg *Import -> Maven -> Existing Maven
> Projects* funktioniert hier nicht zuverlaessig – daher den oben
> beschriebenen Weg ueber **Open Projects from File System** nutzen.

### IntelliJ IDEA
1. **File -> Open…** und den Ordner `08_Servlets_Formular/` waehlen.
2. Hinweis *Load Maven Project* bestaetigen, JDK 17 einstellen.

## Ausfuehren

1. Klasse `edu.thi.servlets.ServerMain` oeffnen.
2. Rechtsklick -> **Run 'ServerMain.main()'**.
3. Konsole:
   ```
   Formular erreichbar unter: http://localhost:8080/
   ```
4. Im Browser <http://localhost:8080/> aufrufen, das Formular ausfuellen
   und auf **Absenden** klicken. Die URL wechselt z.B. zu

   ```
   http://localhost:8080/feedback?titel=Frau&name=Sorglos&vname=Susi&mail=s.s%40thi.de&geboren=1990&nachricht=S%C3%BC%C3%9F+30%E2%82%AC&reply=reply&absenden=absenden
   ```

   und die Antwortseite zeigt die in Variablen gespeicherten Werte.
5. Zum Vergleich die POST-Variante: <http://localhost:8080/feedback-post.html>
   – nach dem Absenden steht in der URL nur noch `/feedbackPost`, die
   Antwortseite zeigt zusaetzlich den rohen Request-Body.
6. Beenden mit dem roten Stop-Button der IDE bzw. `Strg+C`.

Alternativ ueber die Kommandozeile:

```bash
mvn compile exec:java
```

## Hinweise zum Code

- **Datentransfer**: Das HTML-Formular bestimmt mit `method="get"` bzw.
  `method="post"`, *wie* die Daten uebertragen werden:
  - `get`: Parameter werden als **Query-String an die URL angehaengt**
    (z.B. `feedback?login=guest&productId=1234`).
  - `post`: Parameter werden im **Body** der HTTP-Anfrage uebertragen.

  Das `FeedbackServlet` implementiert beide Faelle (`doGet` und `doPost`
  delegieren auf dieselbe Methode), sodass beide Varianten funktionieren.

- **Auslesen im Servlet**:
  ```java
  String name      = request.getParameter("name");
  String vorname   = request.getParameter("vname");
  boolean antwort  = request.getParameter("reply") != null;
  ```
  Der Schluessel entspricht jeweils dem `name`-Attribut im HTML.
  Checkboxen werden nur uebertragen, wenn sie angehakt sind.

- **Antwortseite**: Wie im `DateServlet`-Beispiel wird die HTML-Antwort
  per `response.getWriter()` direkt geschrieben. In echten Projekten
  uebernimmt das meist eine View-Technologie (JSP, Thymeleaf, …).

- **Wo kommt `feedback.html` her?** Maven kopiert beim Build alles aus
  `src/main/resources/` in den Classpath (`target/classes/`). Aus dem
  Ordner `resources/web/` wird also der Classpath-Pfad `/web/`. In
  `ServerMain` wird dieses Verzeichnis ueber
  `context.setBaseResource(Resource.newClassPathResource("/web"))` als
  Document-Root des Jetty-Kontexts gesetzt. Jettys `DefaultServlet`
  (auf `/` gemappt) liefert die statischen Dateien daraus aus, und
  `setWelcomeFiles("feedback.html")` sorgt dafuer, dass beim Aufruf von
  `/` automatisch das Formular angezeigt wird.

  Wichtig: `/web` ist nur der **Classpath**-Pfad, **nicht** Teil der
  URL. Das Formular ist daher unter `http://localhost:8080/` und
  `http://localhost:8080/feedback.html` erreichbar – aber **nicht**
  unter `http://localhost:8080/web` (das gibt korrekterweise 404).
