package in.scalive.service;

import java.util.List;

import org.springframework.stereotype.Service;

import in.scalive.dto.AuthorResponseDTO;
import in.scalive.dto.AuthorUpdateDTO;
import in.scalive.entity.Author;
import in.scalive.exception.ResourceNotFoundException;
import in.scalive.repository.AuthorRepository;
import in.scalive.repository.PostRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepo;
    private final PostRepository postRepo;
    
    // getting all authors
    public List<AuthorResponseDTO> getAllUsers() {
        List<Author> authors = authorRepo.findAll();
        return authors.stream()
                .map(author -> new AuthorResponseDTO(
                        author.getId(),
                        author.getName(),
                        author.getEmail(),
                        author.getRole(),
                        author.getAbout(),
                        postRepo.countByAuthorId(author.getId())
                ))
                .toList();
    }

    public int getPostCountByAuthorId(Long authorId) {
        return postRepo.countByAuthorId(authorId);
    }
    
    // getting single author
    public Author getUserById(Long id) {
        Author author = authorRepo.findById(id).orElse(null);
        
        if(author == null) {
        	throw new ResourceNotFoundException("No author with Id: "+id+" found!");
        }
        
        return author;   
    }

    	// updating  author
    public Author updateUser(Long id, AuthorUpdateDTO updAuthor) {

        Author author = getUserById(id);
        
        if(updAuthor.getName() == null && updAuthor.getEmail() == null && updAuthor.getAbout() == null) {
        	throw new RuntimeException("Empty object not allowed!");
        }
        
        if(updAuthor.getName() != null && updAuthor.getName().isBlank()) {
        	throw new RuntimeException("Name cannot be blank!");
        }
        
        if(updAuthor.getAbout() != null && updAuthor.getAbout().isBlank()) {
        	throw new RuntimeException("About cannot be blank!");
        }

        // updating author details
        if(updAuthor.getName() != null) {
        	author.setName(updAuthor.getName());
        }
        
        if(updAuthor.getEmail() != null) {
        	author.setEmail(updAuthor.getEmail());
        }
        
        if(updAuthor.getAbout() != null) {
        	author.setAbout(updAuthor.getAbout());
        }
 
     return authorRepo.save(author);

       
    }

    	//deleting author 
    public void deleteuser(Long id) {
    	Author author = getUserById(id);
    	authorRepo.delete(author);
    	
        
    }
}