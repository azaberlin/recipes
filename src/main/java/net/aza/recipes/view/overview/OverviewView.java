package net.aza.recipes.view.overview;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.*;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.repositories.RecipeRepository;
import net.aza.recipes.view.edit.Editor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An overview component for recipes. Has currently access to the repository to
 * access the data when needed. Might be outsources later.
 *
 * @author Stefan Uebe
 */
@ViewScope
@SpringView(name = OverviewView.VIEW_NAME)
public class OverviewView extends CustomComponent implements View {

	static final String VIEW_NAME = "";
	public static final String LAST_SELECTED_CATEGORY = "last_category";

	@Autowired
	private RecipeRepository repository;
	private OverviewList selectedPage;
	private TabSheet sheet;

	@PostConstruct
	private void init() {
		addStyleName("recipes-overview");

		VerticalLayout container = new VerticalLayout();
		container.setMargin(false);
		container.setDefaultComponentAlignment(Alignment.TOP_CENTER);

		Button newButton = new Button("Neues Rezept hinzufügen", VaadinIcons.PLUS);
		newButton.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		newButton.addStyleName(ValoTheme.BUTTON_TINY);
		newButton.addClickListener(event -> getUI().addWindow(new Editor(repository)));
		container.addComponent(newButton);

		this.sheet = new TabSheet();
		this.sheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		this.sheet.addStyleName("recipe-navigation");
		this.sheet.setSizeFull();
		container.addComponentsAndExpand(this.sheet);

		container.setSizeFull();

		setCompositionRoot(container);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		Stream<String> filteredStream = IntStream.rangeClosed('A', 'Z')
				.mapToObj(value -> Character.valueOf((char) value).toString())
				.filter(s -> this.repository.countByNameLike(s + "%") > 0);

		filteredStream.forEach(value -> this.sheet.addTab(new OverviewList(value, this.repository), value));

		this.sheet.addSelectedTabChangeListener(tabEvent -> {
			OverviewList page = updateInternalsOnTabSwitchEvent(tabEvent);

			storeSelectedTabInSession(page);

			page.loadAndShowRecipes();
		});

		// try to restore last selected tab if returning to this page
		// (not needed in current implementation)
		// restoreLastSelectedTabFromSessionIfPresent();

		reloadCurrentPage();
	}

	private OverviewList updateInternalsOnTabSwitchEvent(TabSheet.SelectedTabChangeEvent tabEvent) {
		if (this.selectedPage != null) {
			this.selectedPage.clearPage();
		}

		OverviewList page = (OverviewList) tabEvent.getTabSheet().getSelectedTab();
		this.selectedPage = page;
		return page;
	}

	private void storeSelectedTabInSession(OverviewList page) {
		getSession().setAttribute(LAST_SELECTED_CATEGORY, page.getCategory());
	}

	/**
	 * If there is a currently selected page, then do a reload.
	 */
	private void reloadCurrentPage() {
		getSelectedPage().ifPresent(OverviewList::loadAndShowRecipes);
	}

	/**
	 * Tries to restore the last selected tab. This function is usefull when all pages are opened in
	 * one page (currently not implemented).
	 */
	private void restoreLastSelectedTabFromSessionIfPresent() {
		String lastCategory = (String) getSession().getAttribute(LAST_SELECTED_CATEGORY);
		if (lastCategory != null) {
			Iterator<Component> iterator = this.sheet.iterator();
			while (iterator.hasNext()) {
				OverviewList page = (OverviewList) iterator.next();
				if (lastCategory.equals(page.getCategory())) {
					this.sheet.setSelectedTab(page);
				}
			}
		}
	}

	/**
	 * Returns the currently selected page if present.
	 *
	 * @return currently selected page
	 */
	private Optional<OverviewList> getSelectedPage() {
		return Optional.ofNullable((OverviewList) this.sheet.getSelectedTab());
	}
}
