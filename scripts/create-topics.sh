echo "Waiting for Kafka to come online..."
cub kafka-ready -b kafka:9092 1 20
# create the game events topic with retention of 10 minutes for better testablity
kafka-topics --create --bootstrap-server kafka:9092 --topic new-comment-batches --replication-factor 1 --partitions 1 --config retention.ms=600000
kafka-topics --create --bootstrap-server kafka:9092 --topic preprocessed-comments --replication-factor 1 --partitions 1 --config retention.ms=600000

# create the players topic
sleep infinity
