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

1. Separation of concerns: The scraper service will only be responsible for 
   processing orders and will not need to be concerned with how the orders are 
   assigned to it. This separation of concerns will make the system more 
   modular and easier to maintain.
2. Fault tolerance: The project-manager service will be more strongly becoming 
   a single point of failure because of its "orchestrating" role. However, 
   with the decoupling of services, the system will be more fault-tolerant 
   as services can continue to operate independently if one service fails.