package net.aza.recipes.model;

public enum ServingSizeType {
	PERSON("Person", "Personen"),
	PORTION("Portion", "Portionen"),
	PIECE("Stück", "Stück");

	private String displayNameSingle;
	private String displayNameMultiple;

	ServingSizeType(String displayName, String displayNameMultiple) {
		this.displayNameSingle = displayName;
		this.displayNameMultiple = displayNameMultiple;
	}

	public String getDisplayNameMultiple() {
		return displayNameMultiple;
	}

	public String getDisplayNameSingle() {
		return displayNameSingle;
	}

	public String getDisplayNameByServingSize(int servingSize) {
		return servingSize == 1 ? displayNameSingle : displayNameMultiple;
	}
}
