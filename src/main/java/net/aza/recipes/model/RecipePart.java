package net.aza.recipes.model;


import javax.persistence.*;
import java.util.List;

/**
 * A recipe consists of one or more recipe parts. A recipe part can have an own name (recommended if there are multiple
 * recipe parts. Ingredients are always set for recipe parts while the serving size is set on the recipe itself.
 */
@Entity
@Table(name = "recipe_parts")
public class RecipePart extends StorageEntity{

	private String name;

	@Lob
	private String instructions;

	@ManyToOne
	private Recipe recipe;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "recipePart")
	private List<Ingredient> ingredients;

	public RecipePart(Recipe recipe, String name, String instructions) {
		this.recipe = recipe;
		this.name = name;
		this.instructions = instructions;
	}

	public RecipePart() {

	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
}
