package net.aza.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import net.aza.recipes.model.Ingredient;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.model.RecipePart;
import net.aza.recipes.model.ServingSizeType;
import net.aza.recipes.repositories.RecipeRepository;

@Push
@PushStateNavigation
@SpringUI
@Theme("recipes")
@SpringViewDisplay
public class RecipesUI extends UI implements ViewDisplay {
	private static final long serialVersionUID = 4432650588283258437L;

	public static final String PATH_BASE_SHOW_RECIPE = "/show";

	@Autowired
	private RecipeRepository repository;

	private VerticalLayout uiLayout;
	private View lastView;

	@Override
	protected void init(final VaadinRequest request) {
		createTestData(); // TODO remove

		uiLayout = initLayout();
		createTitle(uiLayout);

		setContent(uiLayout);
	}

	@Override
	public void showView(final View view) {
		View currentView = getNavigator().getCurrentView();
		if (currentView != null) {
			uiLayout.removeComponent(currentView.getViewComponent());
		}
		uiLayout.addComponentsAndExpand(view.getViewComponent());
	}

	/**
	 * Initializes this UI's basic layout.
	 *
	 * @return basic layout
	 */
	private VerticalLayout initLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		return layout;
	}

	/**
	 * Creates the UI's title line and the returns the root component.
	 *
	 * @param uiLayout
	 * @return title
	 */
	private Component createTitle(final VerticalLayout uiLayout) {
		Label title = new Label(VaadinIcons.CROSS_CUTLERY.getHtml() + "&nbsp;&nbsp;&nbsp;mein rezeptbuch&nbsp;&nbsp;" + VaadinIcons.GLASS.getHtml(), ContentMode.HTML);
		title.addStyleName(ValoTheme.LABEL_H1);
		title.addStyleName(ValoTheme.LABEL_COLORED);
		uiLayout.addComponent(title);
		return title;
	}

	/**
	 * Creates the "url invalid" info component.
	 *
	 * @param uiLayout
	 * @return component
	 */
	private Component createUnknownOrInvalidUrlInfo(final VerticalLayout uiLayout) {
		Label label = new Label(
				"Leider konnten wir die angegebene URL nicht verarbeiten. Wir haben Dir einfach mal die Übersicht geladen. :)");
		label.addStyleName(ValoTheme.LABEL_FAILURE);
		uiLayout.addComponent(label);
		return label;
	}

	/**
	 * Creates the "recipe not found" info component.
	 *
	 * @param uiLayout
	 * @return component
	 */
	private Component createRecipeNotFoundInfo(final VerticalLayout uiLayout) {
		Label label = new Label(
				"Leider konnten wir das angegebene Rezept nicht finden. Aber vielleicht findest Du ja ein anderes, was Dir gefällt :)");
		label.addStyleName(ValoTheme.LABEL_FAILURE);
		uiLayout.addComponent(label);
		return label;
	}

	/*
	 * TO BE REMOVED LATER
	 */
	private void createTestData() {
		Random random = new Random();
		IntStream.rangeClosed('A', 'Z').filter(i -> random.nextBoolean())
				.mapToObj(value -> Character.valueOf((char) value).toString()).forEach(value -> {
			System.out.println("Create test data for letter " + value);
			for (int i = 0; i < random.nextInt(5) + 5; i++) {
				Recipe recipe = new Recipe(value + "-Rezept Nr. " + (i + 1),
						"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \n"
								+ "\n"
								+ "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.",
						random.nextInt(10) + 1, ServingSizeType.PERSON);
				List<RecipePart> parts = new ArrayList<>();
				recipe.setParts(parts);

				int partsAmount = random.nextInt(5);
				for (int j = 0; j < partsAmount + 1; j++) {
					RecipePart recipePart = new RecipePart(recipe, partsAmount > 1 ? "Teil " + (j + 1) : "",
							"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \n"
									+ "\n"
									+ "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.   \n"
									+ "\n"
									+ "Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus.   \n"
									+ "\n"
									+ "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea");
					parts.add(recipePart);

					List<Ingredient> ingredients = new ArrayList<>();
					recipePart.setIngredients(ingredients);

					for (int k = 0; k < random.nextInt(5) + 5; k++) {
						double amount = random.nextInt(5) + 1;
						Ingredient ingredient = new Ingredient(recipePart, amount, " Karotten");
						ingredients.add(ingredient);
					}
				}

				this.repository.save(recipe);
			}

		});
	}
}
