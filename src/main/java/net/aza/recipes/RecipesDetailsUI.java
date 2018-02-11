package net.aza.recipes;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Push
@PushStateNavigation
@SpringUI(path = "/recipe")
@Theme("recipes")
public class RecipesDetailsUI extends UI {

	@Autowired
	private RecipeRepository repository;

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout uiLayout = initLayout();
		uiLayout.addComponents(createTitle());

		String pathInfo = request.getPathInfo();
		Recipe recipe = null;
		if(pathInfo.matches("/recipe/[0-9]+")) {
			String substring = pathInfo.substring("/recipes/".length() - 1);
			recipe = repository.findOne(Long.valueOf(substring));
			if (recipe != null) {
				uiLayout.addComponent(createContent(recipe));
			} else {
				uiLayout.addComponent(createRecipeNotFoundContent());
			}
		} else {
			uiLayout.addComponent(createInvalidUrlContent());
		}


		setContent(uiLayout);
	}

	private Component createInvalidUrlContent() {
		return new Label("Ungültige URL!");
	}

	private Component createRecipeNotFoundContent() {
		return new Label("Rezept leider nicht gefunden!");
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

	private Component createContent(Recipe recipe) {
		VerticalLayout detailContainer = new VerticalLayout();

		detailContainer.addComponent(new Label("Mengenangaben für <b>" + recipe.getServingSize() + " " + recipe.getServingSizeType().getDisplayName(), ContentMode.HTML));

		recipe.getParts().forEach(p -> {
			if(p.getName() != null && !p.getName().isEmpty()) {
				Label partTitle = new Label(p.getName());
				partTitle.addStyleName(ValoTheme.LABEL_H3);
				detailContainer.addComponent(partTitle);
			}

			p.getIngredients().forEach(i -> {
				detailContainer.addComponent(new Label("<b>" + i.getAmount() + "</b> " + i.getUnit(), ContentMode.HTML));
			});

			Label instructions = new Label(p.getInstructions());
			instructions.setWidth(50, Unit.PERCENTAGE);
			detailContainer.addComponent(instructions);
		});

		return detailContainer;
	}

}
