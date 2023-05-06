package gui;

import javax.swing.JFrame;

public class Model {
	// View des Programms
	private View view;
	
	// Daten dieses Modells 
	private String displayOutput;
	private int taskStep = 0;
	
	// Konstruktor 
	public Model() {
		displayOutput = "Willkommen beim Assistent f�r die Montage von Bauteilen!\n"
				+ "Durch Auswahl eines der Aufgaben und Klicken auf 'Weiter' "
				+ "k�nnen die verschiedenen Funktionen genutzt werden.";
	}
	
	/**
	 * F�gt eine neue View hinzu
	 * @param view Die View
	 */
	public void setView(View view){
		this.view = view;
		setStandardBounds();
		view.refresh();
	}
	
	/**
	 * @return the displayOutput
	 */
	public String getOutput(){
		return displayOutput;
	}
	
	/**
	 * Setzt die Gr��e auf Standard zur�ck.
	 */
	public void setStandardBounds() {
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * Wechselt je nach Auswahl zum richtigen Panel.
	 */
	public void switchPanel() {
		if (taskStep == 0) {
			view.setCurrentTasks(view.getTasks().get(view.getComboBox().getSelectedIndex()));
			taskStep = 1;
			view.getTfTaskTitle().setText(view.getCurrentTasks()[taskStep-1].name);
			displayOutput = view.getCurrentTasks()[taskStep-1].description;
			view.getBtnNextStep().setText("Weiter");
			view.switchToPanel("Tasks");
		}/* else if (view.getCurrentTasks() != null && taskIndex > 0) {
			
		}*/ else {
			displayOutput = "Ein Fehler ist aufgetreten.";
			view.switchToPanel("Menu");
		}
	}

	/**
	 * Wechselt je nach Auswahl zum richtigen Panel.
	 * @param next True setzen wenn zum n�chsten Schritt der Task, false wenn zum vorherigen Schritt.
	 */
	public void switchPanel(boolean next) {
		if (view.getCurrentTasks() != null && taskStep > 0 && next) {
			taskStep++;
			if (taskStep > view.getCurrentTasks().length) {
				toMenuPanel();
			} else {
				view.getTfTaskTitle().setText(view.getCurrentTasks()[taskStep-1].name);
				displayOutput = view.getCurrentTasks()[taskStep-1].description;
				if (view.getCurrentTasks().length == taskStep) {
					view.getBtnNextStep().setText("Fertig");
				}
				view.refresh();
			}
		} else if (view.getCurrentTasks() != null && taskStep > 0 && !next) {
			taskStep--;
			if (taskStep == 0) {
				toMenuPanel();
			} else {
				view.getTfTaskTitle().setText(view.getCurrentTasks()[taskStep-1].name);
				displayOutput = view.getCurrentTasks()[taskStep-1].description;
				view.getBtnNextStep().setText("Weiter");
				view.refresh();
			}
		} else {
			displayOutput = "Ein Fehler ist aufgetreten.";
			view.switchToPanel("Menu");
		}
	}
	
	/**
	 * Wechselt zum Hauptmen�-Panel.
	 */
	public void toMenuPanel() {
		taskStep = 0;
		displayOutput = "Durch Auswahl eines der Aufgaben und Klicken auf 'Weiter' "
				+ "k�nnen die verschiedenen Funktionen genutzt werden.";
		view.switchToPanel("Menu");
	}
	
}
