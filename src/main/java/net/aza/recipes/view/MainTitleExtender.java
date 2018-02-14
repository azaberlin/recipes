package net.aza.recipes.view;

import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.VerticalLayout;

/**
 * This interface allows a view to hook up and extend the title bar of this application.
 */
public interface MainTitleExtender {

	/**
	 * Extends the left part of the title line. Default action is noop.
	 * @param leftPartContainer - container for the left part
	 */
	default void extendLeftPart(ComponentContainer leftPartContainer) {
	}

	/**
	 * Extends the right part of the title line. Default action is noop.
	 * @param rightPartContainer - container for the right part
	 */
	default void extendRightPart(ComponentContainer rightPartContainer) {
	}
}
