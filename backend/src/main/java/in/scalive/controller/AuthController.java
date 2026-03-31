package in.scalive.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.dto.AuthResponseDTO;
import in.scalive.dto.LoginRequestDTO;
import in.scalive.dto.RegisterRequestDTO;
import in.scalive.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "api/auth")
public class AuthController {
	private AuthService authServ;
	
	// Constructor Injection
	public AuthController(AuthService authServ) {
		this.authServ = authServ;
	}
	
	// 1 :  register method
	@PostMapping( "/register")
	public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO request){
		 AuthResponseDTO authDTO = authServ.register(request);
		 return new ResponseEntity<AuthResponseDTO>(authDTO, HttpStatus.CREATED);
	}
	
	// 2:  login method
		@PostMapping( "/login")
		public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO request, HttpSession session){
			 AuthResponseDTO authDTO = authServ.login(request, session);
			 return ResponseEntity.ok(authDTO);
		}
		
		// 3:  logout method
		@PostMapping( "/logout")
		public ResponseEntity<String> logout(HttpSession session){
			authServ.logout( session);
			 return ResponseEntity.ok("Logged Out Successfully!");
		}
			
		// 4:  getCurrentUser method
		@GetMapping( "/me")
		public ResponseEntity<AuthResponseDTO> getCurrentUser(HttpSession session){
			 AuthResponseDTO authDTO = authServ.getCurrentUser(session);
			 return ResponseEntity.ok(authDTO);
		}
			
}
