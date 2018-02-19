package net.aza.recipes.view.details;

import com.vaadin.navigator.ViewChangeListener;

public final class EventIdParser {

	private final boolean validParameter;
	private Long id;

	public static EventIdParser of(ViewChangeListener.ViewChangeEvent event) {
		return new EventIdParser(event);
	}

	private EventIdParser(ViewChangeListener.ViewChangeEvent event) {
		String parameters = event.getParameters();
		validParameter = parameters.matches("[1-9][0-9]*");
		if (validParameter) {
			id = Long.valueOf(parameters);
		}
	}

	public boolean isValidParameter() {
		return validParameter;
	}

	public Long getId() {
		return id;
	}
}
