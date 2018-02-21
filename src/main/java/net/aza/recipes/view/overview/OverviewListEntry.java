package net.aza.recipes.view.overview;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.model.RecipePart;
import net.aza.recipes.view.details.IngredientsPart;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executors;

class OverviewListEntry extends VerticalLayout {

	private static final String STYLE_EXPANDED = "expanded";
	private static final String STYLE_COLLAPSED = "collapsed";
	private final VerticalLayout detailsContainer;
	private final Recipe recipe;
	private final Label expandCollapseIcon;
	private boolean expanded;

	OverviewListEntry(Recipe recipe) {
		this.recipe = recipe;
		setDefaultComponentAlignment(Alignment.TOP_CENTER);
		setMargin(false);
		addStyleName("overview-list-entry");
		addStyleName(STYLE_COLLAPSED);

		HorizontalLayout entryTitleContainer = new HorizontalLayout();
		entryTitleContainer.addStyleName("overview-list-entry-title");
		entryTitleContainer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		expandCollapseIcon = new Label(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml(), ContentMode.HTML);
		expandCollapseIcon.addStyleName("expand-icon");
		expandCollapseIcon.addStyleName(ValoTheme.LABEL_COLORED);
		expandCollapseIcon.addStyleName(ValoTheme.LABEL_TINY);
		entryTitleContainer.addComponent(expandCollapseIcon);

		Label nameLabel = new Label(recipe.getName());
		nameLabel.addStyleName(ValoTheme.LABEL_COLORED);
		nameLabel.addStyleName("name");
		entryTitleContainer.addComponent(nameLabel);

		Label additionalInfoLabel = new Label("Dauer: ca. 20 Minuten, Schwierigkeit: niedrig");
		additionalInfoLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		additionalInfoLabel.addStyleName("info");
		additionalInfoLabel.addStyleName(ValoTheme.LABEL_TINY);
		entryTitleContainer.addComponent(additionalInfoLabel);

		addComponent(entryTitleContainer);

		entryTitleContainer.addLayoutClickListener(event -> {
			if (event.getButton() == MouseEventDetails.MouseButton.LEFT) {
				if (expanded) {
					expanded = false;
					collapse();
				} else {
					expanded = true;
					expand();
				}
			}
		});

		detailsContainer = new VerticalLayout();
		detailsContainer.addStyleName("overview-list-entry-details");
		detailsContainer.setVisible(false);
		detailsContainer.setDefaultComponentAlignment(Alignment.TOP_CENTER);
		detailsContainer.setMargin(false);

		addComponent(detailsContainer);

	}

	/**
	 * Shows additional content of this entry.
	 */
	private void expand() {
		addStyleName(STYLE_EXPANDED);
		removeStyleName(STYLE_COLLAPSED);

		expandCollapseIcon.setValue(VaadinIcons.ANGLE_DOUBLE_UP.getHtml());

		ProgressBar progressBar = new ProgressBar();
		progressBar.setIndeterminate(true);
		detailsContainer.addComponent(progressBar);

		Executors.newSingleThreadExecutor().execute(() -> {
			// showing the progress bar initialiy leads to clipping errors and jumping texts. so
			// we'll wait a few seconds, to show it.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				if (progressBar.isAttached()) {
					getUI().access(() -> progressBar.addStyleName("delayed"));
				}
			}
		});


		Executors.newSingleThreadExecutor().execute(() -> {
			// TODO remove later
			// just to show the progress bar
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				getUI().access(() -> {
					// TODO add tab sheet with page switcher
					Set<RecipePart> recipeParts = new LinkedHashSet<>(recipe.getParts());

					if (recipeParts.size() > 1) { // more than one part - display in tabs
						TabSheet sheet = new TabSheet();
						sheet.addStyleNames(ValoTheme.TABSHEET_CENTERED_TABS);

						VerticalLayout summaryContainer = new VerticalLayout();
						summaryContainer.setCaption("Zusammenfassung");
						summaryContainer.addStyleName("details-container-content");

						Label descriptionLabel = new Label(recipe.getDescription());
						descriptionLabel.addStyleName("wrap");
						summaryContainer.addComponent(descriptionLabel);

						sheet.addTab(summaryContainer);

						recipeParts.forEach(recipePart -> {
							VerticalLayout partContainer = new VerticalLayout();
							partContainer.addStyleName("details-container-content");

							partContainer.addComponent(new IngredientsPart(recipe.getServingSize(), new LinkedHashSet<>(recipePart.getIngredients())));

							Label instructionsLabel = new Label(recipePart.getInstructions());
							instructionsLabel.addStyleName("wrap");
							partContainer.addComponent(instructionsLabel);

							sheet.addTab(partContainer, recipePart.getName());
						});
						detailsContainer.addComponent(sheet);
					} else { // only one part - display inline
						VerticalLayout inlineContent = new VerticalLayout();
						inlineContent.addStyleName("details-container-content");

						Label descriptionLabel = new Label(recipe.getDescription());
						descriptionLabel.addStyleName("wrap");
						inlineContent.addComponent(descriptionLabel);

						RecipePart recipePart = recipeParts.iterator().next();
						inlineContent.addComponent(new IngredientsPart(recipe.getServingSize(), new LinkedHashSet<>(recipePart.getIngredients())));

						Label instructionsLabel = new Label(recipePart.getInstructions());
						instructionsLabel.addStyleName("wrap");
						inlineContent.addComponent(instructionsLabel);

						detailsContainer.addComponent(inlineContent);

					}

					detailsContainer.removeComponent(progressBar);
				});
			}
		});

		detailsContainer.setVisible(true);
	}

	/**
	 * Hides additional content of this entry.
	 */
	private void collapse() {
		removeStyleName(STYLE_EXPANDED);
		addStyleName(STYLE_COLLAPSED);

		expandCollapseIcon.setValue(VaadinIcons.ANGLE_DOUBLE_DOWN.getHtml());

		detailsContainer.setVisible(false);
		detailsContainer.removeAllComponents();
	}
}
