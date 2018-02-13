package net.aza.recipes.view.overview;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import net.aza.recipes.model.Recipe;
import net.aza.recipes.repositories.RecipeRepository;
import net.aza.recipes.view.details.RecipeDetails;

/**
 * An overview component for recipes. Has currently access to the repository to
 * access the data when needed. Might be outsources later.
 * 
 * @author Stefan Uebe
 *
 */
@SpringView(name = RecipesOverview.VIEW_NAME)
public class RecipesOverview extends CustomComponent implements View {

	public static final String VIEW_NAME = "";

	private static final long serialVersionUID = -3106739615646238527L;

	@Autowired
	private RecipeRepository repository;
	private RecipesOverviewContentPage selectedPage;
	private TabSheet sheet;

	@PostConstruct
	public void init() {
		this.sheet = new TabSheet();
		this.sheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		this.sheet.addStyleName("recipe-navigation");
		this.sheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
		this.sheet.setSizeFull();

		setCompositionRoot(this.sheet);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		Stream<String> filteredStream = IntStream.rangeClosed('A', 'Z')
				.mapToObj(value -> Character.valueOf((char) value).toString())
				.filter(s -> this.repository.countByNameLike(s + "%") > 0);

		filteredStream.forEach(value -> this.sheet.addTab(new RecipesOverviewContentPage(value)));

		this.sheet.addSelectedTabChangeListener(tabEvent -> {
			if (this.selectedPage != null) {
				this.selectedPage.clearPage();
			}

			RecipesOverviewContentPage page = (RecipesOverviewContentPage) tabEvent.getTabSheet().getSelectedTab();
			this.selectedPage = page;

			// store category for returning to this page
			getSession().setAttribute("last_category", page.getCategory());

			page.loadAndShowRecipes();
		});

		// try to restore last selected tab if returning to this page
		String lastCategory = (String) getSession().getAttribute("last_category");
		if (lastCategory != null) {
			Iterator<Component> iterator = this.sheet.iterator();
			while (iterator.hasNext()) {
				RecipesOverviewContentPage page = (RecipesOverviewContentPage) iterator.next();
				if (lastCategory.equals(page.getCategory())) {
					this.sheet.setSelectedTab(page);
				}
			}
		}

		getSelectedPage().ifPresent(RecipesOverviewContentPage::loadAndShowRecipes);
	}

	/**
	 * Returns the currently selected page if present.
	 * @return currently selected page
	 */
	private Optional<RecipesOverviewContentPage> getSelectedPage() {
		Tab tab = this.sheet.getTab(0);
		if (tab != null) {
			return Optional.of((RecipesOverviewContentPage) tab.getComponent());
		}

		return Optional.empty();
	}

	/**
	 * A lazy loading page that shows recipes of a certain category (like all recipes starting with the 
	 * letter "A").
	 * @author Stefan Uebe
	 *
	 */
	private class RecipesOverviewContentPage extends VerticalLayout {
		private static final long serialVersionUID = -4505004127233223184L;
		private String category;

		/**
		 * Creates a new page for a certain category (i. e. "A").
		 * @param category - category
		 */
		public RecipesOverviewContentPage(final String category) {
			this.category = category;
			setCaption(category);
			setDefaultComponentAlignment(Alignment.TOP_CENTER);
		}

		public String getCategory() {
			return this.category;
		}

		/**
		 * Clears this page and removes all the recipes.
		 */
		public void clearPage() {
			removeAllComponents();
		}

		/**
		 * Load this category's recipes from repository and show them on this page.
		 * Use {@link #clearPage()} before if you want to have a clean page.
		 */
		public void loadAndShowRecipes() {
			ProgressBar progressBar = new ProgressBar();
			progressBar.setIndeterminate(true);
			addComponent(progressBar);

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
		 * @return recipes of this category
		 */
		private List<Recipe> loadRecipesFromRepository() {
			return RecipesOverview.this.repository.findAllByNameLike(this.category + "%");
		}

		/**
		 * Adds the given recipe list to the page. Use {@link #clearPage()} if you want to clear the page
		 * before.
		 * @param list - recipes to add.
		 */
		private void addRecipes(final List<Recipe> list) {
			list.stream().forEach(recipe -> {

				HorizontalLayout titleLayout = new HorizontalLayout();

				Label recipeTitle = new Label(recipe.getName());
				recipeTitle.addStyleName(ValoTheme.LABEL_H2);
				recipeTitle.addStyleName(ValoTheme.LABEL_COLORED);

				titleLayout.addComponent(recipeTitle);

				Button showRecipeDetailsButton = new Button(VaadinIcons.EYE);
				String uriFragment = "/" + recipe.getId();

				showRecipeDetailsButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
				showRecipeDetailsButton.addStyleName(ValoTheme.BUTTON_TINY);
				showRecipeDetailsButton.setIcon(VaadinIcons.EYE);
				showRecipeDetailsButton.addClickListener(
						e -> getUI().getNavigator().navigateTo(RecipeDetails.VIEW_NAME + uriFragment));

				titleLayout.addComponent(showRecipeDetailsButton);

				addComponent(titleLayout);

				String description = recipe.getDescription();
				int length = description.length();
				Label receiptDescription = new Label(
						length > 300 ? description.substring(0, 300) + "..." : description);
				receiptDescription.setWidth(50, Unit.PERCENTAGE);

				addComponent(receiptDescription);

			});
		}
	}
}
