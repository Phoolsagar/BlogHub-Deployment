package in.scalive.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.dto.PostRequestDTO;
import in.scalive.dto.PostResponseDTO;
import in.scalive.dto.PostUpdateDTO;
import in.scalive.entity.Post;
import in.scalive.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {
	private PostService pServ;

	@Autowired
	public PostController(PostService pServ) {
		this.pServ = pServ;
	}

	@PostMapping
	public ResponseEntity<PostResponseDTO> createPost(@RequestBody @Valid PostRequestDTO request, HttpSession session) {
		Long currenUserId = (Long) session.getAttribute("userId");
		request.setAuthorId(currenUserId);
		Post post = pServ.createPost(request);
		PostResponseDTO resp = new PostResponseDTO();
		resp.setId(post.getId());
		resp.setTitle(post.getTitle());
		resp.setContent(post.getContent());
		resp.setCategoryId(post.getCategory().getId());
		resp.setAuthorId(post.getAuthor().getId());
		resp.setCreatedAt(post.getCreatedAt());
		resp.setCategoryName(post.getCategory().getCatName());
		resp.setAuthorName(post.getAuthor().getName());
		
		return new ResponseEntity<>(resp, HttpStatus.CREATED);
	}

	@GetMapping("/getAll")
	public ResponseEntity<List<PostResponseDTO>> getAllPosts(@RequestParam(required = false) String term) {
		List<Post> postList;
		if (term != null && !term.isBlank()) {
			postList = pServ.searchPosts(term);
		} else {
			postList = pServ.getAllPosts();
		}
		List<PostResponseDTO> respList = new ArrayList<>();
		for (Post post : postList) {
			PostResponseDTO resp = new PostResponseDTO();
			resp.setId(post.getId());
			resp.setTitle(post.getTitle());
			resp.setContent(post.getContent());
			resp.setCategoryId(post.getCategory().getId());
			resp.setAuthorId(post.getAuthor().getId());
			resp.setCreatedAt(post.getCreatedAt());
			resp.setCategoryName(post.getCategory().getCatName());
			resp.setAuthorName(post.getAuthor().getName());
			
			respList.add(resp);
		}
		return ResponseEntity.ok(respList);
	}

	@GetMapping
	public ResponseEntity<Page<PostResponseDTO>> getAllPosts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, @RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		return ResponseEntity.ok(pServ.getAllPosts(page, size, sortBy, sortDir));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
		Post post = pServ.getPostById(id);
		PostResponseDTO resp = new PostResponseDTO();
		resp.setId(post.getId());
		resp.setTitle(post.getTitle());
		resp.setContent(post.getContent());
		resp.setCategoryId(post.getCategory().getId());
		resp.setAuthorId(post.getAuthor().getId());
		resp.setCreatedAt(post.getCreatedAt());
		resp.setCategoryName(post.getCategory().getCatName());
		resp.setAuthorName(post.getAuthor().getName());
		
		return ResponseEntity.ok(resp);
	}

	@GetMapping("/my-post")
	public ResponseEntity<List<PostResponseDTO>> getMyPosts(@RequestAttribute("currentUserId") Long currentUserId) {
		List<Post> postList = pServ.getPostsByAuthor(currentUserId);
		List<PostResponseDTO> respList = new ArrayList<>();
		for (Post post : postList) {
			PostResponseDTO resp = new PostResponseDTO();
			resp.setId(post.getId());
			resp.setTitle(post.getTitle());
			resp.setContent(post.getContent());
			resp.setCategoryId(post.getCategory().getId());
			resp.setAuthorId(post.getAuthor().getId());
			resp.setCreatedAt(post.getCreatedAt());
			resp.setCategoryName(post.getCategory().getCatName());
			resp.setAuthorName(post.getAuthor().getName());
			
			respList.add(resp);
		}
		return ResponseEntity.ok(respList);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostUpdateDTO postUpd,
			@RequestAttribute("currentUserId") Long currentUserId,
			@RequestAttribute("currentUserRole") String currentUserRole) {

		Post post = pServ.getPostById(id);
		if (!post.getAuthor().getId().equals(currentUserId) && !currentUserRole.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("{\"error\":\"You can only update your own posts \"}");
		}
		Post updatedPost = pServ.updatePost(id, postUpd);
		PostResponseDTO resp = new PostResponseDTO();
		resp.setId(updatedPost.getId());
		resp.setTitle(updatedPost.getTitle());
		resp.setContent(updatedPost.getContent());
		resp.setCategoryId(updatedPost.getCategory().getId());
		resp.setAuthorId(updatedPost.getAuthor().getId());
		resp.setCreatedAt(updatedPost.getCreatedAt());
		resp.setCategoryName(updatedPost.getCategory().getCatName());
		resp.setAuthorName(updatedPost.getAuthor().getName());
		
		return ResponseEntity.ok(resp);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePost(@PathVariable Long id,
			@RequestAttribute("currentUserId") Long currentUserId,
			@RequestAttribute("currentUserRole") String currentUserRole) {
		Post post = pServ.getPostById(id);
		if (!post.getAuthor().getId().equals(currentUserId) && !currentUserRole.equals("ADMIN")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body("{\"error\":\"You can only delete your own posts \"}");
		}
		pServ.deletePost(id);
		return ResponseEntity.ok("Post Deleted Successfully");
	}
}
