package blogmodel.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

/**
 *  Entity representing a comment of the blog.
 *  A comment can be made on a post of the blog.
 *  Every user, authenticated or not, can create
 *  a comment but only the blog owner can remove it.
 */
@Entity
public class Comment implements Serializable {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    @JsonIgnore
    private Post post;

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
