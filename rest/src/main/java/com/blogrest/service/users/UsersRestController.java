package com.blogrest.service.users;

import blogmodel.entities.User;
import blogmodel.repositories.UserRepository;
import com.blogrest.service.AbstractBlogRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Rest controller handling user services.
 */
@RestController
public class UsersRestController extends AbstractBlogRestController {
    private final UserRepository userRepository;

    @Autowired
    public UsersRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the user of the blog.
     * @param userId the id of the user
     * @return the user of the blog
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public User getBlogUser(@PathVariable Long userId) {
        return userRepository.findOne(userId);
    }

    /**
     * Creates the user of the blog.
     * @param user the user to create
     * @return a response based on the result of the creation
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createUser(@RequestBody @Valid User user) {
        userRepository.save(user);
        return new ResponseEntity<>("User with id: " + user.getId() +
                " created and his/her blog were created successfully.", HttpStatus.CREATED);
    }
}
