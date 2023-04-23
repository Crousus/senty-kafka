![senty-kafka](assets/senty-kafka-banner.png)

# Introduction

Senty is a simple and lightweight Kafka client for PHP. It is built on top of [rdkafka](

# How to Run

First, start docker container from `/docker`: `docker-compose -f 
docker-compose.yml up --build`

Add an environment variable to the `ScraperApplication` configuration in 
IntelliJ: `API-KEY=[YOUR-API-KEY]`

Then, run the following services from IntelliJ:

- `ProjectManagerCamundaApplication`
- `CheckoutApplication`
- `OrderApplication`
- `RegistrationApplication`'
- `EmailNotifierApplication`
- `ScraperApplication` (scraper-youtube)

// TODO: maybe we will put this in a docker script


By heading to [http://localhost:8093/camunda/app/welcome/default/#!/login](http://localhost:8093/camunda/app/welcome/default/#!/login), you can then access the Camunda Cockpit.

user/pw: demo

### Register a user:

- [localhost:8096/registration](localhost:8096/registration)
- Put in the following JSON body with your data
`{
  "company": "PORSCHE",
  "firstName": "John",
  "lastName": "Doe",
  "email": "your email",
  "password": "mypassword"
  }`

- verify your email by clicking on the link in the email you received (email only works if verified on my mailgun account, so you mihgt need to enter your own api key in the email-service)
- this is an example verification string: `localhost:8096/verify?email=john.doe@example.com&traceId=5bf59967-df3b-11ed-aae2-34298f74d12c`
- The trace id here is the id of the process instance in Camunda. You can find it in the Camunda Cockpit.

### Place an Order

By heading to http://localhost:8091/shop.html, you now see a checkout form where you can place a new order.

### Other stuff

Assignments covered:
- Exercise 2: Kafka with Spring

  For the initial implementation, we aimed to keep the system simple. We developed a scraper that mimics comment post events for eight YouTube videos from two YouTube channels by extracting data from the "data/comments_filtered_merged_sorted_timed.json" file (previous versions of our data source, prior to cleaning, are also stored in this directory). We also created an analyzer that simply counts the number of posted comments and generates an event when certain milestones are met. The mail service retrieves this event and sends an email to a recipient that is currently hardcoded. The relevant services in the repository at the moment were the scraper, comment-analyzer, and email-notifier.
  
  Architecture and message flow diagram:
![img.png](img.png)

This exercise was inspired by the Flowing retail example especially the creation of producers and consumers and the concepts we had in Lecture 2. Especially the four different patterns of Event-Driven Architectures were kept in mind planning and beginning the senty project. Since the lecture was also about choreography we thought about which different coupling forces will be needed in our project. We decided to go for the Event notification with the mail service as our first EDA Pattern implemented.



- Exercise 3: Process Orchestration with Camunda 

We used Camunda for creating BPMN-based processes with simple HTML user interfaces to start processes and extended sets of process elements such as different types of gateways, external tasks, user tasks, as well as timers, message events, and exceptions. We also implemented a simple HTML Frontend to start the order process. We leveraged Camunda Lafayette3 tutorials to learn and implement these features. Additionally, we used Kafka as a message broker in some parts of the system. We used the Camunda Modeler to create all BPMN diagrams. 
From Lecture 3 and 4 we learned more about Process Orchestration and Automation. Especially the new Information about Boundaries and Business Processes with pointing out that respecting boundaries and avoiding Process Monoliths was a key input for us when we designed the workflows in camunda. With this we created our first workflow which is orchestrated by the project manager.
Through Lecture 3 we got to know more about Fundamentals of Process Automation with Process Engines. The learned knowledge about Workflow Engines and Process Solutions, the development of said and how to orchestrate everything helped us with the creation of the workflows in camunda. 
Understanding that the BPMN Process as XML can be understood by the workflow engine and the concept of Tokens and the happy path helped us tremendously.
It was also usefull to see some more complex workflows with Gateways and decisions in the exercise.


- Exercise 4: Orchestration vs Choreography in Flowing Retail

We extended our project to realize the "order BPMN" flow. To optimize the sequence of crucial actions and ensure their reliability, different commands were used for example payment retrieval and topUp messages. The payment service and scraper service will be responsible for these actions, respectively.
For events we implemented the order succeeded/placed as one and the retrieve active scrapers step in order to guarantee that all active scrapers can respond to the event.
Key Input for us here was lecture 4 and Balancing Orchestration and Choreography. Before the implementation of the project we thought about the clear responsibilities of each service and the tasks it should be included/concerned with. 
Additionally we discussed the difference between Events and Commands and how to implement them in our system. We tried to find a good balance between events and commands for our workflow.
- Exercise 5: Sagas and Stateful Resilience Patterns 


Group reflections and lessons learned:
