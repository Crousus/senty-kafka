package ch.unisg.senty.projectmanager.rest;

import ch.unisg.senty.projectmanager.application.ProjectService;
import ch.unisg.senty.projectmanager.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostRestController {

    @Autowired
    private ProjectService projectService;

    @PostMapping(path = "/analyze", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPost(@RequestBody Post post) {
        if(projectService.createProjectFromPost(post)) {
            //TODO Dispatch Notification Event post added
            projectService.createProjectFromPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            //TODO Dispatch Notification Event post not successfull
            return ResponseEntity.badRequest().body("No video of ID "+post.getVideoId()+ "could be added!");
        }
    }
}
