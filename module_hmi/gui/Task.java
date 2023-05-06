package gui;

public class Task {

	public String name;
	public String description;
	
	/**
	 * Konstruktor
	 * @param name Der Name der Aufgabe
	 * @param desc Die Beschreibung der Aufgabe
	 */
	public Task(String name, String desc) {
		setName(name);
		setDescription(desc);
	}
	
	// Getters/Setters
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
