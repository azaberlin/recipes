package net.aza.recipes.view;

import net.aza.recipes.model.Recipe;

/**
 * Simple utility class to create a uri fragment based on the given recipe that can be added to an existing uri and
 * identify the recipe.
 */
public final class URIFragmentBuilder {
	private final Recipe recipe;

	private URIFragmentBuilder(Recipe recipe) {
		this.recipe = recipe;
	}

	public static final URIFragmentBuilder of(Recipe recipe) {
		return new URIFragmentBuilder(recipe);
	}

	public String getFragment() {
		return addToURI("");
	}

	public String addToURI(final String uri) {
		String newURI = uri;
		if(!newURI.endsWith("/")) {
			newURI = newURI + "/";
		}

		return newURI + recipe.getId();
	}
}
