package in.scalive.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import in.scalive.dto.AuthorResponseDTO;
import in.scalive.dto.AuthorUpdateDTO;
import in.scalive.entity.Author;
import in.scalive.service.AuthorService;


@RestController
@RequestMapping("/api/users")
public class AuthorController {

    private  AuthorService authorServ;
    
    @Autowired
    public AuthorController(AuthorService authorServ) {
    	this.authorServ = authorServ;
    }

    

    @PostMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getUserById(@PathVariable Long id) {

        Author author = authorServ.getUserById(id);
        int postCount = authorServ.getPostCountByAuthorId(author.getId());

        AuthorResponseDTO response = new AuthorResponseDTO(
                id,
                author.getName(),
                author.getEmail(),
                author.getRole(),
            author.getAbout(),
            postCount
        );

        return ResponseEntity.ok(response);
    }

    // Update user (WITH SESSION DATA)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody AuthorUpdateDTO authUpdate,
            @RequestAttribute("currentUserId") Long currentUserId,
            @RequestAttribute("currentUserRole") String currentUserRole) {

        // Authorization logic (important)
        if (!currentUserRole.equals("ADMIN") && !id.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
            		.body("{\"error\": You can only update your own profile.\"}");
        }

        Author updatedAuthor = authorServ.updateUser(id, authUpdate);
        int postCount = authorServ.getPostCountByAuthorId(updatedAuthor.getId());
        
        AuthorResponseDTO responseDTO = new AuthorResponseDTO(updatedAuthor.getId(),updatedAuthor.getName(), updatedAuthor.getEmail(), updatedAuthor.getRole(), updatedAuthor.getAbout(), postCount);
        return ResponseEntity.ok(responseDTO);
    }
    
    //Get All users
    @GetMapping
	public ResponseEntity<List<AuthorResponseDTO>> getAllUsers(){
        	return ResponseEntity.ok(authorServ.getAllUsers());
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @RequestAttribute("currentUserId") Long currentUserId,
            @RequestAttribute("currentUserRole") String currentUserRole) {

        if (!currentUserRole.equals("ADMIN") && !id.equals(currentUserId)) {
            return ResponseEntity.status(403)
                    .body("{\"error\": You can only delete your own account.\"}");
        }

        authorServ.deleteuser(id);
        return ResponseEntity.ok("User Deleted Successfully!");
    }
}