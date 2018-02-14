package net.aza.recipes.view.details;

import java.util.LinkedHashSet;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.vaadin.server.Page;
import com.vaadin.ui.*;
import net.aza.recipes.view.MainTitleExtender;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.themes.ValoTheme;

import net.aza.recipes.model.Recipe;
import net.aza.recipes.model.RecipePart;
import net.aza.recipes.repositories.RecipeRepository;
import net.aza.recipes.view.overview.RecipesOverview;

@SpringView(name = RecipeDetails.VIEW_NAME)
public class RecipeDetails extends Panel implements View, MainTitleExtender {

	public static final String VIEW_NAME = "show";
	private static final long serialVersionUID = 2078142131705053643L;
	private Integer servingSize;

	@Autowired
	private RecipeRepository repository;
	private VerticalLayout layout;

	@PostConstruct
	private void init() {
		layout = new VerticalLayout();
		layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);

		setContent(layout);
		addStyleName(ValoTheme.PANEL_BORDERLESS);
		addStyleName(ValoTheme.PANEL_SCROLL_INDICATOR);
		setSizeFull();
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		String parameters = event.getParameters();
		if (parameters.matches("[1-9][0-9]*")) {
			Long id = Long.valueOf(parameters);

			Recipe recipe = this.repository.findOne(id);
			if (recipe != null) {
				this.servingSize = recipe.getServingSize();

				layout.addComponent(createRecipeTitle(recipe));
				layout.addComponent(createAmountCalculationPart(recipe));

				// show ingredients
				LinkedHashSet<RecipePart> parts = new LinkedHashSet<>(recipe.getParts());

				createIngredientsPart(parts);
				createInstructionsPart(parts);
			}
		}
	}

	private Component createAmountCalculationPart(final Recipe recipe) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Label("Mengenangaben für "));
		TextField amountField = new TextField();
		amountField.addStyleName("serving-size");
		amountField.addStyleName(ValoTheme.TEXTFIELD_SMALL);

		amountField.setValue(this.servingSize.toString());
		amountField.addValueChangeListener(event -> {
			String value = event.getValue();
			if (value.isEmpty()) {
				value = null;
			}

			if (value != null && (value.startsWith("-") || !value.matches("[1-9]+[0-9]*"))) {
				amountField.setValue(this.servingSize.toString());
			} else {
				this.servingSize = value != null ? Integer.valueOf(value) : 0;
			}
		});

		layout.addComponent(amountField);
		layout.addComponent(new Label(recipe.getServingSizeType().getDisplayName()));

		return layout;
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

	private void createIngredientsPart(final LinkedHashSet<RecipePart> parts) {
		Label title = new Label("Was benötigst Du?");
		title.addStyleName(ValoTheme.LABEL_H3);
		layout.addComponent(title);

		parts.forEach(p -> {
			if (nameIsNotEmpty(p)) {
				Label partTitle = new Label("... für " + p.getName());
				partTitle.addStyleName(ValoTheme.LABEL_H4);
				layout.addComponent(partTitle);
			}

			// TODO cache created ingredient parts and update their content in another
			// method.
			layout.addComponent(new IngredientsPart(this.servingSize, p.getIngredients()));
		});
	}

	private void createInstructionsPart(final LinkedHashSet<RecipePart> parts) {
		Label title = new Label("Was ist zu tun?");
		title.addStyleName(ValoTheme.LABEL_H3);
		layout.addComponent(title);

		parts.forEach(p -> {
			if (nameIsNotEmpty(p)) {
				Label partTitle = new Label(p.getName());
				partTitle.addStyleName(ValoTheme.LABEL_H4);
				layout.addComponent(partTitle);
			}

			Label instructions = new Label(p.getInstructions());
			instructions.setWidth(50, Unit.PERCENTAGE);
			layout.addComponent(instructions);
		});
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
