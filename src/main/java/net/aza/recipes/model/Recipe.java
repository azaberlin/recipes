package net.aza.recipes.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A simple recipe. Each recipe consists of one or multiple recipe parts which contains the ingredients.
 */
@Entity
@Table(name = "recipes")
public class Recipe extends StorageEntity {
	@NotNull
	private String name;

	@Lob
	private String description;

	@ElementCollection
	private List<String> tags;

	@Min(1)
	private int servingSize = 1;

	@NotNull
	private ServingSizeType servingSizeType = ServingSizeType.PERSON;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "recipe")
	private List<RecipePart> parts;

	public Recipe(String name, int servingSize, ServingSizeType servingSizeType) {
		this.name = name;
		this.servingSize = servingSize;
		this.servingSizeType = servingSizeType;
	}

	public Recipe(String name, String description, int servingSize, ServingSizeType servingSizeType) {
		this.name = name;
		this.description = description;
		this.servingSize = servingSize;
		this.servingSizeType = servingSizeType;
	}

	public Recipe() {

	}

	public List<String> getTags() {

		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public int getServingSize() {
		return servingSize;
	}

	public void setServingSize(int servingSize) {
		this.servingSize = servingSize;
	}

	public ServingSizeType getServingSizeType() {
		return servingSizeType;
	}

	public void setServingSizeType(ServingSizeType servingSizeType) {
		this.servingSizeType = servingSizeType;
	}

	public List<RecipePart> getParts() {
		return parts;
	}

	public void setParts(List<RecipePart> parts) {
		this.parts = parts;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
