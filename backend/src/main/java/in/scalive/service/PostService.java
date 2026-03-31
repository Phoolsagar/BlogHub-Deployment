package in.scalive.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import in.scalive.dto.PostRequestDTO;
import in.scalive.dto.PostResponseDTO;
import in.scalive.dto.PostUpdateDTO;
import in.scalive.entity.Author;
import in.scalive.entity.Category;
import in.scalive.entity.Post;
import in.scalive.exception.ResourceNotFoundException;
import in.scalive.repository.AuthorRepository;
import in.scalive.repository.CategoryRepository;
import in.scalive.repository.PostRepository;

@Service
public class PostService {

 
    private PostRepository pRepo;

    private AuthorRepository aRepo;

    private CategoryRepository cRepo;

    @Autowired
    public PostService(PostRepository pRepo, AuthorRepository aRepo, CategoryRepository cRepo) {
		this.pRepo = pRepo;
		this.aRepo = aRepo;
		this.cRepo = cRepo;
	}

	// ✅ CREATE POST
    public Post createPost(PostRequestDTO prDTO) {

        if (prDTO.getAuthorId() == null) {
            throw new RuntimeException("AuthorId not found");
        }

        Author author = aRepo.findById(prDTO.getAuthorId()).orElse(null);
        if (author == null) {
            throw new ResourceNotFoundException("AuthorId with id: " + prDTO.getAuthorId() + " not found!");
        }

        Category cat = cRepo.findById(prDTO.getCategoryId()).orElse(null);
        if (cat == null) {
            throw new ResourceNotFoundException("CategoryId with id: " + prDTO.getCategoryId() + " not found!");
        }

        // ✅ Create Post
        Post post = new Post();
        post.setTitle(prDTO.getTitle());
        post.setContent(prDTO.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(author);
        post.setCategory(cat);

        return pRepo.save(post);
    }

    
    public List<Post> getAllPosts() {
        return pRepo.findAll();
    }
       
   
    
 // ✅ GET ALL (PAGINATED)
    public Page<PostResponseDTO> getAllPosts(int page, int size, String sortBy, String sortDir) {

    	//create sort object
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        
        //create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        //create post object containing post entity
        Page<Post> postsPage = pRepo.findAll(pageable); 
        
        //list of prdto 
        List<PostResponseDTO> dtoList = new ArrayList<>();
        
        // map post --> prdto
        for(Post post : postsPage.getContent()) {
        	PostResponseDTO dto = new PostResponseDTO();
        	dto.setId(post.getId());
        	dto.setTitle(post.getTitle());
        	dto.setContent(post.getContent());
        	dto.setCategoryName(post.getCategory().getCatName());
        	dto.setAuthorId(post.getAuthor().getId());
        	dto.setAuthorName(post.getAuthor().getName());
        	dto.setCreatedAt(post.getCreatedAt());
        	dto.setCategoryId(post.getCategory().getId());
        	
        	dtoList.add(dto);
        }
        
        //map List<prdto> --> post<prdto>
        Page<PostResponseDTO>pageList = new PageImpl<>(dtoList, pageable, postsPage.getTotalElements());
           return pageList;
     }

    // ✅ GET BY ID
    public Post getPostById(Long id) {

        Post post = pRepo.findById(id).orElse(null);
        if(post == null) {
        	throw new ResourceNotFoundException("post not found with id: "+id);
        }

        return post;
    }
    
    public List<Post> searchPosts(String term){
    	return pRepo.findByTitleContainingOrContentContaining(term.toLowerCase(), term.toLowerCase());
    }
    
    
    public List<Post> getPostsByAuthor(Long authorId){
    	 Author author = aRepo.findById(authorId).orElse(null);
         if (author == null) {
             throw new ResourceNotFoundException("AuthorId with id: " + authorId+ " not found!");
         }
         
         return pRepo.findByAuthor(author);
    }
    

    // ✅ UPDATE
    public Post updatePost(Long postId, PostUpdateDTO postUpd) {

        Post post = getPostById(postId);
    if(postUpd == null || postUpd.getTitle() == null && postUpd.getCategoryId() == null && postUpd.getContent() == null) {
        	throw new RuntimeException("At least one field must be present for updation");
        }
        
        if(postUpd.getTitle() != null) {
        	post.setTitle(postUpd.getTitle());
        }
         
        if(postUpd.getContent() != null) {
        	post.setContent(postUpd.getContent());
        }
        
        if(postUpd.getCategoryId() != null) {
        	 Category cat = cRepo.findById(postUpd.getCategoryId()).orElse(null);
             if (cat == null) {
                 throw new ResourceNotFoundException("CategoryId with id: " + postUpd.getCategoryId() + " not found!");
             }
            
            post.setCategory(cat);
        }
       
    return  pRepo.save(post);

 }

    // ✅ DELETE
    public void deletePost(Long id) {

        Post post = getPostById(id);

        pRepo.delete(post);
    }

   
}

    