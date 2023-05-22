echo "Waiting for Kafka to come online..."
cub kafka-ready -b kafka:9092 1 20
# create the game events topic
kafka-topics --create --bootstrap-server kafka:9092 --topic new-comment-batches --replication-factor 1 --partitions 1
# create the players topic
sleep infinity
