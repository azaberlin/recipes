package net.aza.recipes;

import java.util.List;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import net.aza.recipes.model.Recipe;

class RecipesPage extends VerticalLayout {

	private static final long serialVersionUID = 7140663956338613438L;

	RecipesPage(final String recipesLetter, final List<Recipe> recipes) {
		setCaption(recipesLetter);

		recipes.stream().forEach(r -> {

			VerticalLayout recipeContainer = new VerticalLayout();
			addComponent(recipeContainer);

			Label recipeTitle = new Label(r.getName());
			recipeTitle.addStyleName(ValoTheme.LABEL_H2);
			recipeTitle.addStyleName(ValoTheme.LABEL_COLORED);

			HorizontalLayout recipeTitleLine = new HorizontalLayout();

			Link link = new Link("Details anzeigen", new ExternalResource(RecipesUI.PATH_BASE_SHOW_RECIPE + "/" + r.getId()));
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