# ADR 0006: Registration and authentication merged

## Status

Accepted

## Context

To utilize Senty and its services, users need to register and log in to their accounts.

## Decision

We decided to merge the registration and login tasks into a single service. Both tasks are closely related to user management, and only the user can perform these tasks. Combining them into one service streamlines the user experience and simplifies the architecture. Additionally, both tasks share the same database, further justifying the consolidation. Therefore, we will apply the Joint Ownership – Service Consolidation Technique for Data Ownership.

## Consequences

By merging the registration and authentication services, we will experience the following consequences:

- Preserved atomic transactions: Combining the services ensures atomic transactions, simplifying the management of user data and interactions.
- Improved overall performance: Consolidating the services may lead to better performance, as fewer inter-service calls are required.
- Reduced complexity: Merging the services reduces the complexity of the overall architecture, making it easier to maintain and understand.

On the other hand, the merged service offers a few downsides:

- More coarse-grained scalability: Combining the services may reduce the granularity of scaling options, as scaling one functionality now affects the other.
- Reduced fault tolerance: A single point of failure is introduced, as issues with one service could impact the other.
- Increased deployment risk: Deploying updates to one service now carries a risk of affecting the other, increasing the potential for deployment issues.