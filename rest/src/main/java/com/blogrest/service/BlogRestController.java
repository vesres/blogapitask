package com.blogrest.service;

import blogmodel.entities.Category;
import blogmodel.entities.Comment;
import blogmodel.entities.Post;
import blogmodel.entities.User;
import blogmodel.repositories.CategoryRepository;
import blogmodel.repositories.CommentRepository;
import blogmodel.repositories.PostRepository;
import blogmodel.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 */
@RestController
@RequestMapping("/blogusers/")
@MultipartConfig
public class BlogRestController {

    private static final Logger LOGGER = Logger.getLogger(BlogRestController.class.getName());
    private static final String UPLOAD_FILE_ERR_MSG = "IOException while uploading file to blog post.File name: ";
    private static final String FILE_TYPE_ERR = "Only pdf,jpg,png files are allowed." +
            "No file uploaded.";
    private static final String POST_DELETION_ERR_MSG = "Exception while deleting post.";
    private static final String IMAGES_RELATIVE_PATH = "\\src\\main\\resources\\images\\";

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;


    @Autowired
    public BlogRestController(UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              PostRepository postRepository,
                              CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }


    /*
        User Methods
      */

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

    /*
        Category Methods
      */

    /**
     * Returns the categories of the user's blog.
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
     * @param userId the id of the user
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

    /*
        Blog Posts Methods
     */

    /**
     * Returns the posts of the blog by category.
     * @param categoryId the id of the category
     * @return a list of posts
     */
    @RequestMapping(value = "/{userId}/categories/{categoryId}/posts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Post> getBlogPostsByCategory(@PathVariable Long categoryId) {
        Category category = categoryRepository.findOne(categoryId);
        if (category != null) return category.getPosts();
        else return Collections.emptyList();
    }

    /**
     * Creates a post of the blog.
     * @param categoryId the id of the category of the blog
     * @param post the post
     * @return a response based on the result of the creation
     */
    @RequestMapping(value = "/{userId}/categories/{categoryId}/posts",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createBlogPost(@PathVariable Long categoryId,
                                                 @RequestBody Post post) {
        Category category = categoryRepository.findOne(categoryId);
        if (category != null) {
            if (post.getFilename() != null)
                return new ResponseEntity<>("Filename must be uploaded, not passed as request body value.",
                        HttpStatus.BAD_REQUEST);
            post.setCategory(category);
            category.getPosts().add(post);
            postRepository.save(post);
            categoryRepository.save(category);
            return new ResponseEntity<>("Blog post was created successfully.", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("No category exists therefore no post was created.", HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a post of the blog.
     * @param postId the id of the post
     * @return a response based on the result of the deletion
     */
    @RequestMapping(value = "/{userId}/categories/{categoryId}/posts/{postId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBlogPost(@PathVariable Long postId) {
        Post blogPost = postRepository.findOne(postId);
        if (blogPost != null) {
            deletePost(postId, blogPost);
            return new ResponseEntity<>("Blog post was deleted successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("No post existed and no deletion has taken place.", HttpStatus.NOT_FOUND);
    }

    private void deletePost(Long postId, Post blogPost) {
        File file = new File("");
        String path = file.getAbsolutePath() + "\\src\\main\\resources\\images\\"
                + blogPost.getFilename();
        file = new File(path);
        try {
            postRepository.delete(postId);
            file.delete();
        } catch (Exception ex) {
            LOGGER.severe(POST_DELETION_ERR_MSG);
            ex.printStackTrace();
        }
    }

    /**
     * Uploads a file (only *.pdf, *.jpg, *.jpeg, *.png allowed) for the post.
     * @param postId the id of the post
     * @param uploadedFileRef the uploaded file
     * @return a String response based on the result of the creation
     */
    @RequestMapping(value = "/{userId}/categories/{categoryId}/posts/{postId}/upload",
            method = RequestMethod.POST, headers = "content-type=multipart/form-data")
    public String uploadFile(@PathVariable Long postId,
                             @RequestParam("uploadedFile")
                                     MultipartFile uploadedFileRef) {
        String fileName = uploadedFileRef.getOriginalFilename();
        if (!isUploadedFileValid(fileName)) {
            return FILE_TYPE_ERR;
        }
        File file = new File("");
        String path = file.getAbsolutePath() + IMAGES_RELATIVE_PATH + fileName;
        try {
            uploadedFileRef.transferTo(new File(path));
            Post post = postRepository.findOne(postId);
            post.setFilename(fileName);
            postRepository.save(post);
        } catch (IOException e) {
            LOGGER.severe(UPLOAD_FILE_ERR_MSG + fileName);
            e.printStackTrace();
        }
        return "File was uploaded successfully.Total bytes read: " + uploadedFileRef.getSize()
                + " for file with name: " + uploadedFileRef.getOriginalFilename();
    }


    private boolean isUploadedFileValid(String fileName) {
        return fileName.endsWith("pdf") || fileName.endsWith("jpg")
                || fileName.endsWith("jpeg") || fileName.endsWith("png");
    }

     /*
        Blog Comments Methods
     */

    /**
     * Returns a set of comments by post id.
     * @param postId the id of the post
     * @return a set of comments
     */
    @RequestMapping(value = "/{userId}/categories/{categoryId}/posts/{postId}/comments", method = RequestMethod.GET,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Comment> getCommentsByBlogPostId(@PathVariable Long postId) {
        Post blogPost = postRepository.findOne(postId);
        if (blogPost != null) {
            return blogPost.getComments();
        }
        return Collections.emptySet();
    }

    /**
     * Creates a comment of the post.
     * @param postId the id of the post
     * @param comment the comment
     * @return a response based on the result of the creation
     */
    @RequestMapping(value = "/{userId}/categories/{categoryId}/posts/{postId}/comments",
            method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createComment(@PathVariable Long postId,
                                                @RequestBody Comment comment) {
        Post blogPost = postRepository.findOne(postId);
        if ((blogPost != null)) {
            comment.setPost(blogPost);
            blogPost.getComments().add(comment);
            commentRepository.save(comment);
            postRepository.save(blogPost);
            return new ResponseEntity<>("Comment was created successfully.", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("No post exists therefore no comment was created.", HttpStatus.NOT_FOUND);
    }

    /**
     * Deletes a comment of the post.
     * @param commentId the id of the comment.
     * @return a response based on the result of the deletion
     */
    @RequestMapping(value = "/{userId}/categories/{categoryId}/posts/{postId}/comments/{commentId}",
            method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        Comment comment = commentRepository.findOne(commentId);
        if (comment != null) {
            commentRepository.delete(comment);
            return new ResponseEntity<>("Comment was deleted successfully.", HttpStatus.OK);
        }
        return new ResponseEntity<>("No comment existed and no deletion has taken place.", HttpStatus.NOT_FOUND);
    }


}
