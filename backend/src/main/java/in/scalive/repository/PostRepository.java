package in.scalive.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.scalive.entity.Author;
import in.scalive.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByTitleContainingOrContentContaining(String title, String contentt);
	List<Post> findByAuthor(Author author); 
	int countByAuthorId(Long authorId);
	
	int countByCategoryId(Long categoryId); // for post counting
}
