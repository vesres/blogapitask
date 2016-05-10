HOW TO USE THE API(Sorry for not using markdown)
-----------------------------------------------

Type of Authorization: Basic Auth

2 type of users , authorized and anonymous.

Authorized user
--------------
username:bloguser
password:12345

Anonymous user does not need credentials.

Use Content-Type:application/json

POST http://localhost:8080/blogusers/  Creates a user and his/her blog.
Example of valid Json request body: {
"username":"Bill",
"blogName":"Bill's blog"
}

GET           http://localhost:8080/blogusers/:userId Returns user information.
     example: http://localhost:8080/blogusers/1 

POST         http://localhost:8080/blogusers/:userId/categories Creates a category for the user.
    example: http://localhost:8080/blogusers/1/categories 
Example of valid Json request body: {
"title":"Beers"
}

GET          http://localhost:8080/blogusers/:userId/categories Returns the categories of the user.
    example: http://localhost:8080/blogusers/1/categories 

POST         http://localhost:8080/blogusers/:userId/categories/categoryId:/posts Creates a blog post for the category.
    example: http://localhost:8080/blogusers/1/categories/2/posts 
Example of valid Json request body:{
"title":"German beers",
"text":"Everybody likes beers, especially the 

ones from Germany"
}

GET   http://localhost:8080/blogusers/:userId/categories/:categoryId/posts  Returns the blog posts of the category.
      http://localhost:8080/blogusers/1/categories/2/posts 

POST  http://localhost:8080/blogusers/:userId/categories/:categoryId/posts/:postId/upload Uploads a file for the post.
      http://localhost:8080/blogusers/1/categories/2/posts/4/upload 
Content-type: none. Body is form-data: key-> uploadedFile, value -> the file to be uploaded.

DELETE  http://localhost:8080/blogusers/:userId/categories/:categoryId/posts/:postId  Deletes the post of the category.
        http://localhost:8080/blogusers/1/categories/2/posts/4 

POST  http://localhost:8080/blogusers/:userId/categories/:categoryId/posts/:postId/comments Creates a comment for the blog post
      http://localhost:8080/blogusers/1/categories/2/posts/4/comments 
Example of valid Json request body: {
"text":"I like all kind of beers!"
}

GET   http://localhost:8080/blogusers/:userId/categories/:categoryId/posts/:postId/comments Returns the comments of the post.
      http://localhost:8080/blogusers/1/categories/2/posts/4/comments 

DELETE  http://localhost:8080/blogusers/:userId/categories/:categoryId/posts/:postId/comments/:commentId Deletes the comment of       the post.
        http://localhost:8080/blogusers/1/categories/2/posts/4/comments/5 

