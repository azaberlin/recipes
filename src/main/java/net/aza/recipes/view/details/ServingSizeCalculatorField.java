package net.aza.recipes.view.details;


import com.vaadin.data.HasValue;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import net.aza.recipes.model.Recipe;
import net.aza.recipes.model.ServingSizeType;

import java.util.*;

public final class ServingSizeCalculatorField extends CustomComponent {

	private final int initialServingSize;
	private int currentServingSize;
	private final ServingSizeType servingSizeType;
	private final Label servingSizeTypeField;

	private Set<ServingSizeCalculatorChangeListener> listeners;

	public ServingSizeCalculatorField(int initialServingSize, ServingSizeType servingSizeType, ServingSizeCalculatorChangeListener... listeners) {
		this.initialServingSize = initialServingSize;
		this.currentServingSize = initialServingSize;
		this.listeners = new LinkedHashSet<>();
		if (listeners != null) {
			this.listeners.addAll(Arrays.asList(listeners));
		}

		this.servingSizeType = servingSizeType;
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Label("Mengenangaben für "));

		TextField amountField = new TextField();
		amountField.addStyleName("serving-size");
		amountField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
		amountField.setValue(String.valueOf(this.currentServingSize));

		servingSizeTypeField = new Label();
		updateServingSizeTypeFieldBySize();

		amountField.addValueChangeListener(event -> {
			String value = event.getValue();
			if (value.isEmpty()) {
				value = null;
			}

			if (value != null && (value.startsWith("-") || !value.matches("[1-9]+[0-9]*"))) {
				amountField.setValue(String.valueOf(this.currentServingSize));
			} else {
				this.currentServingSize = value != null ? Integer.valueOf(value) : 0;
				updateServingSizeTypeFieldBySize();
			}

			this.listeners.forEach(listener -> listener.onServingSizeChange(this.currentServingSize));
		});

		layout.addComponent(amountField);
		layout.addComponent(servingSizeTypeField);
		setCompositionRoot(layout);

		setSizeUndefined();
	}

	private void updateServingSizeTypeFieldBySize() {
		servingSizeTypeField.setValue(currentServingSize == 1 ? servingSizeType.getDisplayNameSingle() : servingSizeType.getDisplayNameMultiple());
	}

	public void addListener(ServingSizeCalculatorChangeListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(ServingSizeCalculatorChangeListener listener) {
		this.listeners.remove(listener);
	}
}
