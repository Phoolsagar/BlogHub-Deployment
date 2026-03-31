package in.scalive.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    
    private String categoryName;
    private Long categoryId;
   
    private Long authorId;
    private String authorName;

    private LocalDateTime createdAt;
}