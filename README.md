# WI – Messaging-Beispiele (Apache ActiveMQ / JMS)

Java-Beispielcode zur Vorlesung **Wirtschaftsinformatik** (THI, SS26) zum Thema
Nachrichten-Messaging mit Apache ActiveMQ. Die Projekte sind als
**Eclipse-Projekte** vorbereitet und können direkt importiert werden.

## Repository-Struktur

```
.
├── 03_Messaging/      Grundlagen: Queue & Topic, Sync/Async, Durable Subscriber
└── 03a_Messaging/     Liefer-Semantiken: at-most-once, at-least-once, Idempotenz
```

Jedes Unterverzeichnis ist ein **eigenständiges Eclipse-Projekt** mit eigener
`.project`-, `.classpath`-Datei und mitgelieferten Bibliotheken im Ordner `lib/`.

| Projekt          | Inhalt                                                                 |
|------------------|------------------------------------------------------------------------|
| `03_Messaging`   | Producer/Consumer für Queues, Topics, asynchrone Listener, Durable Sub |
| `03a_Messaging`  | 3 Demos zu Verarbeitungs-Semantiken (at-most/-least-once, idempotent)  |

Details zu den einzelnen Klassen siehe jeweilige `README.md` im Projektordner.

## Voraussetzungen

- **JDK 17** (oder neuer – ActiveMQ 5.18 benötigt mindestens Java 11)
- **Eclipse IDE for Java Developers** (2023-09 oder neuer empfohlen)
  *oder* **IntelliJ IDEA** (Community oder Ultimate)
- **Apache ActiveMQ 5.18.x** (Broker), lokal lauffähig auf
  `tcp://localhost:61616`

> Die Projekte sind primär als Eclipse-Projekte (`.project` / `.classpath`)
> ausgeliefert. IntelliJ liest dieses Format ebenfalls und legt beim Import
> automatisch eine eigene `.idea/`-Konfiguration an (per `.gitignore`
> ausgeschlossen, also kein Konflikt mit Eclipse).

> Die benötigten JAR-Dateien (`activemq-all`, `log4j-api`, `log4j-core`) sind
> bereits in den jeweiligen `lib/`-Ordnern enthalten – es ist kein Maven/Gradle
> nötig.

## ActiveMQ-Broker installieren & starten

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

## Projekte in Eclipse importieren

1. Eclipse starten und einen Workspace auswählen.
2. **File → Import… → General → Existing Projects into Workspace**.
3. Bei *Select root directory* den geklonten Repo-Ordner wählen.
4. Eclipse erkennt automatisch beide Projekte (`03_Messaging`,
   `03a_Messaging`). Beide auswählen → **Finish**.
5. Sicherstellen, dass ein **JDK 17** als Workspace-JRE konfiguriert ist
   (*Preferences → Java → Installed JREs*). Falls Eclipse das Projekt mit
   einem anderen JRE öffnet: Rechtsklick auf Projekt → **Build Path →
   Configure Build Path → Libraries → JRE System Library → Edit**.

Bibliotheken (`lib/*.jar`) werden über die mitgelieferte `.classpath`
automatisch eingebunden – kein manuelles Hinzufügen nötig.

## Projekte in IntelliJ IDEA importieren

Da IntelliJ das Eclipse-Format (`.project` / `.classpath`) direkt lesen kann,
funktioniert der Import in zwei Schritten – einmal pro Projekt:

1. **File → Open…** und den Ordner `03_Messaging` auswählen
   (für das zweite Projekt anschließend `03a_Messaging`).
   IntelliJ erkennt die Eclipse-Metadaten und legt ein eigenes Modul an.
   - Alternativ: **File → New → Project from Existing Sources…** und im
     Dialog *Import project from external model → Eclipse* wählen.
2. Nach dem Öffnen das **Project SDK** auf JDK 17 stellen:
   **File → Project Structure → Project → SDK** = `17`,
   **Language level** = `17`.
3. Die JARs aus `lib/` werden automatisch aus der Eclipse-`.classpath`
   übernommen. Falls *Module → Dependencies* leer ist:
   **Project Structure → Modules → Dependencies → +
   → JARs or Directories…** → den `lib/`-Ordner auswählen.
4. Ausführen: Rechtsklick auf eine Klasse mit `main` → **Run '…'**.

Wenn beide Projekte als ein einziges IntelliJ-Workspace gewünscht sind: in
einem davon das jeweils andere via **File → New → Module from Existing
Sources…** hinzufügen.

> Tipp: IntelliJ-spezifische Dateien (`.idea/`, `*.iml`) sind über
> `.gitignore` ausgeschlossen und stören die Eclipse-Nutzung nicht.

## Beispiele ausführen

1. Den ActiveMQ-Broker starten (siehe oben).
2. Im **Package Explorer** die gewünschte Klasse mit `main`-Methode
   auswählen.
3. Rechtsklick → **Run As → Java Application**.
4. Mehrere Consumer/Worker parallel starten: einfach mehrfach Run anstoßen –
   jede Instanz erscheint in einer eigenen Konsole (Konsolen-Toolbar →
   *Display Selected Console*).

Typische Reihenfolge:

- erst Consumer/Worker starten (warten auf Nachrichten)
- dann Producer starten

## Hinweise zum Code

- Alle Beispiele verbinden sich gegen `tcp://localhost:61616`
  (`ActiveMQConnection.DEFAULT_BROKER_URL`).
- Die JMS-API (`javax.jms.*`) ist im `activemq-all-5.18.7.jar` bereits
  enthalten.
- Es wird bewusst auf Build-Tools (Maven/Gradle) verzichtet, damit der
  Eclipse-Import möglichst niederschwellig bleibt.

## Lizenz / Nutzung

Lehrmaterial für die THI. Bitte nicht ohne Rücksprache weiterverwenden.
