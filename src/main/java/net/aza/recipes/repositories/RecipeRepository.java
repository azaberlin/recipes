package net.aza.recipes.repositories;

import net.aza.recipes.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	List<Recipe> findByNameLike(String namePart);
	int countByNameLike(String namePart);
}
