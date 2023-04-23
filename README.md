![senty-kafka](assets/senty-kafka-banner.png)

# How to Run

First, start docker container from `/docker`: `docker-compose -f 
docker-compose.yml up --build`

Add an environment variable to the `ScraperApplication` configuration in 
IntelliJ: `API-KEY=[YOUR-API-KEY]`. Further instructions on how to obtain an 
API key can be found in our submitted report.

Then, run the following services from IntelliJ:

- `ProjectManagerCamundaApplication`
- `CheckoutApplication`
- `OrderApplication`
- `EmailNotifierApplication`
- `ScraperApplication`

By heading to [http://localhost:8091/shop.html](http://localhost:8091/shop.html), you now see a checkout form where you can place a new order.

By heading to [http://localhost:8093/camunda/app/welcome/default/#!/login](http://localhost:8093/camunda/app/welcome/default/#!/login), you can then access the Camunda Cockpit.

Thank you!