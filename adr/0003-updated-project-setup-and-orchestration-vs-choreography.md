# ADR 0003: Updated Project Setup and Orchestration vs. Choreography

## Status

Superseeded

## Context

We need to update the project setup and decide on whether to use 
orchestration or choreography to handle communication between services. 

## Decision

We have decided to use an orchestration-based approach to manage the 
workflow between the services. This means that the project-manager will be 
responsible for coordinating and managing the flow of work between the 
different services. All services, with the exception of the `email-notifier` 
will use Camunda to model the business processes and manage the workflow.

In addition, we have added a new service called `checkout` which will serve as 
the first touchpoint with the user. When a user places an order, the 
checkout service will process the order and forward it to the 
`project-manager`. The `project-manager` will then coordinate the execution with 
the scraper.

## Consequences

1. Centralized control: The orchestration approach provides centralized control over the workflow, which can simplify the management of complex processes.
2. Performance and scalability: The orchestrated approach may introduce performance bottlenecks and affect scalability, particularly if the project-manager service becomes overwhelmed with tasks. Careful planning and monitoring of the system's performance will be required to mitigate these concerns.
3. Modularity: The updated project setup with the addition of the checkout service further modularizes the system, making it easier to maintain and evolve individual components.