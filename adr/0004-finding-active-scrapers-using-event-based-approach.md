# ADR 0004: Finding Active Scrapers using Event-based Approach

## Status

Accepted

## Context

When a new order is created, we need to find an active scraper to process it.
The project manager is responsible for assigning the order to an available 
and active scraper. To do this, two options were considered:

1. The PM could maintain a list of scrapers and try to ping them via their API.
2. The PM could emit an event using Kafka and let the scrapers respond to it.

## Decision

The event-based approach is chosen to find active scrapers. The PM will emit 
an event using Kafka, and the scrapers will respond with a message. The PM 
will have a list of active scrapers and can assign the order (new order or 
top-up) to one of them.

## Consequences

1. Scalability: The event-based approach makes the system more scalable, as 
   the number of scrapers can be increased or decreased without changing the 
   PM's code. The PM can emit the event, and new scrapers can respond with 
   their message, making the system flexible. 
2. Efficiency: The event-based approach reduces the load on the PM since it 
   does not need to try to ping every scraper individually. Instead, it only 
   needs to emit the event, and the scrapers will respond.
3. Latency: The event-based approach may introduce some latency since the PM 
   needs to wait for the scrapers to respond before assigning an order to 
   one of them (instead of knowing exactly whom to ping and only having to 
   wait until we get an HTTP response). However, this latency is likely to 
   be negligible since the scrapers are likely to respond quickly.