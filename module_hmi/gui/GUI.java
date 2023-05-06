package gui;

/**
 * Die Hauptklasse, die das Model erstellt und die View verwaltet.
 * @author paulj
 *
 */
public class GUI {
	
	public static void main(String[] args) {
		// Das Model wird angelegt
		Model model = new Model();
		// Die Views, welche die Daten anzeigen werden angelegt
		View view = new View(model);
	
		// Die Views tragen sich bei dem Model ein
		model.setView(view);
	}

}
