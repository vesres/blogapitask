package com.blog;

import com.blogsecurity.SecurityApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SecurityApplication.class)
public class BlogApiTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApiTaskApplication.class, args);
	}
}
