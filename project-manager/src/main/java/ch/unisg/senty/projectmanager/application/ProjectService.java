package ch.unisg.senty.projectmanager.application;

import ch.unisg.senty.projectmanager.domain.Post;
import ch.unisg.senty.projectmanager.messages.Message;
import ch.unisg.senty.projectmanager.messages.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectService {

    @Autowired
    private MessageSender messageSender;
    public boolean createProjectFromPost(Post post) {
        Message<Post> message = new Message<>("PostAddedEvent", post);
        messageSender.send(message);

        return true;
    }

}
