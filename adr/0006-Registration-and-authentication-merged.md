# ADR 0006: Registration and authentication merged

## Status

Accepted

## Context

In order to use senty and its services, the user needs to register and login himself.

## Decision

We decided to implement the tasks registration and login in a single service. 
Both tasks are related to the user and the user is the only one who can perform these tasks. Therefore, it makes sense to implement them in a single service.
Additionally, the tasks share the same database, so it makes sense to implement them in a single service.
Therefore, the Joint Ownership – Service Consolidation Technique is used for the Data Ownership


## Consequences

--- More coarse-grained scalability

--- Less fault tolerance

--- increased deployment risk

+++ Preserves atomic Transactions

+++ good overall performance

+++ reduced complexity
