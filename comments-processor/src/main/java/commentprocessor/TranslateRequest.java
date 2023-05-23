package commentprocessor;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslateRequest {
    private String q;
    private String source;
    private String target;
}