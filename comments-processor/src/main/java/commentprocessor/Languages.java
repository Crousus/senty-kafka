package commentprocessor;

import commentprocessor.model.Comment;
import lombok.Data;

import java.util.HashMap;

@Data
public class Languages implements java.io.Serializable {

    private final HashMap<String, Integer> languages = new HashMap<>();

    public Languages add(final Comment comment) {
        if (languages.containsKey(comment.getLanguage())) {
            languages.put(comment.getLanguage(), languages.get(comment.getLanguage()) + 1);
        } else {
            languages.put(comment.getLanguage(), 1);
        }
        return this;
    }

    public HashMap<String, Integer> getLanguages() {
        return languages;
    }
}
