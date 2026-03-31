package in.scalive.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorUpdateDTO {

	@Size(min = 1, message = "Ename required!")
    private String name;
	
	@Email(message = "Email should be valid")
    private String email;
    
    @Size(min = 1, message = "About required!")
    private String about;
}
