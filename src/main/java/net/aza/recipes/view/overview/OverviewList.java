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
		list.addStyleName("recipes-content-list");

		setSizeFull();
		addStyleName("recipes-overview-content-page");
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
	 * (Re-) Load this category's recipes from repository and show them on this page.
	 */
	public void loadAndShowRecipes() {
		clearPage();

		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		setContent(progressBar);

		Executors.newSingleThreadExecutor().submit(() -> {
			List<Recipe> recipes = loadRecipesFromRepository();

			getUI().access(() -> {
				recipes.forEach(recipe -> {
					list.addComponent(new OverviewListEntry(recipe));
				});
				setContent(list);
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

}
