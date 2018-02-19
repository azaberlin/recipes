package net.aza.recipes.view.details;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Ingredient;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This class provides an ingredient shopping list for the user. It calculates the amount of ingredients based on the
 * given serving size (that means that the ingredient amount needs to be based on a serving size of 1).
 */
@ViewScope
@SpringView(name = IngredientsShoppingList.VIEW_NAME)
class IngredientsShoppingList extends VerticalLayout implements View {
	public static final String VIEW_NAME = "shopping-list";

	@Autowired
	private RecipeRepository repository;
	private Label title;
	private Label servingSizeLabel;

	@PostConstruct
	private void init() {
		setCaption("Einkaufsliste");

		setMargin(true);

		title = new Label();
		title.addStyleName(ValoTheme.LABEL_H2);
		addComponent(title);
		setComponentAlignment(title, Alignment.TOP_CENTER);

		servingSizeLabel = new Label("", ContentMode.HTML);
		servingSizeLabel.addStyleNames(ValoTheme.LABEL_H3, "wrap");
		addComponent(servingSizeLabel);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
		EventIdParser idParser = EventIdParser.of(event);
		if (idParser.isValidParameter()) {
			Recipe recipe = this.repository.findOne(idParser.getId());

			if (recipe != null) {
				title.setValue(recipe.getName());
				servingSizeLabel.setValue("Für <b>" + recipe.getServingSize() + " " + recipe.getServingSizeType().getDisplayNameByServingSize(recipe.getServingSize()) + "</b> benötigtst Du Folgendes:");

				Map<String, Double> summedUpIngredientsMap = new LinkedHashSet<>(recipe.getParts()).stream().flatMap(recipePart -> recipePart.getIngredients().stream()).collect(Collectors.toMap(Ingredient::getUnit, Ingredient::getAmount, Double::sum));
				new TreeMap<>(summedUpIngredientsMap).forEach((k, v) -> addComponent(new Label((v * (double) recipe.getServingSize()) + " " + k)));
			}
		}
	}
}
