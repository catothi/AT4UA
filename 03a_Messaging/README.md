# 03a_Messaging – Liefer-Semantiken & Idempotenz

Drei kleine Producer/Worker-Demos, die zeigen, **wie der Zeitpunkt des ACK
das Verhalten bei Crashs verändert** und wie ein **Idempotenz-Cache**
Doppelverarbeitungen verhindert.

Alle Demos arbeiten nach dem gleichen Schema:

```
Producer  ─── Task-Queue ───►  Worker  ─── Result-Queue ───►  Producer
            (a + b)                          (sum, msgId)
```

## Inhalt

| Demo (Source-Folder)                              | Klassen                                | Semantik                                           |
|---------------------------------------------------|----------------------------------------|----------------------------------------------------|
| `01_normal/`                                      | `CalcProducer`, `CalcWorker`           | **at-most-once** – ACK *vor* Berechnung            |
| `02_normal_ACK_nach_Berechnung_producer/`         | `CalcProducerAck`, `CalcWorkerAck`     | **at-least-once** – ACK *nach* Berechnung+Send     |
| `03_idempotent/`                                  | `CalcProducerIdem`, `CalcWorkerIdem`   | **at-least-once + idempotenter Worker** (Cache)    |

Queue-Namen je Demo:

| Demo | Task-Queue           | Result-Queue           |
|------|----------------------|------------------------|
| 1    | `calc.tasks.normal`  | `calc.results.normal`  |
| 2    | `calc.tasks.ack`     | `calc.results.ack`     |
| 3    | `calc.tasks.idem`    | `calc.results.idem`    |

> Die drei Demos sind komplett voneinander entkoppelt – sie können in
> beliebiger Reihenfolge bzw. parallel ausgeführt werden.

## Lernziele

- **Demo 1 (at-most-once):** ACK zuerst, dann Rechnung. Crasht der Worker
  zwischen ACK und Versand des Ergebnisses, ist die Nachricht **weg**.
- **Demo 2 (at-least-once):** ACK erst nach erfolgreichem Versand des
  Ergebnisses. Crash → Re-Delivery → eventuell **mehrfache** Verarbeitung
  und doppelte Ergebnisse.
- **Demo 3 (idempotent):** Worker hält einen `msgId → result`-Cache. Bei
  Re-Delivery wird das gleiche Ergebnis erneut gesendet, statt neu zu
  rechnen. Der Producer dedupliziert eingehende Ergebnisse über `msgId`.
  In Demo 3 simuliert der Worker bewusst einen Crash für `msgId=3`
  (`session.recover()`), um die Re-Delivery sichtbar zu machen.

## Vorgeschlagene Ausführungsreihenfolge

Für jede Demo gilt:

1. ActiveMQ-Broker starten
2. **Worker** starten (`CalcWorker*`)
3. **Producer** starten (`CalcProducer*`)

Beide Konsolen beobachten – die Ausgaben sind so gestaltet, dass die
Semantik (ACK-Zeitpunkt, Re-Delivery, Cache-Treffer) direkt nachvollziehbar
ist.

## Eclipse-Hinweise

- Die drei Demo-Ordner sind als **getrennte Source-Folder** konfiguriert.
  Dadurch dürfen alle Klassen im *Default Package* liegen, ohne dass es
  Namenskollisionen gibt.
- In Eclipse stehen die Klassen direkt im *Package Explorer* unter den drei
  Ordnern. *Run As → Java Application* genügt zum Starten.
- Falls Eclipse beim Import eine Warnung „default package" anzeigt: das ist
  hier so gewollt und ohne Funktionseinschränkung.

## Voraussetzungen

Siehe Haupt-`README.md` im Wurzelverzeichnis (JDK 17, ActiveMQ 5.18,
Eclipse). Die benötigten JARs liegen in `lib/`.
