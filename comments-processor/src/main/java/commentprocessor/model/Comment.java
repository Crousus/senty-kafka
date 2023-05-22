package commentprocessor.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Comment {
    private String videoId;
    private String commentId;
    private String timestamp;
    private String comment;
    private List<String> subComments;
}
