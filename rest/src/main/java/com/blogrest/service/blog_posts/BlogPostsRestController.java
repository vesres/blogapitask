package com.blogrest.service.blog_posts;

import blogmodel.entities.Category;
import blogmodel.entities.Post;
import blogmodel.repositories.CategoryRepository;
import blogmodel.repositories.PostRepository;
import com.blogrest.service.AbstractBlogRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Rest controller handling blog posts services.
 */
@RestController
@MultipartConfig
public class BlogPostsRestController extends AbstractBlogRestController {
    private static final Logger LOGGER = Logger.getLogger(BlogPostsRestController.class.getName());
    private static final String POST_DELETION_ERR_MSG = "Exception while deleting post.";
    private static final String FILE_TYPE_ERR = "Only pdf,jpg,png files are allowed." +
            "No file uploaded.";
    private static final String UPLOAD_FILE_ERR_MSG = "IOException while uploading file to blog post.File name: ";
    private static final String IMAGES_RELATIVE_PATH = "\\src\\main\\resources\\images\\";

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    @Autowired
    public BlogPostsRestController(CategoryRepository categoryRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
    }

    /**
     * Returns the posts of the blog by category.
     *
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
     *
     * @param categoryId the id of the category of the blog
     * @param post       the post
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
     *
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
     *
     * @param postId          the id of the post
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
}
