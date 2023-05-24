# ADR 0008: Stateless and statefull processing

## Status

Accepted

## Context

Our application involves stream processing services that require different levels of state management. Some services will
operate purely on incoming data without considering previous data (stateless), while others will need to remember information
from previous data records (stateful).
## Decision

We have decided to implement both stateless and stateful processing methodologies in our stream processing service,
depending on the specific requirements of each processing stream.

For services that do not require memory of past data or the tracking of the state of any entities, we will implement stateless
processing. This approach simplifies the service design as it allows each piece of data to be processed independently, reducing
dependencies and potential bottlenecks. This includes filtering the language, translating, performing sentiment analysis, and filtering
comments containing blacklisted words

Stateful processing will be implemented in stream processing where it's crucial to keep track of the state of entities across multiple
data records. This includes the Retrieving of number of comments in each language for a list of video IDs and 
Retrieving Comments regarding their sentiment value for a Video.

## Consequences

Implementing both stateless and stateful processing will add complexity to our stream processing services design.
However, this approach provides us with the flexibility to handle a wide range of data processing requirements