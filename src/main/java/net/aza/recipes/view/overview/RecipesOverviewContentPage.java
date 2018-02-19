package net.aza.recipes.view.overview;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.repositories.RecipeRepository;
import net.aza.recipes.view.URIFragmentBuilder;
import net.aza.recipes.view.details.RecipeDetails;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * A lazy loading page that shows recipes of a certain category (like all recipes starting with the
 * letter "A").
 *
 * @author Stefan Uebe
 */
class RecipesOverviewContentPage extends Panel {
	private final String category;
	private final RecipeRepository repository;
	private final VerticalLayout layout;

	/**
	 * Creates a new page for a certain category (i. e. "A").
	 *
	 * @param category - category
	 */
	RecipesOverviewContentPage(final String category, RecipeRepository repository) {
		this.category = category;
		this.repository = repository;

		layout = new VerticalLayout();

		layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		setContent(layout);
		setSizeFull();
		addStyleName("recipes-overview-content-page");
		addStyleName("recipes-content-container");
	}

	public String getCategory() {
		return this.category;
	}

	/**
	 * Clears this page and removes all the recipes.
	 */
	public void clearPage() {
		layout.removeAllComponents();
	}

	/**
	 * Load this category's recipes from repository and show them on this page.
	 * Use {@link #clearPage()} before if you want to have a clean page.
	 */
	public void loadAndShowRecipes() {
		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		layout.addComponent(progressBar);

		Executors.newSingleThreadExecutor().submit(() -> {
			List<Recipe> list = loadRecipesFromRepository();

			getUI().access(() -> {
				addRecipes(list);
				progressBar.setVisible(false);
			});
		});
	}

	/**
	 * Loads the recipes of this page's category from the repository and returns them.
	 *
	 * @return recipes of this category
	 */
	private List<Recipe> loadRecipesFromRepository() {
		return this.repository.findAllByNameLike(this.category + "%");
	}

	/**
	 * Adds the given recipe list to the page. Use {@link #clearPage()} if you want to clear the page
	 * before.
	 *
	 * @param list - recipes to add.
	 */
	private void addRecipes(final List<Recipe> list) {
		list.forEach(recipe -> {

			HorizontalLayout titleLayout = new HorizontalLayout();
			titleLayout.addStyleName("title-bar");

			Label recipeTitle = new Label(recipe.getName());
			recipeTitle.addStyleName(ValoTheme.LABEL_H2);
			recipeTitle.addStyleName(ValoTheme.LABEL_COLORED);

			titleLayout.addComponent(recipeTitle);

			String uriFragment = URIFragmentBuilder.of(recipe).getFragment();

			Link showRecipeDetails = new Link();
			showRecipeDetails.setIcon(VaadinIcons.ANGLE_RIGHT);
			showRecipeDetails.setResource(new ExternalResource(RecipeDetails.VIEW_NAME + uriFragment));
			showRecipeDetails.setTargetName("_blank");
			showRecipeDetails.setDescription("Rezept öffnen");
			showRecipeDetails.addStyleName(ValoTheme.LABEL_H2);

			titleLayout.addComponent(showRecipeDetails);
			titleLayout.setComponentAlignment(showRecipeDetails, Alignment.MIDDLE_LEFT);

			layout.addComponent(titleLayout);

			String description = recipe.getDescription();
			int length = description.length();
			Label receiptDescription = new Label(
					length > 300 ? description.substring(0, 300) + "..." : description);
			receiptDescription.setWidth(50, Unit.PERCENTAGE);

			layout.addComponent(receiptDescription);

		});
	}
}
