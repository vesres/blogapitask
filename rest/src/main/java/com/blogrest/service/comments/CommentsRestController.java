package com.blogrest.service.comments;

import blogmodel.entities.Comment;
import blogmodel.entities.Post;
import blogmodel.repositories.CommentRepository;
import blogmodel.repositories.PostRepository;
import com.blogrest.service.AbstractBlogRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Set;

/**
 * Rest controller handling blog posts services.
 */
@RestController
public class CommentsRestController extends AbstractBlogRestController {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public CommentsRestController(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    /**
     * Returns a set of comments by post id.
     *
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
     *
     * @param postId  the id of the post
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
     *
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
