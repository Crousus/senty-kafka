# HSG-EDPO-SS23 / Exercise 3 and 4

|         	| 	                         |
|---------	|---------------------------|
| Group   	| 2                       	 |
| Members 	| Johannes, Luka, Philipp 	 |
| Date    	| March 21, 2023           	 |

<br>

# Exercise 3



<br>

# Exercise 4

## GitHub
[https://github.com/Crousus/senty-kafka](https://github.com/Crousus/senty-kafka)

For the first implementation, we decided to keep the system simple. We made a scraper that mocks comment post events of 8 youtube videos of 2 youtube channels by reading them from `data/comments_filtered_merged_sorted_timed.json` (previous iterations of our data source, pre-cleaning, are also in this folder). We added an analyzer that currently just counts the ammount of postet comments and produces an Event once certain milestones are reached. The mail service picks this up and sends an email to a currently hard coded recepient. The currently relevant services in the repository are:
- scraper
- comment-analyzer
- email-notifier

## Architecture and message flow diagram:
<img src="diagram.png"  width="200">

## Contributions:
- Johannes, Luka, Philipp

