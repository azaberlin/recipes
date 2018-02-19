package net.aza.recipes.view.overview;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.repositories.RecipeRepository;
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
@SpringView(name = RecipesOverview.VIEW_NAME)
public class RecipesOverview extends CustomComponent implements View {

	static final String VIEW_NAME = "";

	private static final long serialVersionUID = -3106739615646238527L;

	@Autowired
	private RecipeRepository repository;
	private RecipesOverviewContentPage selectedPage;
	private TabSheet sheet;

	@PostConstruct
	private void init() {
		this.sheet = new TabSheet();
		this.sheet.addStyleName(ValoTheme.TABSHEET_CENTERED_TABS);
		this.sheet.addStyleName("recipe-navigation");
		this.sheet.setSizeFull();

		setCompositionRoot(this.sheet);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		Stream<String> filteredStream = IntStream.rangeClosed('A', 'Z')
				.mapToObj(value -> Character.valueOf((char) value).toString())
				.filter(s -> this.repository.countByNameLike(s + "%") > 0);

		filteredStream.forEach(value -> this.sheet.addTab(new RecipesOverviewContentPage(value, this.repository), value));

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
		// (not needed in current implementation)
		// restoreLastSelectedTabIfPresent();

		getSelectedPage().ifPresent(RecipesOverviewContentPage::loadAndShowRecipes);
	}

	/**
	 * Tries to restore the last selected tab. This function is usefull when all pages are opened in
	 * one page (currently not implemented).
	 */
	private void restoreLastSelectedTabIfPresent() {
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
	}

	/**
	 * Returns the currently selected page if present.
	 *
	 * @return currently selected page
	 */
	private Optional<RecipesOverviewContentPage> getSelectedPage() {
		Tab tab = this.sheet.getTab(0);
		if (tab != null) {
			return Optional.of((RecipesOverviewContentPage) tab.getComponent());
		}

		return Optional.empty();
	}
}
