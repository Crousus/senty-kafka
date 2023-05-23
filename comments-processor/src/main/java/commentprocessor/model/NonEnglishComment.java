package commentprocessor.model;

import lombok.Data;

@Data
public class NonEnglishComment {
    private String language;
    private Comment comment;
    private String translation;
}
