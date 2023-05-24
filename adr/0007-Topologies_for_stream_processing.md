# ADR 0007: Topologies for stream processing

## Status

Accepted

## Context

As part of the development of Senty, we need a way to process streams of data, particularly, comment batches. These comment batches need to be broken down into individual comments and processed in various ways to meet our application's requirements.

## Decision

We have decided to use a single, main topology in Senty for all our stream processing needs. This topology will encompass all necessary operations for our application's data processing pipeline. The operations are as follows:

    Decomposition of Comment Batches: Each batch of comments will be broken down into individual comments to facilitate further processing.

    Language Identification: We will identify the language of each comment. For comments that are not in English, we will translate them to English.

    Comment Translation: Non-English comments will be translated into English to ensure consistency in further processing.

    Comment Filtering: Comments will be screened for blacklisted words. Any comment containing such words will be filtered out from the pipeline.

    Sentiment Analysis: Each comment will undergo a sentiment analysis to identify its sentiment value.


## Consequences

Adopting a single, main topology design increases the complexity of our stream processing system. This complexity could potentially increase the cognitive load for developers maintaining and extending the system, as they would have to understand the entire data processing pipeline.

However, we believe the benefits outweigh the drawbacks. The single topology approach provides a clear, linear flow of data processing, which improves the traceability of data and simplifies debugging. Additionally, we anticipate that our topology design is relatively stable and will not require frequent modifications, reducing the overhead associated with understanding the topology.