package com.blogrest.service.categories;

import blogmodel.entities.Category;
import blogmodel.entities.User;
import blogmodel.repositories.CategoryRepository;
import blogmodel.repositories.UserRepository;
import com.blogrest.service.AbstractBlogRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Rest controller handling categories services.
 */
@RestController
public class CategoriesRestController extends AbstractBlogRestController {
    private final UserRepository userRepository;

    @Autowired
    public CategoriesRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the categories of the user's blog.
     *
     * @param userId the id of the user
     * @return a set of categories
     */
    @RequestMapping(value = "/{userId}/categories", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Category> getCategoriesByUser(@PathVariable Long userId) {
        User user = userRepository.findOne(userId);
        return user.getCategories();
    }

    /**
     * Creates a category for the user's blog.
     *
     * @param userId   the id of the user
     * @param category the category of the blog
     * @return a response based on the result of the creation
     */
    @RequestMapping(value = "/{userId}/categories",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createCategory(@PathVariable Long userId,
                                                 @RequestBody Category category) {
        User user = userRepository.findOne(userId);
        category.setUser(user);
        user.getCategories().add(category);
        userRepository.save(user);
        return new ResponseEntity<>("Category was created successfully.", HttpStatus.CREATED);
    }
}
