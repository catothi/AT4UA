# 04_RMI – Java Remote Method Invocation

Zwei kleine RMI-Beispiele: ein **Addition-Service** und ein **Search-Customer-Service**
(inkl. serialisierbarem `Customer`-Objekt als Rückgabewert).

## Inhalt

| Datei | Zweck |
|-------|-------|
| `src/edu/thi/rmi/AdditionInterface.java` | Remote-Interface |
| `src/edu/thi/rmi/AdditionServer.java` | Server, registriert `ADDITIONSERVICE` |
| `src/edu/thi/rmi/AdditionClient.java` | Client, ruft `add(7, -9)` auf |
| `src/edu/thi/rmi/SearchCustomerInterface.java` | Remote-Interface |
| `src/edu/thi/rmi/SearchCustomerServer.java` | Server, registriert `SEARCH_CUSTOMER_SERVICE` |
| `src/edu/thi/rmi/SearchCustomerClient.java` | Client, sucht Kunden über `demo.org` |
| `src/edu/thi/rmi/Customer.java` | Serialisierbares DTO |

Beide Beispiele nutzen Port **1099** (`Registry.LocateRegistry`) auf `127.0.0.1`.

## Import in der IDE

### Eclipse
`File → Import… → Existing Projects into Workspace` → Ordner `04_RMI` auswählen.
Eclipse erkennt `.project` / `.classpath` automatisch.

### IntelliJ IDEA
`File → Open…` → Ordner `04_RMI` auswählen. IntelliJ erkennt die Eclipse-Projektdateien
und das `src/`-Layout automatisch (Package: `edu.thi.rmi`).

## Ausführen

Pro Beispiel **immer zuerst den Server, dann den Client** starten:

1. `AdditionServer` → danach `AdditionClient`
2. `SearchCustomerServer` → danach `SearchCustomerClient`

> Hinweis: Beide Server belegen Port 1099. Daher nicht gleichzeitig starten –
> entweder Addition- **oder** SearchCustomer-Beispiel laufen lassen.
