![senty-kafka](assets/senty-kafka-banner.png)

# Introduction

Senty is a simple and lightweight Kafka client for PHP. It is built on top of [rdkafka](

# How to Run

First, start docker container from `/docker`:

`docker-compose -f docker-compose.yml up --build`

Then, run the following services from IntelliJ:

- `ProjectManagerCamundaApplication`
- `CheckoutApplication`
- `OrderApplication`
- `EmailNotifierApplication`
- `ScraperApplication`

// TODO: maybe we will put this in a docker script

By heading to [http://localhost:8091/shop.html](http://localhost:8091/shop.html), you now see a checkout form where you can place a new order.

By heading to [http://localhost:8093/camunda/app/welcome/default/#!/login](http://localhost:8093/camunda/app/welcome/default/#!/login), you can then access the Camunda Cockpit.

