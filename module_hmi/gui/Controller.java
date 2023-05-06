package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * Die Superklasse f�r alle Controller mit ActionListener.
 * @author paulj
 *
 */
public abstract class Controller implements ActionListener {
	// Referenz auf das Model
	private Model model;

	// Konstruktor
	public Controller(Model model) {
		this.setModel(model);
	}
	
	public abstract void actionPerformed(ActionEvent e);

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

}

/**
 * Der Controller f�r einen Weiter-Button.
 * @author paulj
 *
 */
class nextButtonControl extends Controller {

	public nextButtonControl(Model model) {
		super(model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getModel().switchPanel();
	}
	
}

/**
 * Der Controller f�r einen Zur�ck-Button.
 * @author paulj
 *
 */
class backButtonControl extends Controller {

	public backButtonControl(Model model) {
		super(model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getModel().switchPanel(false);
	}
	
}

/**
 * Der Controller f�r einen Weiter-Button bei den Aufgabe.
 * @author paulj
 *
 */
class nextButtonTaskControl extends Controller {

	public nextButtonTaskControl(Model model) {
		super(model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getModel().switchPanel(true);
	}
	
}

/**
 * Der Controller f�r den Men�punkt '�ber'.
 * @author paulj
 *
 */
class aboutMenuControl extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public aboutMenuControl() {
		putValue(Action.NAME, "�ber...");
		putValue(Action.MNEMONIC_KEY, 0);
		// Tastaturk�rzel setzen (bei Erstellung)
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String aboutText = "Projekt im Rahmen des Moduls Assistenzsysteme bei Prof. Dr. Alexander Maier.\n"
				+ "Von Jona Brockhaus, Finn K�hler und Janis Paul.";
		JOptionPane.showMessageDialog(new JFrame(), aboutText, "�ber", JOptionPane.PLAIN_MESSAGE);
	}
}

/**
 * Der Controller f�r den Men�punkt 'Startseite'.
 * @author paulj
 *
 */
class homeMenuControl extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private Model model;

	public homeMenuControl(Model model) {
		putValue(Action.NAME, "Startseite");
		// Tastaturk�rzel setzen (bei Erstellung)
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_0, KeyEvent.CTRL_DOWN_MASK));
		this.model = model;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		model.toMenuPanel();
	}
}

/**
 * Der Controller f�r den Men�punkt 'Beenden'.
 * @author paulj
 *
 */
class quitMenuControl extends AbstractAction {
	private static final long serialVersionUID = 1L;

	public quitMenuControl() {
		putValue(Action.NAME, "Beenden");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
