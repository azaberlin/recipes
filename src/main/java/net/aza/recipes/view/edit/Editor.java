package net.aza.recipes.view.edit;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.repositories.RecipeRepository;
import org.springframework.context.annotation.Scope;

import java.util.LinkedHashSet;
import java.util.Objects;

public class Editor extends Window {

	private Recipe recipe;

	// TODO instead of getting repository, better take a store listener that passes the update recipe element
	public Editor(final RecipeRepository repository) {
		this(new Recipe(), repository);
	}

	public Editor(final Recipe recipe, final RecipeRepository repository) {
		this.recipe = recipe;
		if (!isNewRecipe()) {
			this.recipe = repository.findOne(recipe.getId());

			if (this.recipe == null) {
				throw new IllegalArgumentException("Could not find matching recipe instance in data base: " + recipe.getId());
			}
		}

		initEditor();
	}

	private void initEditor() {
		setWidth(50, Unit.PERCENTAGE);
		setHeight(80, Unit.PERCENTAGE);
		center();
		setModal(true);
		setResizable(true);
		addStyleNames(ValoTheme.LAYOUT_CARD);

		VerticalLayout outerLayout = new VerticalLayout();
		outerLayout.addComponent(createButtons());
		outerLayout.setMargin(false);
		outerLayout.setSizeFull();

		VerticalLayout innerLayout = new VerticalLayout();
		innerLayout.setSizeFull();
		outerLayout.addComponentsAndExpand(innerLayout);

		TextField nameField = new TextField("Name");
		innerLayout.addComponent(nameField);


		RichTextArea descriptionText = new RichTextArea("Beschreibung");
		innerLayout.addComponent(descriptionText);

		Binder<Recipe> recipeBinder = new Binder<>();
		recipeBinder.forField(nameField).asRequired().bind(Recipe::getName, Recipe::setName);
		recipeBinder.forField(descriptionText).bind(Recipe::getDescription, Recipe::setDescription);
		recipeBinder.setBean(recipe);

		innerLayout.addComponent(createSeparator());

		innerLayout.addComponent(new Label("..."));

//		new LinkedHashSet<>(recipe.getParts()).forEach(recipePart -> {
//			HorizontalLayout editorRow = new HorizontalLayout();
//
//
//		});

		setContent(outerLayout);
	}

	private boolean isNewRecipe() {
		return recipe.getId() == null;
	}

	private Component createButtons() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponentsAndExpand(new Label(isNewRecipe() ? "Neues Rezept hinzufügen" : recipe.getName() + " bearbeiten"));

		Button storeButton = new Button(isNewRecipe() ? "Anlegen" : "Speichern");
		storeButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		storeButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		storeButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		storeButton.addClickListener(event -> {
			// TODO call outside set store listeners
			close();
		});

		layout.addComponent(storeButton);

		Button cancelButton = new Button("Abbrechen");
		cancelButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		cancelButton.addClickListener(event -> close());
		layout.addComponent(cancelButton);

		layout.addStyleName("v-panel-caption");

		return layout;
	}

	private Label createSeparator() {
		Label label = new Label("<hr/>", ContentMode.HTML);
		label.setWidth(100, Unit.PERCENTAGE);
		label.addStyleName("separator");
		return label;
	}
}
