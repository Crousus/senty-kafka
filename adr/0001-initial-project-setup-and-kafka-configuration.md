# ADR 0001: Initial Project Setup and Kafka Configuration

## Status

Accepted

## Context

We need to decide on the initial project setup, including the service 
architecture and Kafka configuration.

## Decision

We decided to split our system into three services:

1. `project-manager` service, responsible for managing orders and assigning them 
to scrapers.
2. `scraper` service, responsible for processing orders and returning data to 
   the project manager.
3. `email-notifier` service, responsible for notifying customers of their order 
   status.

Regarding Kafka, we decided to focus on the following:

1. Use of topics to ensure communication between services.
2. Use of headers to ensure proper routing of messages to assign them to 
   their customer order.

## Consequences

1. Scalability: The service-oriented architecture will make the system more 
   scalable and easier to maintain. 
2. Flexibility: The use of Kafka topics will provide flexibility to add or 
   remove services without affecting the other services. Headers will make 
   sure that we don't have to worry about routing messages to the correct 
   customers, and also won't have to create too many topics that achieve a 
   similar goal.
3. Integration: The services will need to be integrated correctly to ensure 
   proper functioning. Kafka will mainly be used for communication between 
   services.