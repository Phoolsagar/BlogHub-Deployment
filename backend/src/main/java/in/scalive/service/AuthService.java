package in.scalive.service;

import org.springframework.stereotype.Service;

import in.scalive.dto.AuthResponseDTO;
import in.scalive.dto.LoginRequestDTO;
import in.scalive.dto.RegisterRequestDTO;
import in.scalive.entity.Author;
import in.scalive.exception.ResourceAlreadyExistsException;
import in.scalive.exception.ResourceNotFoundException;
import in.scalive.repository.AuthorRepository;
import jakarta.servlet.Registration;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

@Service
public class AuthService {

	private final AuthorRepository authRepo;

	// Constructor Injection
	public AuthService(AuthorRepository authRepo) {
		this.authRepo = authRepo;
	}

//	    Registration method 
	public AuthResponseDTO register(RegisterRequestDTO request) {

		// Step: 1 Check if email already exists
		if (authRepo.existsByEmail(request.getEmail())) {
			throw new ResourceAlreadyExistsException("Email already registered");
		}

		// Step: 2 Create new Author object
		Author author = new Author();
		author.setName(request.getName());
		author.setEmail(request.getEmail());
		author.setPassword(request.getPassword()); // ⚠️ No encryption
		author.setAbout(request.getAbout());
		author.setRole("USER");

		// Step: 3 Save to DB
		Author savedAuthor = authRepo.save(author);

		// Step: 4 Return response
		return new AuthResponseDTO(
						savedAuthor.getId(),
						savedAuthor.getName(),
						savedAuthor.getEmail(),
						savedAuthor.getRole(), 
						"Registration successful"
						);
	}

	// LOGIN METHOD
	public AuthResponseDTO login(LoginRequestDTO request, HttpSession session) {

	        // step: 1 Find user by email
	        Author author = authRepo.findByEmail(request.getEmail()).orElse(null);
	        	
	        	// step: 2 Check email
	        	if(author == null) {
	        		throw new ResourceNotFoundException("Invalid Email of Password!");
	        	
	        	}
	        	// step: 3 Check password
	        	if(!author.getPassword().equals(request.getPassword())) {
	        		throw new ResourceNotFoundException("Invalid Email of Password!");
	        	}
	        
	     // step: 4 setting session
	        session.setAttribute("userId", author.getId());
	        session.setAttribute("userRole", author.getRole());
	        session.setAttribute("userName", author.getName());
	        session.setAttribute("userEmail", author.getEmail());

	        //  step: 5 Return success response
	        return new AuthResponseDTO(
	                author.getId(),
	                author.getName(),
	                author.getEmail(),
	                author.getRole(),
	                "Login successful"
	        );
	    }
	
	public void logout(HttpSession session) {
		session.invalidate();
	}
	
	public AuthResponseDTO getCurrentUser(HttpSession session) {
		Long userId = (Long)session.getAttribute("userId");
		
		if(userId == null) {
			throw new ResourceNotFoundException("No user logged-in!");
		}
		
		String userName = (String)session.getAttribute("userName");
		String userEmail = (String)session.getAttribute("userEmail");
		String userRole = (String)session.getAttribute("userRole");
		
		return new AuthResponseDTO(
				userId,
				userName,
				userEmail,
				userRole,
				"Current User Data"
				);
				
		
	}
}

