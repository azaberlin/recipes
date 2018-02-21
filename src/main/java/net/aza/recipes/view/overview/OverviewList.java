package net.aza.recipes.view.overview;

import com.vaadin.ui.*;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.repositories.RecipeRepository;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * A lazy loading page that shows recipes of a certain category (like all recipes starting with the
 * letter "A").
 *
 * @author Stefan Uebe
 */
class OverviewList extends Panel {
	private final String category;
	private final RecipeRepository repository;
	private final VerticalLayout list;

	/**
	 * Creates a new page for a certain category (i. e. "A").
	 *
	 * @param category - category
	 */
	OverviewList(final String category, RecipeRepository repository) {
		this.category = category;
		this.repository = repository;

		list = new VerticalLayout();
		list.setDefaultComponentAlignment(Alignment.TOP_CENTER);

		setContent(list);
		setSizeFull();
		addStyleName("recipes-overview-content-page");
		addStyleName("recipes-content-list");
	}

	public String getCategory() {
		return this.category;
	}

	/**
	 * Clears this page and removes all the recipes.
	 */
	public void clearPage() {
		list.removeAllComponents();
	}

	/**
	 * Load this category's recipes from repository and show them on this page.
	 * Use {@link #clearPage()} before if you want to have a clean page.
	 */
	public void loadAndShowRecipes() {
		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		list.addComponent(progressBar);

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
	 * @param recipes - recipes to add.
	 */
	private void addRecipes(final List<Recipe> recipes) {
		recipes.forEach(recipe -> {
			list.addComponent(new OverviewListEntry(recipe));
		});
	}

//	private void addRecipes(final List<Recipe> list) {
//		list.forEach(recipe -> {
//
//			HorizontalLayout titleLayout = new HorizontalLayout();
//			titleLayout.addStyleName("title-bar");
//
//			Label recipeTitle = new Label(recipe.getName());
//			recipeTitle.addStyleName(ValoTheme.LABEL_H2);
//			recipeTitle.addStyleName(ValoTheme.LABEL_COLORED);
//
//			titleLayout.addComponent(recipeTitle);
//
//			String uriFragment = URIFragmentBuilder.of(recipe).getFragment();
//
//			Link showRecipeDetails = new Link();
//			showRecipeDetails.setIcon(VaadinIcons.ANGLE_RIGHT);
//			showRecipeDetails.setResource(new ExternalResource(DetailsView.VIEW_NAME + uriFragment));
//			showRecipeDetails.setTargetName("_blank");
//			showRecipeDetails.setDescription("Rezept öffnen");
//			showRecipeDetails.addStyleName(ValoTheme.LABEL_H2);
//
//			titleLayout.addComponent(showRecipeDetails);
//			titleLayout.setComponentAlignment(showRecipeDetails, Alignment.MIDDLE_LEFT);
//
//			list.addComponent(titleLayout);
//
//			String description = recipe.getDescription();
//			int length = description.length();
//			Label receiptDescription = new Label(
//					length > 300 ? description.substring(0, 300) + "..." : description);
//			receiptDescription.setWidth(50, Unit.PERCENTAGE);
//
//			list.addComponent(receiptDescription);
//
//		});
//	}
}
