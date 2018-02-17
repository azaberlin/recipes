package net.aza.recipes.view.details;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.model.RecipePart;
import net.aza.recipes.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@ViewScope
@SpringView(name = RecipeDetails.VIEW_NAME)
public class RecipeDetails extends CustomComponent implements View {

	public static final String VIEW_NAME = "show";
	private static final long serialVersionUID = 2078142131705053643L;
	private int servingSize;

	@Autowired
	private RecipeRepository repository;
	private VerticalLayout layout;
	private List<IngredientsPart> ingredientsParts = new ArrayList<>();

	@PostConstruct
	private void init() {
		Panel panel = new Panel();

		layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);

		panel.setContent(layout);
		panel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		panel.addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);
		panel.addStyleName("recipes-content-container");
		panel.setSizeFull();
		setCompositionRoot(panel);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		EventIdParser idParser = EventIdParser.of(event);

		if (idParser.isValidParameter()) {
			Recipe recipe = this.repository.findOne(idParser.getId());
			if (recipe != null) {
				this.servingSize = recipe.getServingSize();

				layout.addComponent(createRecipeTitle(recipe));
				ServingSizeCalculatorField servingSizeCalculatorField = new ServingSizeCalculatorField(recipe.getServingSize(), recipe.getServingSizeType(), this::updateIngredientAmounts);

				layout.addComponent(servingSizeCalculatorField);

				// show ingredients
				// TODO we encapsulate the list into a linkedHashSet because of Hibernates. Can be extracted to a
				// service method later.
				LinkedHashSet<RecipePart> parts = new LinkedHashSet<>(recipe.getParts());

				parts.forEach(p -> {
					if (nameIsNotEmpty(p)) {
						Label partTitle = new Label(p.getName());
						partTitle.addStyleName(ValoTheme.LABEL_H4);
						layout.addComponent(partTitle);
					}

					Label instructions = new Label(p.getInstructions(), ContentMode.HTML);
					instructions.addStyleName("wrap");
					instructions.addStyleName("instructions");
					instructions.setSizeFull();

					IngredientsPart ingredientsPart = new IngredientsPart(this.servingSize, new LinkedHashSet<>(p.getIngredients()));
					ingredientsPart.setSizeUndefined();
					ingredientsPart.setMargin(false);

					ingredientsParts.add(ingredientsPart);

					HorizontalLayout instructionListItem = new HorizontalLayout();
					instructionListItem.setSizeFull();
					instructionListItem.setWidth(80, Unit.PERCENTAGE);
					instructionListItem.setDefaultComponentAlignment(Alignment.TOP_CENTER);

					instructionListItem.addComponent(ingredientsPart);
					instructionListItem.addComponentsAndExpand(instructions);

					layout.addComponent(instructionListItem);
				});

			}
		}
	}

	private void updateIngredientAmounts(int servingSize) {
		ingredientsParts.forEach(components -> components.updateIngredients(servingSize));
	}

	private Label createRecipeTitle(final Recipe recipe) {
		Label title = new Label(recipe.getName());
		title.addStyleName(ValoTheme.LABEL_H2);
		return title;
	}

	private boolean nameIsNotEmpty(final RecipePart p) {
		return p.getName() != null && !p.getName().isEmpty();
	}
}
