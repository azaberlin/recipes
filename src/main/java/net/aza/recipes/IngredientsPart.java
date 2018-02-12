package net.aza.recipes;

import java.util.List;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import net.aza.recipes.model.Ingredient;

public class IngredientsPart extends VerticalLayout {
	public IngredientsPart(final int servingSize, final List<Ingredient> list) {
		// TODO extract ingredients to separated, updateable components.

		list.forEach(i -> {
			addComponent(new Label("<b>" + i.getAmount() + "</b> " + i.getUnit(), ContentMode.HTML));
		});

	}
}