package commentprocessor;

import commentprocessor.model.Comment;
import commentprocessor.model.RecentComments;
import io.javalin.http.Context;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.state.*;
import io.javalin.Javalin;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CommentService {

    private final HostInfo hostInfo;
    private final KafkaStreams streams;

    public CommentService(HostInfo hostInfo, KafkaStreams streams) {
        this.hostInfo = hostInfo;
        this.streams = streams;
    }

    ReadOnlyKeyValueStore<String, Languages> getLanguageStore() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        CommentProcessingTopology.languageStore,
                        // state store type
                        QueryableStoreTypes.keyValueStore()));
    }

    ReadOnlyKeyValueStore<String, Long> getCommentCountStore() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        CommentProcessingTopology.commentCountStore,
                        // state store type
                        QueryableStoreTypes.keyValueStore()));
    }

    ReadOnlyKeyValueStore<String, RecentComments> getLast5CommentStore() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        CommentProcessingTopology.lastCommentsStore,
                        // state store type
                        QueryableStoreTypes.keyValueStore()));
    }

    ReadOnlyKeyValueStore<String, Double> getTotalSentiment() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        CommentProcessingTopology.totalSentimentStore,
                        // state store type
                        QueryableStoreTypes.keyValueStore()));
    }

    ReadOnlyWindowStore<String, Double> getTotalSentimentLast24hStore() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        CommentProcessingTopology.windowedSentimentStore,
                        // state store type
                        QueryableStoreTypes.windowStore()));
    }


    // This function starts a Javalin server with the given host and port.
    void start() {
        Javalin app = Javalin.create().start(hostInfo.port());
        // Registering endpoints and their corresponding handlers
        app.post("/comments/count/lang", ctx -> processRequest(ctx, this::getLanguageCount));
        app.post("/comments/count", ctx -> processRequest(ctx, this::getCommentCount));
        app.post("/comments/last5", ctx -> processRequest(ctx, this::getLast5Comment));
        app.post("/sentiment/total", ctx -> processRequest(ctx, this::getTotalSentiment));
        app.post("/sentiment/total/24h", ctx -> processRequest(ctx, this::getSentimentLast24Hours));

    }

    /** This function processes the incoming requests.
     * It parses the request body, retrieves the videoIds, calls the processor function to process the videoIds,
     * @returns the result in the response as JSON.
     **/
    private <T> void processRequest(Context ctx, Function<List<String>, Map<String, T>> processor) {
        // Print the request body
        System.out.println("New request: " + ctx.body() +" "+ ctx.ip());
        // Parse the request body as JSON into a map
        Map<String, List<String>> body = ctx.bodyAsClass(Map.class);
        // Retrieve the videoIds from the parsed request body
        List<String> videoIds = body.get("videoIds");
        // Process the videoIds using the given processor function
        Map<String, T> result = processor.apply(videoIds);
        // Return the result as JSON in the response
        ctx.json(result);
    }

    // This function counts the number of different languages used in comments for each video.
    private Map<String, HashMap<String, Integer>> getLanguageCount(List<String> videoIds) {
        // Initialize an empty map to store the counts
        HashMap<String, HashMap<String, Integer>> counts = new HashMap<>();
        // For each videoId, get the language count from the store and add it to the map
        videoIds.forEach(s -> {
            Languages languages = getLanguageStore().get(s);
            counts.put(s, languages != null ? languages.getLanguages() : new HashMap<>());
        });
        // Return the map of counts
        return counts;
    }

    // This function counts the number of comments for each video.
    private Map<String, Long> getCommentCount(List<String> videoIds) {
        // Initialize an empty map to store the counts
        HashMap<String, Long> counts = new HashMap<>();
        // For each videoId, get the comment count from the store and add it to the map
        videoIds.forEach(s -> {
            Long count = getCommentCountStore().get(s);
            if (count == null)
                counts.put(s, 0L);
            else
                counts.put(s, count);
        });
        // Return the map of counts
        return counts;
    }

    // This function retrieves the last 5 comments for each video.
    private Map<String, List<Comment>> getLast5Comment(List<String> videoIds) {
        // Initialize an empty map to store the comments
        HashMap<String, List<Comment>> counts = new HashMap<>();
        // For each videoId, get the recent comments from the store and add them to the map
        videoIds.forEach(s -> {
            RecentComments recentComments = getLast5CommentStore().get(s);
            if (recentComments == null)
                counts.put(s, new ArrayList<>());
            else
                counts.put(s, recentComments != null ? new ArrayList<>(recentComments.getComments()) : new ArrayList<>());
        });
        // Return the map of comments
        return counts;
    }

    // This function calculates the total sentiment score for each video.
    private Map<String, Double> getTotalSentiment(List<String> videoIds) {
        // Initialize an empty map to store the total sentiments
        HashMap<String, Double> counts = new HashMap<>();
        // For each videoId, get the total sentiment and the comment count from the store, calculate the average sentiment, and add it to the map
        videoIds.forEach(s -> {
            Long count = getCommentCountStore().get(s);
            Double totalSentiment = getTotalSentiment().get(s);

            if (totalSentiment == null || count == null)
                counts.put(s, 0.0);
            else
                // Calculate the average sentiment
                // (total sentiment score) / (number of comments
                counts.put(s, totalSentiment / count);
        });
        // Return the map of total sentiments
        return counts;
    }

    private Map<String, Double> getSentimentLast24Hours(List<String> videoIds) {
        // Initialize an empty map to store the total sentiments
        HashMap<String, Double> counts = new HashMap<>();

        // For each videoId, get the total sentiment from the store and add it to the map
        videoIds.forEach(s -> {
            WindowStoreIterator<Double> sentimentIterator = getTotalSentimentLast24hStore().fetch(s, Instant.now().minus(24, ChronoUnit.HOURS), Instant.now());
            double totalSentimentLast24h = 0;
            long count = 0;

            while (sentimentIterator.hasNext()) {
                totalSentimentLast24h += sentimentIterator.next().value;
                count++;
            }
            sentimentIterator.close();

            if (count == 0) {
                counts.put(s, 0.0);
            } else {
                counts.put(s, totalSentimentLast24h / count);
            }
        });
        // Return the map of total sentiments
        return counts;
    }



}
