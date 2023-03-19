# ADR 0002: Process Orchestration and Messaging with Camunda and Kafka

## Status

Accepted

## Context

We need to decide on the messaging stack for our process orchestrator and 
decide between Kafka or Camunda messaging.

## Decision

We decided to use Camunda for process orchestration and Kafka for messaging. 
Camunda is an open-source workflow automation tool that provides a platform 
for designing, executing, and monitoring business processes. It provides a 
graphical interface to define the workflows and supports BPMN 2.0 notation. 
Kafka, on the other hand, is a distributed streaming platform that provides 
a messaging system for real-time data processing.

We will use Camunda to define and execute our workflows and use Kafka for 
messaging between services. We will use Kafka topics to exchange messages 
between services and ensure decoupling between them. We will also use Avro 
schema for message serialization to ensure type safety and compatibility.

## Consequences

1. Scalability: The use of Camunda and Kafka will make the system more 
   scalable.
2. Efficiency: Camunda will provide a powerful tool to define and execute 
   complex workflows, and Kafka will provide efficient messaging between services.
3. Integration: The services will need to be integrated correctly to ensure 
   functionality across Camunda and Kafka. This may also increase the system's 
   complexity, requiring more development time and expertise. 