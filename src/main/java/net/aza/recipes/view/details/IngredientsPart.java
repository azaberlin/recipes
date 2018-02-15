package net.aza.recipes.view.details;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import net.aza.recipes.model.Ingredient;

import java.util.Collection;

/**
 * Displays a list of ingredients. The amount of ingredients shown is calculated based on the given serving size.
 * This means that the ingredients original amount must be based on a serving size of 1.
 */
class IngredientsPart extends VerticalLayout {
	private Collection<Ingredient> ingredients;

	IngredientsPart(final int servingSize, final Collection<Ingredient> list) {
		ingredients = list;
		// TODO extract ingredients to separated, updateable components.

		setDefaultComponentAlignment(Alignment.TOP_CENTER);

		updateIngredients(servingSize);
	}

	public void updateIngredients(int servingSize) {
		removeAllComponents();
		ingredients.forEach(i -> {
			Label label = new Label("<b>" + (i.getAmount() * servingSize) + "</b> " + i.getUnit(), ContentMode.HTML);
			label.addStyleName("highlight");
			addComponent(label);
		});
	}


}