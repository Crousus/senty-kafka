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
- `RegistrationApplication`'
- `EmailNotifierApplication`
- `ScraperApplication` (scraper-youtube)

By heading to [http://localhost:8091/shop.html](http://localhost:8091/shop.html), you now see a checkout form where you can place a new order.

By heading to [http://localhost:8093/camunda/app/welcome/default/#!/login](http://localhost:8093/camunda/app/welcome/default/#!/login), you can then access the Camunda Cockpit.

Thank you!
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

