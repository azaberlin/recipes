package net.aza.recipes.model;

public enum ServingSizeType {
	PERSON("Person(en)"),
	PORTION("Portion(en)"),
	PIECE("Stück(e)");

	private String displayName;

	ServingSizeType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
