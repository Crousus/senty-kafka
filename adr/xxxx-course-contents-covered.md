# ADR xxxx: Course Contents Covered

# Contents
Event Driven Architecture Patterns:

    • Event Notification
    • Event-carried State Transfer
    • Event Sourcing
    • Command Query Responsibility Segregation (CQRS)
Orchestration vs Choreography

Orchestration:

Choreography:


Workflow State Management

Fundamentals of Process Automation with Process Engines

    • Workflow Engines and Process Solutions
        - Durable State
        - Scheduling
        - Versioning
        - Visibility
        - Audit data
        - Tooling
        - SupportforHuman/Machine Collaboration

    • Development of Process Solutions
        - Token Concept
        - Tasks 
            - service tasks
            - user tasks
            - business rule tasks
            - script tasks
        - Gateways
            - Exclusive Gateway
            - Parallel Gateway
        - Events
            - intermediate and boundary events
            - different event types
    • Orchestrating Everything
        - Software Components
            - Enterprise Service Bus (ESB)
        - Physical devices and things

Autonomy, boundaries and isolation:
    
        - Boundaries and Business Processes
        - Avoid Process Monolith
        - Respect Boundaries

Balancing Orchestration and Choreography

        - Breaking Event Chains with Commands 
        - Clear Responsibilities
        - Events OR Commands
        - Finding the Right Balance

Transactional Sagas

        - State Management and Eventual Consistency
        - Saga Pattern

Process Engines and Integration Challenges

        - Communication patterns
            -  Stateful retry
            -  Human Intervention
            -  Aggregator Pattern
        - Consistency problems and transactional guarantees
            - Business Strategies to Handle Inconsistencies
            - Transactional Outbox Pattern 
            - Challenges Around Atomicity
            - The Need for Business Transactions
        - Importance of idempotence