# HSG-EDPO-SS23 / Exercise 3 and 4

|         	| 	                         |
|---------	|---------------------------|
| Group   	| 2                       	 |
| Members 	| Johannes, Luka, Philipp 	 |
| Date    	| March 21, 2023           	 |

<br>

## Exercise 3

As elaborated in `adr/0002`, the decision has been made to use Camunda for process orchestration and Kafka for messaging, utilizing Kafka topics for exchanging messages and ensuring decoupling between services, with the consequence of increased scalability and efficiency but requiring more careful integration and potential increased complexity.

The `project-manager-camunda` (prev. `project-manager`), implements this decision and orchestrates the following workflow:

<img src="project-manager-camunda-bpmn.png"  width="600">

To accomplish the correlation between Kafka and Camunda, the `project-manager-camunda` uses a `MessageListener` that correlates a Kafka message to a Camunda process instance using the traceId provided in the Kafka message. 

Moreover, as suggested for Task 2, we implemented a simple HTML user interface to start the order process in our `checkout` service:

<img src="form.png"  width="600">

<br>

## Exercise 4

We have extended the order flow to include the following services which orchestrate a workflow (see BPMNs):
- `checkout`
- `order`
- `project-manager-camunda`

## Contributions:
- Johannes, Luka, Philipp

