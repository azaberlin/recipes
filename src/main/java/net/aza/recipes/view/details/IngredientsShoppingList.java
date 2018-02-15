package net.aza.recipes.view.details;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Ingredient;
import net.aza.recipes.model.Recipe;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This class provides an ingredient shopping list for the user. It calculates the amount of ingredients based on the
 * given serving size (that means that the ingredient amount needs to be based on a serving size of 1).
 */
class IngredientsShoppingList extends Window {
	IngredientsShoppingList(Recipe recipe) {
		center();
		setModal(true);
		setCaption("Einkaufsliste");

		VerticalLayout windowLayout = new VerticalLayout();
		windowLayout.setMargin(true);

		Label title = new Label(recipe.getName());
		title.addStyleName(ValoTheme.LABEL_H2);
		windowLayout.addComponent(title);
		windowLayout.setComponentAlignment(title, Alignment.TOP_CENTER);

		Label servingSizeLabel = new Label("Für <b>" + recipe.getServingSize() + " " + recipe.getServingSizeType().getDisplayNameByServingSize(recipe.getServingSize()) + "</b> benötigtst Du Folgendes:", ContentMode.HTML);
		servingSizeLabel.addStyleNames(ValoTheme.LABEL_H3, "wrap");
		windowLayout.addComponent(servingSizeLabel);

		Map<String, Double> summedUpIngredientsMap = new LinkedHashSet<>(recipe.getParts()).stream().flatMap(recipePart -> recipePart.getIngredients().stream()).collect(Collectors.toMap(Ingredient::getUnit, Ingredient::getAmount, Double::sum));
		new TreeMap<>(summedUpIngredientsMap).forEach((k, v) -> windowLayout.addComponent(new Label((v * (double) recipe.getServingSize()) + " " + k)));

		setContent(windowLayout);
	}
}
