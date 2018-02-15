package net.aza.recipes.view.details;

import com.vaadin.icons.VaadinIcons;
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
	IngredientsShoppingList(Recipe recipe, int currentSetServingSize) {
		center();
		setModal(true);
		setCaptionAsHtml(true);
		setCaption("Einkaufsliste für <b>" + recipe.getName() + "</b>");
		setIcon(VaadinIcons.CART_O);

		setHeight(60, Unit.PERCENTAGE);

		VerticalLayout windowLayout = new VerticalLayout();
		windowLayout.setMargin(true);

		Label servingSizeLabel = new Label("Für <b>" + currentSetServingSize + " " + recipe.getServingSizeType().getDisplayNameByServingSize(currentSetServingSize) + "</b> benötigst Du folgende Zutaten :)", ContentMode.HTML);
		windowLayout.addComponent(servingSizeLabel);

		Map<String, Double> summedUpIngredientsMap = new LinkedHashSet<>(recipe.getParts()).stream().flatMap(recipePart -> recipePart.getIngredients().stream()).collect(Collectors.toMap(Ingredient::getUnit, Ingredient::getAmount, Double::sum));
		new TreeMap<>(summedUpIngredientsMap).forEach((k, v) -> windowLayout.addComponent(new Label(VaadinIcons.CIRCLE_THIN.getHtml() + "&nbsp;&nbsp;&nbsp;&nbsp;" + (v * (double) currentSetServingSize) + " " + k, ContentMode.HTML)));

		setContent(windowLayout);
	}
}
