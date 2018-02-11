package net.aza.recipes.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A single ingredient of a recipe part.
 */
@Entity
@Table(name = "ingredients")
public class Ingredient extends StorageEntity {
	private double amount;
	private String unit;

	@ManyToOne
	private RecipePart recipePart;

	public Ingredient() {

	}

	public Ingredient(RecipePart recipePart, double amount, String unit) {
		this.amount = amount;
		this.unit = unit;
		this.recipePart = recipePart;
	}

	public RecipePart getRecipePart() {
		return recipePart;
	}

	public void setRecipePart(RecipePart recipePart) {
		this.recipePart = recipePart;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
