package in.scalive.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.scalive.dto.CategoryRequestDTO;
import in.scalive.dto.CategoryResponseDTO;
import in.scalive.dto.CategoryUpdateDTO;
import in.scalive.entity.Category;
import in.scalive.exception.ResourceAlreadyExistsException;
import in.scalive.exception.ResourceNotFoundException;
import in.scalive.repository.CategoryRepository;
import in.scalive.repository.PostRepository;

@Service
public class CategoryService {
	private CategoryRepository cRepo;

	@Autowired
	public CategoryService(CategoryRepository cRepo) {
		this.cRepo = cRepo;
	}
	
	@Autowired
	private PostRepository postRepository;

	public CategoryResponseDTO createCategory(CategoryRequestDTO catReq) {
		if (cRepo.existsByCatName(catReq.getCatName())) {
			throw new ResourceAlreadyExistsException("This category name is already present!");
		}
		Category category = new Category();
		category.setCatName(catReq.getCatName());
		category.setDescription(catReq.getDescr());
		
		Category savedCat = cRepo.save(category);
		
		return new CategoryResponseDTO(
			    savedCat.getId(),
			    savedCat.getCatName(),
			    savedCat.getDescription(),
			    0   // new category → 0 posts
			);
	}

	public List<CategoryResponseDTO> getAllCategories(){
	    List<Category>catList = cRepo.findAll();
	    List<CategoryResponseDTO> catRespList = new ArrayList<>();

	    for(Category cat: catList) {
	        // 🔥 ADD COUNT HERE
	        int count = postRepository.countByCategoryId(cat.getId());

	        catRespList.add(
	            new CategoryResponseDTO(
	                cat.getId(),
	                cat.getCatName(),
	                cat.getDescription(),
	                count   // 🔥 IMPORTANT
	            )
	        );
	    }
	    return catRespList;
	}

	public CategoryResponseDTO getCategoryById(Long id) {
		Category cat = cRepo.findById(id).orElse(null);
		if (cat == null) {
			throw new ResourceNotFoundException("No category present by id: " + id);
		}
	
		
		int count = postRepository.countByCategoryId(cat.getId());

		return new CategoryResponseDTO(
		    cat.getId(),
		    cat.getCatName(),
		    cat.getDescription(),
		    count
		);
	}

	public CategoryResponseDTO updateCategory(Long id, CategoryUpdateDTO dto) {
		Category cat = cRepo.findById(id).orElse(null);
		if (cat == null) {
			throw new ResourceNotFoundException("No category present by id:" + id);
		}
		if (dto == null || (dto.getCatName() == null && dto.getDescr() == null)) {
			throw new IllegalArgumentException("At least one field must be present for updating!");
		}
		if (dto.getCatName() != null) {
			cat.setCatName(dto.getCatName());
		}
		if (dto.getDescr() != null) {
			cat.setDescription(dto.getDescr());
		}
		Category updatedCat = cRepo.save(cat);
		
		int count = postRepository.countByCategoryId(updatedCat.getId());

		return new CategoryResponseDTO(
		    updatedCat.getId(),
		    updatedCat.getCatName(),
		    updatedCat.getDescription(),
		    count
		);
	}

	public void deleteCategory(Long id) {
		Category cat = cRepo.findById(id).orElse(null);
		if (cat == null) {
			throw new ResourceNotFoundException("No category present by id: " + id);
		}
		cRepo.delete(cat);
	}
	
	
}
