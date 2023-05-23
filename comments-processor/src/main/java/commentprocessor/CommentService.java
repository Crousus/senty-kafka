package commentprocessor;

import io.javalin.http.Context;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentService {

    private final HostInfo hostInfo;
    private final KafkaStreams streams;

    public CommentService(HostInfo hostInfo, KafkaStreams streams) {
        this.hostInfo = hostInfo;
        this.streams = streams;
    }

    ReadOnlyKeyValueStore<String, Long> getStore() {
        return streams.store(
                StoreQueryParameters.fromNameAndType(
                        // state store name
                        CommentProcessingTopology.languageStore,
                        // state store type
                        QueryableStoreTypes.keyValueStore()));
    }

    void start() {
        Javalin app = Javalin.create().start(hostInfo.port());

        /** Local key-value store query: all entries */
        //app.get("/comments", this::getAll);

        /** Local key-value store query: approximate number of entries */
        app.post("/comments/count", this::getCount);

        /** Local key-value store query: approximate number of entries */

    }

    private void getCount(Context ctx) {
        // Parse the body as JSON
        System.out.println("body: " + ctx.body());
        Map<String, List<String>> body = ctx.bodyAsClass(Map.class);;
        // Get the list of video ids from the request

        List<String> videoIds = body.get("videoIds");

        Map<String, Long> counts = new HashMap<>();
        for (String videoId : videoIds) {
            System.out.println("videoId: " + videoId);
            Long count = getStore().get(videoId);
            if (count != null) {
                counts.put(videoId, count);
            }
        }

        ctx.json(counts);
    }

    public List<VideoLanguageKey> getLanguageCounts(String videoId, KafkaStreams streams) {
        ReadOnlyKeyValueStore<VideoLanguageKey, Long> store =
                streams.store("comments-by-language-store", QueryableStoreTypes.keyValueStore());

        List<LanguageCount> counts = new ArrayList<>();

        List<String> allPossibleLanguages = Arrays.asList("ar", "bg", "de", "el", "en", "es", "fr", "hi", "it", "ja", "nl", "pl", "pt", "ru", "sw", "th", "tr", "ur", "vi", "zh");

        for (String language : allPossibleLanguages) {
            VideoLanguageKey key = new VideoLanguageKey(videoId, language);
            Long count = store.get(key);
            if (count != null) {
                counts.add(new LanguageCount(language, count));
            }
        }

        return counts;
    }


}
