# ADR 0005: Superseed Orchestration vs Choreography

## Status

Accepted

## Context

We need to update the project setup and decide on whether to use
orchestration or choreography to handle communication between services.

## Decision

In out updated project, we use a mix of orchestration and choreography:

Orchestration (with Camunda):
- `project-manager`
- `checkout`
- `registration`

Independent / Choreography ??? (without Camuda):
- `scraper`
- `email-notifier`
- `comment-analysis`

## Consequences

1. Fault tolerance: The project-manager service will be more strongly becoming
   a single point of failure because of its "orchestrating" role. However,
   with the decoupling of services, the system will be more fault-tolerant
   as services can continuickly.
2. Performance?