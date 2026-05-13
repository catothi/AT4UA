# 03_Messaging – JMS-Grundlagen mit ActiveMQ

Einfache Producer/Consumer-Beispiele für **Queues** (Point-to-Point) und
**Topics** (Publish/Subscribe), inklusive **asynchronem** Consumer
(MessageListener) und **Durable Subscriber**.

## Inhalt

| Datei                                          | Zweck                                                                    |
|------------------------------------------------|--------------------------------------------------------------------------|
| `src/edu/thi/messaging/QueueProducer.java`     | Sendet 10 000 Nachrichten + `SHUTDOWN` in die Queue `ActiveMQTestQueue3` |
| `src/edu/thi/messaging/QueueConsumer1.java`    | Empfängt synchron aus `ActiveMQTestQueue3` (Wettbewerb mit Consumer 2)   |
| `src/edu/thi/messaging/QueueConsumer2.java`    | Zweiter Queue-Consumer – zeigt Load-Balancing über Konkurrenz            |
| `src/edu/thi/messaging/TopicProducer.java`     | Publisher auf das Topic `ActiveMQTestTopic`                              |
| `src/edu/thi/messaging/TopicConsumer1.java`    | Subscriber auf `ActiveMQTestTopic3` (synchron)                           |
| `src/edu/thi/messaging/TopicConsumer2.java`    | Zweiter Subscriber auf `ActiveMQTestTopic`                               |
| `src/edu/thi/messaging/TopicAsyncConsumer.java`| Async Subscriber – nutzt `MessageListener`                               |
| `src/edu/thi/messaging/TopicMessageListener.java` | Listener-Implementierung für den Async Consumer                       |
| `src/edu/thi/messaging/TopicDurableSubscriber.java` | Dauerhafte Subscription (überlebt Offline-Zeiten)                   |

> Hinweis: Die Queue-/Topic-Namen wurden bewusst leicht verschieden gewählt
> (z. B. `ActiveMQTestQueue3` vs. `ActiveMQTestTopic` / `…Topic3`) – die
> Demos sind so untereinander unabhängig.

## Vorgeschlagene Ausführungsreihenfolge

### a) Queue (Point-to-Point)
1. Broker starten
2. `QueueConsumer1` starten (optional zusätzlich `QueueConsumer2`)
3. `QueueProducer` starten

Beobachtung: Jede Nachricht geht an genau einen Consumer.

### b) Topic (Publish/Subscribe)
1. Broker starten
2. `TopicConsumer1` und/oder `TopicConsumer2` starten
3. `TopicProducer` starten

Beobachtung: Alle aktiven Subscriber erhalten jede Nachricht.

### c) Asynchroner Empfang
1. `TopicAsyncConsumer` starten – wartet via `MessageListener`
2. `TopicProducer` starten

### d) Durable Subscription
1. `TopicDurableSubscriber` **einmal** starten und beenden (Subscription wird registriert)
2. `TopicProducer` starten – Nachrichten werden gepuffert
3. `TopicDurableSubscriber` erneut starten – holt die verpassten Nachrichten ab

## Konfiguration

- Broker-URL: `tcp://localhost:61616` (Default von ActiveMQ)
- Java-Paket: `edu.thi.messaging`
- Build-Output: `bin/` (von Eclipse erzeugt, nicht versioniert)

## Eclipse-Hinweise

Die Klassen werden über *Run As → Java Application* gestartet. Für mehrere
parallele Konsolen das Symbol *Display Selected Console* in der Konsolen-View
nutzen.
