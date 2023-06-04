package commentprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import commentprocessor.model.Comment;
import commentprocessor.model.RecentComments;
import io.javalin.http.Context;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.state.*;
import io.javalin.Javalin;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CommentService {

    private final HostInfo hostInfo;
    private final KafkaStreams streams;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);


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

    ReadOnlyWindowStore<String, Long> getCommentCountLast24hStore() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        CommentProcessingTopology.windowedCountStore,
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
        app.post("/sentiment/total/24h", this::processRequestForSentiment24h);

    }

    /** This function processes the incoming requests.
     * It parses the request body, retrieves the videoIds, calls the processor function to process the videoIds,
     * @returns the result in the response as JSON.
     **/
    private <T> void processRequest(Context ctx, Function<List<String>, Map<String, T>> processor) {
        // Print the request body
        logger.debug("New request: " + ctx.body() +" "+ ctx.ip());
        // Parse the request body as JSON into a map
        Map<String, List<String>> body = ctx.bodyAsClass(Map.class);
        // Retrieve the videoIds from the parsed request body
        List<String> videoIds = body.get("videoIds");
        // Process the videoIds using the given processor function
        Map<String, T> result = processor.apply(videoIds);
        // Return the result as JSON in the response
        ctx.json(result);
    }

    private void processRequestForSentiment24h(Context ctx) {
        // Print the request body
        logger.debug("New request: " + ctx.body() +" "+ ctx.ip());
        // Parse the request body as JSON into a map
        Map<String, List<String>> body = ctx.bodyAsClass(Map.class);
        // Retrieve the videoIds from the parsed request body
        List<String> videoIds = body.get("videoIds");
        // Process the videoIds using the getSentimentLast24Hours function
        List<Map<String, Object>> result = getSentimentLast24Hours(videoIds);
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

    private List<Map<String, Object>> getSentimentLast24Hours(List<String> videoIds) {
        // Initialize an empty map to store the total sentiments
        List<Map<String, Object>> videoData = new ArrayList<>();

        // For each videoId, get the total sentiment from the store and add it to the map
        videoIds.forEach(id -> {
            Instant timeFrom = Instant.now().minus(Duration.ofDays(3000));
            Instant timeTo = Instant.now();
            WindowStoreIterator<Double> sentimentIterator = getTotalSentimentLast24hStore().fetch(id, timeFrom, timeTo);

            WindowStoreIterator<Long> countIterator = getCommentCountLast24hStore().fetch(id, timeFrom, timeTo);

            // Prepare a list to store the sentiment data for this video
            List<Map<String, Object>> sentimentData = new ArrayList<>();

            while (sentimentIterator.hasNext() && countIterator.hasNext()) {
                KeyValue<Long, Double> sentimentNext = sentimentIterator.next();
                KeyValue<Long, Long> countNext = countIterator.next();

                // Prepare a map for this day's data
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", Instant.ofEpochMilli(sentimentNext.key).toString());
                dayData.put("sentiment", sentimentNext.value / countNext.value);
                dayData.put("comments", countNext.value);

                // Add the day's data to the sentiment data list
                sentimentData.add(dayData);
            }

            // Prepare a map for the video data
            Map<String, Object> videoMap = new HashMap<>();
            videoMap.put("videoId", id);
            videoMap.put("sentiment", sentimentData);

            // Add the video data to the videoData list
            videoData.add(videoMap);
        });
        // Return the list of video data
        return videoData;

    }



}
