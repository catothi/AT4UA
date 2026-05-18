# WI – Beispielcode zu Anwendungstechnologien für Unternehmensanwendungen

Java-Beispielcode zur Vorlesung **Wirtschaftsinformatik** (THI, SS26) zu den
Themen **Messaging (JMS/ActiveMQ)**, **Java RMI** und
**Komponentenkommunikation (EJB/WildFly)**. Die Projekte in `03_*` und `04_*`
sind als **Eclipse-Projekte** vorbereitet, `05_*` nutzt **Maven**.

## Repository-Struktur

```
.
├── 03_Messaging/                 Grundlagen: Queue & Topic, Sync/Async, Durable Subscriber
├── 03a_Messaging/                Liefer-Semantiken: at-most-once, at-least-once, Idempotenz
├── 04_RMI/                       Java RMI: Addition- und SearchCustomer-Service
└── 05_Komponentenkommunikation/  EJB-Remote-Aufrufe via JNDI auf WildFly (Docker)
```

| Projekt                          | Inhalt                                                                 | Build/Setup       |
|----------------------------------|------------------------------------------------------------------------|-------------------|
| `03_Messaging`                   | Producer/Consumer für Queues, Topics, asynchrone Listener, Durable Sub | Eclipse + `lib/`  |
| `03a_Messaging`                  | 3 Demos zu Verarbeitungs-Semantiken (at-most/-least-once, idempotent)  | Eclipse + `lib/`  |
| `04_RMI`                         | Remote Method Invocation mit `Registry` auf Port 1099                  | Eclipse, kein Lib |
| `05_Komponentenkommunikation`    | Stateless Session Beans (`@Remote`) auf WildFly im Docker-Container    | Maven + Docker    |

Details zu den einzelnen Klassen und zum jeweiligen Setup siehe `README.md` im
Projektordner.

## Voraussetzungen

- **JDK 17** (für `05_Komponentenkommunikation` zwingend; für die übrigen
  Beispiele genügt auch eine neuere LTS-Version)
- **Eclipse IDE for Java Developers** (2023-09 oder neuer empfohlen)
  *oder* **IntelliJ IDEA** (Community oder Ultimate)
- Nur für `03_Messaging` / `03a_Messaging`: **Apache ActiveMQ 5.18.x**
  (Broker), lokal lauffähig auf `tcp://localhost:61616`
- Nur für `05_Komponentenkommunikation`: **Apache Maven 3.8+** und
  **Docker** (Desktop oder Engine) mit **Docker Compose v2+**

> Die Eclipse-Projekte (`03_Messaging`, `03a_Messaging`, `04_RMI`) sind als
> Eclipse-Projekte (`.project` / `.classpath`) ausgeliefert. IntelliJ liest
> dieses Format ebenfalls und legt beim Import automatisch eine eigene
> `.idea/`-Konfiguration an (per `.gitignore` ausgeschlossen, also kein
> Konflikt mit Eclipse).

> Für `03_Messaging` / `03a_Messaging` sind die benötigten JAR-Dateien
> (`activemq-all`, `log4j-api`, `log4j-core`) bereits in den jeweiligen
> `lib/`-Ordnern enthalten – es ist kein Maven/Gradle nötig. `04_RMI` kommt
> mit der Java-Standardbibliothek aus. Lediglich `05_Komponentenkommunikation`
> verwendet Maven (`pom.xml`).

## ActiveMQ-Broker installieren & starten (nur für `03_*`)

Es gibt zwei Wege, den Broker bereitzustellen. **Einer reicht aus** – wählt
die Variante, die zu eurem Setup passt.

### Variante A: Lokale Installation (Archiv entpacken)

1. Apache ActiveMQ "Classic" 5.18.x von der offiziellen Seite laden:
   <https://activemq.apache.org/components/classic/download/>
2. Archiv entpacken, z. B. nach `~/apache-activemq-5.18.7/`.
3. Broker starten:

   - **macOS / Linux**
     ```bash
     cd ~/apache-activemq-5.18.7/bin
     ./activemq start
     ```
   - **Windows** (PowerShell)
     ```powershell
     cd C:\apache-activemq-5.18.7\bin
     .\activemq.bat start
     ```

4. Web-Konsole prüfen: <http://localhost:8161/admin> (User/Passwort: `admin`/`admin`).
5. Stoppen mit `./activemq stop` bzw. `activemq.bat stop`.

### Variante B: Docker mit Volumes (ohne Docker Compose)

Nutzt das offizielle Image `apache/activemq-classic` und legt persistente
Volumes für Daten, Konfiguration und Logs an. So überleben Queues und
Durable-Subscriptions ein `docker rm`.

1. Image ziehen (einmalig):
   ```bash
   docker pull apache/activemq-classic:5.18.7
   ```

2. Named Volumes anlegen (einmalig):
   ```bash
   docker volume create activemq-data
   docker volume create activemq-conf
   docker volume create activemq-logs
   ```

3. Broker starten:

   - **macOS / Linux**
     ```bash
     docker run -d \
       --name activemq \
       -p 61616:61616 \
       -p 8161:8161 \
       -v activemq-data:/opt/apache-activemq/data \
       -v activemq-conf:/opt/apache-activemq/conf \
       -v activemq-logs:/opt/apache-activemq/data/log \
       apache/activemq-classic:5.18.7
     ```
   - **Windows** (PowerShell)
     ```powershell
     docker run -d `
       --name activemq `
       -p 61616:61616 `
       -p 8161:8161 `
       -v activemq-data:/opt/apache-activemq/data `
       -v activemq-conf:/opt/apache-activemq/conf `
       -v activemq-logs:/opt/apache-activemq/data/log `
       apache/activemq-classic:5.18.7
     ```

4. Status / Logs prüfen:
   ```bash
   docker ps
   docker logs -f activemq
   ```

5. Web-Konsole prüfen: <http://localhost:8161/admin> (User/Passwort: `admin`/`admin`).

6. Stoppen / wieder starten / entfernen:
   ```bash
   docker stop activemq          # anhalten (Daten bleiben im Volume)
   docker start activemq         # erneut starten
   docker rm activemq            # Container löschen (Volumes bleiben!)
   docker volume rm activemq-data activemq-conf activemq-logs   # erst hier wird der Zustand verworfen
   ```

> **Hinweis zu Pfaden im Container** (Image `apache/activemq-classic`):
> Installation unter `/opt/apache-activemq`, Konfiguration in
> `…/conf`, KahaDB- und Persistenzdaten in `…/data`, Logs in
> `…/data/log`. Die Anwendungen verbinden sich – unabhängig von Variante A
> oder B – gegen `tcp://localhost:61616`.

## Projekte in Eclipse importieren

1. Eclipse starten und einen Workspace auswählen.
2. **File → Import… → General → Existing Projects into Workspace**.
3. Bei *Select root directory* den geklonten Repo-Ordner wählen.
4. Eclipse erkennt automatisch die Eclipse-Projekte (`03_Messaging`,
   `03a_Messaging`, `04_RMI`). Auswählen → **Finish**.
5. Sicherstellen, dass ein **JDK 17** als Workspace-JRE konfiguriert ist
   (*Preferences → Java → Installed JREs*). Falls Eclipse das Projekt mit
   einem anderen JRE öffnet: Rechtsklick auf Projekt → **Build Path →
   Configure Build Path → Libraries → JRE System Library → Edit**.

Bibliotheken (`lib/*.jar`) werden bei `03_Messaging` / `03a_Messaging` über
die mitgelieferte `.classpath` automatisch eingebunden – kein manuelles
Hinzufügen nötig. `05_Komponentenkommunikation` wird nicht als Eclipse-Projekt,
sondern als Maven-Projekt importiert (siehe dortige `README.md`).

## Projekte in IntelliJ IDEA importieren

Da IntelliJ das Eclipse-Format (`.project` / `.classpath`) direkt lesen kann,
funktioniert der Import in zwei Schritten – einmal pro Projekt:

1. **File → Open…** und den jeweiligen Projektordner (`03_Messaging`,
   `03a_Messaging` bzw. `04_RMI`) auswählen.
   IntelliJ erkennt die Eclipse-Metadaten und legt ein eigenes Modul an.
   - Alternativ: **File → New → Project from Existing Sources…** und im
     Dialog *Import project from external model → Eclipse* wählen.
   - `05_Komponentenkommunikation` stattdessen direkt als Maven-Projekt
     öffnen (IntelliJ erkennt die `pom.xml`).
2. Nach dem Öffnen das **Project SDK** auf JDK 17 stellen:
   **File → Project Structure → Project → SDK** = `17`,
   **Language level** = `17`.
3. Die JARs aus `lib/` werden automatisch aus der Eclipse-`.classpath`
   übernommen. Falls *Module → Dependencies* leer ist:
   **Project Structure → Modules → Dependencies → +
   → JARs or Directories…** → den `lib/`-Ordner auswählen.
4. Ausführen: Rechtsklick auf eine Klasse mit `main` → **Run '…'**.

Wenn mehrere Projekte als ein einziges IntelliJ-Workspace gewünscht sind: in
einem davon die jeweils anderen via **File → New → Module from Existing
Sources…** hinzufügen.

> Tipp: IntelliJ-spezifische Dateien (`.idea/`, `*.iml`) sind über
> `.gitignore` ausgeschlossen und stören die Eclipse-Nutzung nicht.

## Beispiele ausführen

Die genaue Vorgehensweise unterscheidet sich pro Projekt – Details siehe
jeweilige `README.md`. Grobe Übersicht:

- **`03_Messaging` / `03a_Messaging`**: ActiveMQ-Broker starten (siehe oben),
  dann im Package Explorer die gewünschte Klasse mit `main`-Methode per
  Rechtsklick → **Run As → Java Application** ausführen. Typische Reihenfolge:
  erst Consumer/Worker starten (warten auf Nachrichten), dann Producer.
  Mehrere Consumer/Worker parallel: einfach mehrfach Run anstoßen.
- **`04_RMI`**: pro Beispiel zuerst den Server (`AdditionServer` bzw.
  `SearchCustomerServer`), danach den passenden Client starten. Beide Server
  belegen Port 1099, daher nicht gleichzeitig laufen lassen.
- **`05_Komponentenkommunikation`**: `mvn package`, dann den WildFly-Container
  per `docker compose up` starten und anschließend den Standalone-Client
  (`client/`) ausführen.

## Hinweise zum Code

- Die JMS-Beispiele (`03_*`) verbinden sich gegen `tcp://localhost:61616`
  (`ActiveMQConnection.DEFAULT_BROKER_URL`); RMI nutzt Port `1099` auf
  `127.0.0.1`, EJB den WildFly-Remote-Port `8080`.
- Die JMS-API (`javax.jms.*`) ist im `activemq-all-5.18.7.jar` bereits
  enthalten.
- Für `03_*` und `04_*` wird bewusst auf Build-Tools (Maven/Gradle)
  verzichtet, damit der Eclipse-Import möglichst niederschwellig bleibt.
  `05_Komponentenkommunikation` setzt aufgrund der WildFly-Abhängigkeiten
  Maven ein.

## Lizenz / Nutzung

Lehrmaterial für die THI. Bitte nicht ohne Rücksprache weiterverwenden.
