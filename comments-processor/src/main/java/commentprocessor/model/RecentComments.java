package commentprocessor.model;;

import lombok.Data;

import java.util.ArrayDeque;

import java.util.Queue;

@Data
public class RecentComments {

    private final Queue<Comment> comments = new ArrayDeque<>();

    public RecentComments add(final Comment comment) {
        comments.add(comment);

        if (comments.size() > 5) {
            comments.remove();
        }
        return this;
    }

}

