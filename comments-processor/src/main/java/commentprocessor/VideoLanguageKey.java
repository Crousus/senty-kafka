package commentprocessor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VideoLanguageKey {
    private String videoId;
    private String language;
}
