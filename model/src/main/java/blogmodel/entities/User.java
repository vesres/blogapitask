package blogmodel.entities;


import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *  Entity representing a blog user.
 *  The user is the owner of the blog.
 *
 */
@Entity(name = "BLOG_USER")
public class User implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Pattern(regexp = "\\w+",
            message = "error.username.pattern")
    @Column(nullable = false)
    private String username;

    /**
     * Represents the name of the blog
     * which belongs to the user.
     */
    @Column(nullable = false)
    private String blogName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Category> categories = new HashSet<>();

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public Set<Category> getCategories() {
        return categories;
    }

}
