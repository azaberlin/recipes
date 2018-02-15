package net.aza.recipes.view.details;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Ingredient;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.model.RecipePart;
import net.aza.recipes.model.ServingSizeType;
import net.aza.recipes.repositories.RecipeRepository;
import net.aza.recipes.view.MainTitleExtender;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@SpringView(name = RecipeDetails.VIEW_NAME)
public class RecipeDetails extends Panel implements View, MainTitleExtender {

	public static final String VIEW_NAME = "show";
	private static final long serialVersionUID = 2078142131705053643L;
	private int servingSize;

	@Autowired
	private RecipeRepository repository;
	private VerticalLayout layout;
	private Recipe recipe;
	private Button showIngredientsButton;
	private List<IngredientsPart> ingredientsParts = new ArrayList<>();

	@PostConstruct
	private void init() {
		layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);

		setContent(layout);
		addStyleName(ValoTheme.PANEL_BORDERLESS);
		addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);
		addStyleName("recipes-content-container");
		setSizeFull();
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		String parameters = event.getParameters();
		if (parameters.matches("[1-9][0-9]*")) {
			Long id = Long.valueOf(parameters);

			recipe = this.repository.findOne(id);
			if (recipe != null) {
				this.servingSize = recipe.getServingSize();

				layout.addComponent(createRecipeTitle(recipe));
				layout.addComponent(createAmountCalculationPart(recipe));

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
				showIngredientsButton.setVisible(true);
			}
		}
	}

	private Component createAmountCalculationPart(final Recipe recipe) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Label("Mengenangaben für "));

		TextField amountField = new TextField();
		amountField.addStyleName("serving-size");
		amountField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
		amountField.setValue(String.valueOf(this.servingSize));

		Label servingSizeTypeField = new Label();
		updateServingSizeTypeFieldBySize(recipe, servingSizeTypeField);

		amountField.addValueChangeListener(event -> {
			String value = event.getValue();
			if (value.isEmpty()) {
				value = null;
			}

			if (value != null && (value.startsWith("-") || !value.matches("[1-9]+[0-9]*"))) {
				amountField.setValue(String.valueOf(this.servingSize));
			} else {
				this.servingSize = value != null ? Integer.valueOf(value) : 0;
				updateServingSizeTypeFieldBySize(recipe, servingSizeTypeField);
				updateIngredientAmounts(servingSize);
			}
		});

		layout.addComponent(amountField);
		layout.addComponent(servingSizeTypeField);

		return layout;
	}

	private void updateIngredientAmounts(int servingSize) {
		ingredientsParts.forEach(components -> components.updateIngredients(servingSize));
	}

	private void updateServingSizeTypeFieldBySize(Recipe recipe, Label label) {
		ServingSizeType type = recipe.getServingSizeType();
		label.setValue(servingSize == 1 ? type.getDisplayNameSingle() : type.getDisplayNameMultiple());
	}

	private Object calculateIngredients(final Integer value) {
		return null;
	}

	private Label createRecipeTitle(final Recipe recipe) {
		Label title = new Label(recipe.getName());
		title.addStyleName(ValoTheme.LABEL_H2);
		return title;
	}

	private boolean nameIsNotEmpty(final RecipePart p) {
		return p.getName() != null && !p.getName().isEmpty();
	}

	@Override
	public void extendLeftPart(ComponentContainer container) {
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(false);
		layout.setSizeFull();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);

		showIngredientsButton = new Button(VaadinIcons.FILE_TEXT);
		showIngredientsButton.setDescription("Alle Zutaten zusammengefasst anzeigen");
		showIngredientsButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		showIngredientsButton.setVisible(false);

		showIngredientsButton.addClickListener(event -> {
			Window window = new IngredientsShoppingList(recipe);
			getUI().addWindow(window);
		});

		layout.addComponent(showIngredientsButton);

		container.addComponent(layout);
	}

	@Override
	public void extendRightPart(ComponentContainer container) {
		VerticalLayout layout = new VerticalLayout();

		layout.setMargin(false);
		layout.setSizeFull();
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);

		Button closeButton = new Button(VaadinIcons.CLOSE);
		closeButton.setDescription("Rezept schließen");
		closeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);

		closeButton.addClickListener(event -> {
			// since the overview opens this view in a new window we will provide a "soft close" additional
			// to the native close of the browser.
			Page.getCurrent().getJavaScript().execute("close()");
			getUI().close();
		});

		layout.addComponent(closeButton);
		container.addComponent(layout);
	}
}
