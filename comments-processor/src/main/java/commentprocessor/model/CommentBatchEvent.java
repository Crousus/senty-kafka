package commentprocessor.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CommentBatchEvent {
    private String videoId;
    private String timestamp;
    private List<Comment> comments;
}
