# Komponentenkommunikation mit EJB

Ein lauffähiges Beispiel für **Remote-Aufrufe von Enterprise Java Beans** über JNDI.
Es zeigt das Zusammenspiel zwischen einem Standalone-Java-Client (lokale JVM) und
zwei Session-Beans (`@Stateless`), die auf einem **WildFly Application Server** im
**Docker-Container** deployt sind.

## Voraussetzungen

| Werkzeug | Version | Prüfen mit |
|---|---|---|
| JDK | **17** | `java -version` |
| Apache Maven | 3.8+ | `mvn -v` |
| Docker (Desktop oder Engine) | 20+ | `docker --version` |
| Docker Compose | v2+ | `docker compose version` |
| IDE | IntelliJ IDEA (Community/Ultimate) **oder** Eclipse IDE for Enterprise Java | – |

> **Wichtig:** In der IDE muss als Projekt-/Workspace-SDK **JDK 17** eingestellt
> sein. Mit JDK 24/25 schlagen die WildFly-Client-Bibliotheken zur Laufzeit fehl
> (`UnsupportedOperationException: Setting a system-wide Policy object is not supported`).
> Details siehe Abschnitt **"Import in IntelliJ / Eclipse"** weiter unten.

## Projektstruktur

```
.
├── api/                            Gemeinsame Typen für Client und Server
│   └── src/main/java/edu/thi/at4ua/
│       ├── bean/Customer.java          DTO (Serializable)
│       └── ejb/
│           ├── DiscountBeanRemote.java        @Remote-Interface
│           └── SearchCustomerBeanRemote.java  @Remote-Interface
│
├── server/                         Bean-Implementierungen, gepackt als WAR
│   └── src/main/
│       ├── java/edu/thi/at4ua/ejb/
│       │   ├── DiscountBean.java              @Stateless
│       │   └── SearchCustomerBean.java        @Stateless
│       └── resources/META-INF/ejb-jar.xml     Setzt module-name = ejbdemo
│
├── client/                         Standalone-Java-Anwendung
│   └── src/main/java/Main.java     JNDI-Lookup + Remote-Aufrufe
│
├── wildfly-conf/                   Konfiguration für den WildFly-Container
│   ├── application-users.properties   Demo-User für Remote-EJB-Calls
│   ├── application-roles.properties
│   ├── mgmt-users.properties          Admin für Management-Console
│   └── mgmt-groups.properties
│
├── Dockerfile                      Eigenes WildFly-Image (User vorinstalliert)
├── docker-compose.yml              Baut Image und startet Container
└── pom.xml                         Maven-Parent-Projekt
```

## Schnellstart

```bash
# 1) WAR bauen (ejbdemo.war in server/target/)
mvn package

# 2) WildFly-Server im Container hochfahren (baut beim ersten Mal das Image,
#    deployt das WAR automatisch).
docker compose up -d --build

# 3) Warten bis WildFly bereit ist (ca. 10–20 Sekunden), Logs ansehen:
docker compose logs -f wildfly
#    --> Strg+C beendet nur die Log-Ausgabe, nicht den Container

# 4) Client starten – entweder per Maven …
mvn -pl client exec:java

#    … oder in der IDE: siehe Abschnitt "Import in IntelliJ / Eclipse" unten.

# 5) Aufräumen, wenn fertig:
docker compose down
```

## Import in IntelliJ IDEA

1. **File → Open** → die Datei `pom.xml` im Projekt-Wurzelverzeichnis auswählen
   → im Dialog **"Open as Project"** klicken.
2. IntelliJ liest das Multi-Modul-Projekt ein und legt die Module `api`,
   `server`, `client` an. Beim ersten Mal werden alle Maven-Dependencies
   heruntergeladen (kann 1–2 Minuten dauern).
3. **Projekt-SDK auf JDK 17 setzen**, falls IntelliJ es nicht erkennt:
   `File → Project Structure → Project → SDK → Add JDK` und JDK 17 auswählen.
4. **Client starten:** oben rechts in der Run-Toolbar die vorbereitete
   Run-Konfiguration **"Client"** wählen und ▶ klicken.
   - Falls "Client" nicht in der Liste steht: Maven-Tool-Window (rechts, `m`-Symbol)
     öffnen und auf ⟳ "Reload All Maven Projects" klicken.
   - Alternativ: Rechtsklick auf `client/src/main/java/Main.java` → **Run 'Main.main()'**.

## Import in Eclipse

> Empfohlene Eclipse-Variante: **"Eclipse IDE for Enterprise Java and Web
> Developers"**. Sie enthält das `m2e`-Plugin (Maven-Integration) bereits.

1. **File → Import → Maven → Existing Maven Projects** → **Next**.
2. Bei **"Root Directory"** dieses Projektverzeichnis auswählen.
3. Eclipse findet vier `pom.xml`-Dateien (Parent + drei Module). Alle vier
   markiert lassen → **Finish**.
4. **JDK 17 als Workspace-Default setzen**, falls noch nicht geschehen:
   `Window → Preferences → Java → Installed JREs → Add` und JDK 17 hinzufügen,
   dann unter `Java → Compiler` als Compliance-Level **17** wählen.
5. **Client starten:** im Project-Explorer
   `client/src/main/java/Main.java` öffnen → Rechtsklick im Editor →
   **Run As → Java Application**.

## Erwartete Ausgabe

**Im Client (Konsole bzw. Run-Tab der IDE):**

```
Initializing Context...done!
Finding discountBean...done!
Discount für 101.000€: 10.5
Finding searchCustomerBean...done!
4711
donald.duck@demo.org
secret1
4712
susan.summer@demo.org
secret2
```

**Im WildFly-Container (`docker compose logs wildfly`):**

```
Searching for demo.org
```

Diese Zeile wird in `SearchCustomerBean.searchCustomers()` mit `System.out.println`
ausgegeben. Dass sie im Container-Log und **nicht** in der Client-Konsole erscheint,
zeigt: die Bean läuft serverseitig in der JVM des WildFly, der Client ruft sie
wirklich remote auf.

## Demo-Zugangsdaten

Das Setup hat zwei Benutzer, die beim Bauen des Images ins Container-Image
einkopiert werden (siehe `Dockerfile`):

| Zweck | Benutzer | Passwort | Rolle/Gruppe | Konfiguration |
|---|---|---|---|---|
| **Remote-EJB-Aufrufe** (Client → Server) | `demo` | `demo123!` | `guest` | `wildfly-conf/application-users.properties` + `application-roles.properties` |
| **Web-Konsole** auf <http://localhost:9990> | `admin` | `admin123!` | `SuperUser` | `wildfly-conf/mgmt-users.properties` + `mgmt-groups.properties` |

Der Client liest seine Zugangsdaten direkt aus `client/src/main/java/Main.java`
(`Context.SECURITY_PRINCIPAL` und `Context.SECURITY_CREDENTIALS`).

> **Nur für Demo-Zwecke!** In produktiven Setups gehören diese Werte nicht in
> Versionskontrolle, und Passwörter sollten nicht per MD5 abgelegt werden.

## WildFly Management Console

Mit dem laufenden Container:

1. Browser öffnen: <http://localhost:9990>
2. Login mit `admin` / `admin123!`
3. Tab **Deployments** → `ejbdemo.war` ist sichtbar
4. Tab **Runtime → Server → JNDI View** → die gebundenen EJB-Namen sind einsehbar

## Was demonstriert das Beispiel?

1. **Verteilte Komponenten**: Client und Beans laufen in unterschiedlichen JVMs
   (Client lokal, Beans im Container). Aufrufe gehen über das Netzwerk.
2. **EJB-Programmiermodell**: `@Stateless`, `@Remote`, Business-Interface,
   keine `main`-Methode im Server-Code.
3. **JNDI-Lookup**: Der Client kennt nur die Remote-Interfaces (`api`-Modul)
   und sucht die konkreten Beans zur Laufzeit über ihren JNDI-Namen.
4. **Serialisierung**: `Customer`-Objekte werden vom Server zum Client
   serialisiert (deshalb `implements Serializable`).
5. **Authentifizierung**: Remote-EJB-Aufrufe sind nicht „offen" – der Client
   muss sich gegenüber dem Server identifizieren.

## Troubleshooting

**`UnsupportedOperationException: Setting a system-wide Policy object is not supported`**
Das Projekt läuft mit JDK 24 oder höher. Auf **JDK 17** umstellen:
- IntelliJ: `File → Project Structure → Project → SDK`
- Eclipse: `Window → Preferences → Java → Installed JREs` (Standard auf 17 setzen)
  und `Project → Properties → Java Build Path → Libraries`

**`Connection refused: localhost:8080`**
WildFly läuft noch nicht. `docker compose ps` prüfen, dann ggf. mit
`docker compose logs wildfly` nachsehen, was der Server macht.

**`Authentication failed: all available authentication mechanisms failed`**
Die Properties-Files unter `wildfly-conf/` wurden nicht ins Container-Verzeichnis
`/opt/jboss/wildfly/standalone/configuration/` gemountet. `docker compose down`
und `docker compose up -d` neu ausführen.

**Bean-Code wurde geändert, aber der Client sieht alte Logik**
Das WAR muss neu gebaut und der Container neu gestartet werden:
```bash
mvn package && docker compose restart wildfly
```

**WildFly meldet beim Start „Deployment failed" für `ejbdemo.war`**
Häufige Ursache: das WAR enthält noch alte Klassen oder Konfigurationsdateien
aus früheren Builds. Komplett neu bauen und Container neu hochfahren:
```bash
mvn clean package
docker compose up -d --force-recreate
```

**User-/Passwort-Änderung in `wildfly-conf/` wirkt nicht**
Das Image muss neu gebaut werden, da die Properties-Files beim Build kopiert
werden:
```bash
docker compose up -d --build
```

**Port 8080 ist schon belegt**
Anderer Dienst läuft schon auf 8080. Entweder den abschalten, oder in
`docker-compose.yml` den Port-Mapping ändern (z. B. `"18080:8080"`) und
in `Main.java` die `PROVIDER_URL` entsprechend anpassen.
