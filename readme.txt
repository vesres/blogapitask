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

GET  http://localhost:8080/blogusers/1 Returns user information.

POST http://localhost:8080/blogusers/1/categories Creates a category for the user.
Example of valid Json request body: {
"title":"Beers"
}

GET http://localhost:8080/blogusers/1/categories Returns the categories of the user.

POST http://localhost:8080/blogusers/1/categories/2/posts Creates a blog post for the category.
Example of valid Json request body:{
"title":"German beers",
"text":"Everybody likes beers, especially the 

ones from Germany"
}

GET http://localhost:8080/blogusers/1/categories/2/posts Returns the blog posts of the category.

POST http://localhost:8080/blogusers/1/categories/2/posts/4/upload Uploads a file for the post.
Content-type: none. Body is form-data: key-> uploadedFile, value -> the file to be uploaded.

DELETE http://localhost:8080/blogusers/1/categories/2/posts/4 Deletes the post of the category.

POST http://localhost:8080/blogusers/1/categories/2/posts/4/comments Creates a comment for the blog post.
Example of valid Json request body: {
"text":"I like all kind of beers!"
}

GET http://localhost:8080/blogusers/1/categories/2/posts/4/comments Returns the comments of the post.

DELETE http://localhost:8080/blogusers/1/categories/2/posts/4/comments/5 Deletes the comment of the post.

