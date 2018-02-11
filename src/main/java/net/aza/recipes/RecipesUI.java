package net.aza.recipes;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Ingredient;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.model.RecipePart;
import net.aza.recipes.model.ServingSizeType;
import net.aza.recipes.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Push
@PushStateNavigation
@SpringUI
@Theme("recipes")
public class RecipesUI extends UI {

	@Autowired
	private RecipeRepository repository;

	@Override
	protected void init(VaadinRequest request) {
		createTestData();

		VerticalLayout uiLayout = initLayout();
		uiLayout.addComponents(createTitle());
		uiLayout.addComponentsAndExpand(createContent());

		setContent(uiLayout);
	}

	private void createTestData() {
		Random random = new Random();
		IntStream.rangeClosed('A', 'Z').filter(i -> random.nextBoolean()).mapToObj(value -> Character.valueOf((char) value).toString()).forEach(value -> {
			System.out.println("Create test data for letter " + value);
			for (int i = 0; i < random.nextInt(5) + 5; i++) {
				Recipe recipe = new Recipe(value + "-Rezept Nr. " + (i + 1), "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \n" +
						"\n" +
						"Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.", random.nextInt(10) + 1, ServingSizeType.PERSON);
				List<RecipePart> parts = new ArrayList<>();
				recipe.setParts(parts);

				int partsAmount = random.nextInt(5);
				for (int j = 0; j < partsAmount + 1; j++) {
					RecipePart recipePart = new RecipePart(recipe, partsAmount > 1 ? "Teil " + (j + 1) : "", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \n" +
							"\n" +
							"Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.   \n" +
							"\n" +
							"Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus.   \n" +
							"\n" +
							"Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea");
					parts.add(recipePart);

					List<Ingredient> ingredients = new ArrayList<>();
					recipePart.setIngredients(ingredients);

					for (int k = 0; k < random.nextInt(5) + 5; k++) {
						double amount = random.nextInt(5) + 1;
						Ingredient ingredient = new Ingredient(recipePart, amount, " Karotten");
						ingredients.add(ingredient);
					}
				}

				repository.save(recipe);
			}

		});
	}

	private VerticalLayout initLayout() {
		VerticalLayout layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		return layout;
	}

	private Label createTitle() {
		Label title = new Label("Mein Rezeptbuch");
		title.addStyleName(ValoTheme.LABEL_H1);
		title.addStyleName(ValoTheme.LABEL_COLORED);
		return title;
	}

	private Component createContent() {
		TabSheet sheet = new TabSheet();
		sheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);

		IntStream.rangeClosed('A', 'Z').mapToObj(value -> Character.valueOf((char) value).toString()).filter(s -> repository.countByNameLike(s + "%") > 0).forEach(value -> sheet.addComponent(new RecipesPage(value, repository.findByNameLike(value + "%"))));

		return sheet;
	}

	private class RecipesPage extends VerticalLayout {
		private final String recipesLetter;

		RecipesPage(String recipesLetter, List<Recipe> recipes) {
			this.recipesLetter = recipesLetter;
			setCaption(recipesLetter);

			recipes.stream().forEach(r -> {

				VerticalLayout recipeContainer = new VerticalLayout();
				addComponent(recipeContainer);

				Label recipeTitle = new Label(r.getName());
				recipeTitle.addStyleName(ValoTheme.LABEL_H2);
				recipeTitle.addStyleName(ValoTheme.LABEL_COLORED);

				HorizontalLayout recipeTitleLine = new HorizontalLayout();

				Link link = new Link("Details anzeigen", new ExternalResource("recipe/" + r.getId()));
				link.setTargetName("_blank");
				link.addStyleName(ValoTheme.LINK_SMALL);
				link.addStyleName(ValoTheme.LABEL_LIGHT);

				recipeTitleLine.addComponents(recipeTitle, link);
				recipeTitleLine.setComponentAlignment(link, Alignment.MIDDLE_LEFT);

				Label receiptDescription = new Label(r.getDescription());
				receiptDescription.setWidth(50, Unit.PERCENTAGE);

				recipeContainer.addComponent(recipeTitleLine);
				recipeContainer.addComponent(receiptDescription);
			});
		}
	}
}
