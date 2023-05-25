package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class View extends JFrame {
	private static final long serialVersionUID = 1L;

	// Referenz auf das Model 
	private Model model;
	
	// AWT
	private CardLayout cardLayout;

	// Swing
	private JTextArea txtOutputMenu, txtOutputTasks;
	private JTextField tfTaskTitle;
	private JButton btnNext, btnNextStep;
	private JComboBox<String> cb;
//	private ArrayList<Task[]> tasks = new ArrayList<>();
//	private ArrayList<String> tasksName = new ArrayList<>();
	private String[] ipcs = {"No IPCs available"};
//	private String image = "image.jpg";
//	private byte[] imageBytes;
	private JLabel imageLabel = new JLabel();
	private JLabel imageLabel2 = new JLabel();

	// Konstruktor
	public View(Model model) {
		this.model = model;
		
		cardLayout = new CardLayout(0, 0);
		setOutputTxtAreas();

		setTitle("Assistenzsystem f�r die Montage von Bauteilen GUI");
		setMinimumSize(new Dimension(900, 800));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(cardLayout);
		
		JPanel menuPanel = new JPanel();
		menuPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		menuPanel.setLayout(new BorderLayout(3, 3));
		add(menuPanel, "Menu");
		
		// Men�zeile hinzuf�gen
		createMenuBar();
		
		JPanel optionsPanel = createOptionsPanel();
		menuPanel.add(optionsPanel);

		JPanel southArea = createNextButtonOutputPanel();
		menuPanel.add(southArea, BorderLayout.SOUTH);
		
		// Panels f�r Eingabe bei Funktionen erstellen
		JPanel tasksPanel = new JPanel();
//		tasksPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		tasksPanel.setLayout(new BorderLayout(3, 3));
		getContentPane().add(tasksPanel, "Tasks");
		
		JPanel taskInfo = new JPanel();
//		taskInfo.setBorder(new EmptyBorder(5, 5, 5, 5));
		taskInfo.setLayout(new BorderLayout(3, 3));
		tfTaskTitle = new JTextField();
		tfTaskTitle.setEditable(false);
		tfTaskTitle.setText("");
		tfTaskTitle.setHorizontalAlignment(SwingConstants.CENTER);
		taskInfo.add(tfTaskTitle, BorderLayout.NORTH);
		JPanel imagePanel = new JPanel();
		imagePanel.add(imageLabel2);
		taskInfo.add(imagePanel, BorderLayout.CENTER);
		tasksPanel.add(taskInfo);
		JPanel southAreaTasks = createBackNextButtonsOutputPanel(txtOutputTasks);
		tasksPanel.add(southAreaTasks, BorderLayout.SOUTH);
		
		refresh();

		setLocation(250, 60);
		setBounds(100, 100, 450, 310);
		setVisible(true);
		
		model.mqttConnectAndSubscribe();
	}

	// Getters/Setters
	
	/**
	 * @return the cardLayout
	 */
	public CardLayout getCardLayout() {
		return cardLayout;
	}

//	/**
//	 * @return the currentTasks
//	 */
//	public ArrayList<Task[]> getTasks() {
//		return tasks;
//	}
//
//	/**
//	 * @param tasks the tasks to set
//	 */
//	public void setTasks(ArrayList<Task[]> tasks) {
//		this.tasks = tasks;
//	}
//
//	/**
//	 * @return the currentTasks
//	 */
//	public Task[] getCurrentTasks() {
//		return currentTasks;
//	}
//
//	/**
//	 * @param currentTasks the currentTasks to set
//	 */
//	public void setCurrentTasks(Task[] currentTasks) {
//		this.currentTasks = currentTasks;
//	}
	
	/**
	 * @return the ipcs
	 */
	public String[] getIpcs() {
		return ipcs;
	}
	
	/**
	 * @param ipcs the ipcs to set
	 */
	public void setIpcs(String[] ipcs) {
		this.ipcs = ipcs;
	}
	
	/**
	 * @return the tfInputOptionOne
	 */
	public JTextField getTfTaskTitle() {
		return tfTaskTitle;
	}

	/**
	 * @return the cb
	 */
	public JComboBox<String> getComboBox() {
		return cb;
	}

	/**
	 * @return the btnNext
	 */
	public JButton getBtnNext() {
		return btnNext;
	}

	/**
	 * @return the btnStart
	 */
	public JButton getBtnNextStep() {
		return btnNextStep;
	}

	/**
	 * @return the imageLabel
	 */
	public JLabel getImageLabel() {
		return imageLabel;
	}

	/**
	 * @param imageLabel the imageLabel to set
	 */
	public void setImageLabel(ImageIcon icon) {
	    this.imageLabel.setIcon(icon);
	    this.imageLabel2.setIcon(icon);
	}

	// Methoden

	/**
	 * Wird ausgef�hrt, wenn sich etwas an den Daten im Model �ndert.
	 */
	public void refresh() {
		txtOutputMenu.setText(model.getOutput());
		txtOutputTasks.setText(model.getOutput());
	}
	
	/**
	 * Erstellt die ben�tigten Ausgabe-TextAreas.
	 */
	public void setOutputTxtAreas() {
		txtOutputMenu = new JTextArea(6, 10);
		txtOutputMenu.setEditable(false);
		txtOutputMenu.setLineWrap(true);
		txtOutputMenu.setWrapStyleWord(true);
		
		txtOutputTasks = new JTextArea(6, 10);
		txtOutputTasks.setEditable(false);
		txtOutputTasks.setLineWrap(true);
		txtOutputTasks.setWrapStyleWord(true);
	}
	
	/**
	 * Erstellt die Men�zeile.
	 */
	public void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// Men�-Eintr�ge werden angelegt und zur Men�leiste hinzugef�gt
		JMenu fileMenu = new JMenu("Weiteres");
		menuBar.add(fileMenu);
		JMenu helpMenu = new JMenu("Hilfe");
		menuBar.add(helpMenu);

		helpMenu.add(new aboutMenuControl());

		fileMenu.add(new homeMenuControl(model));
		fileMenu.add(new quitMenuControl());

		// Men�leiste und Fenster/Frame werden verbunden
		setJMenuBar(menuBar);

		setVisible(true);
	}
	
	/**
	 * Erstellt ein Panel mit den drei Auswahloptionen.
	 * @return Das erstellte Optionen-Panel
	 */
	public JPanel createOptionsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(3,3));
		cb = new JComboBox<String>(ipcs);
		panel.add(cb, BorderLayout.NORTH);
		JPanel imagePanel = new JPanel();
		imagePanel.add(imageLabel);
		panel.add(imagePanel, BorderLayout.CENTER);
		return panel;
	}
	
	/**
	 * Erstellt ein Panel mit Weiter-Button und Ausgabe-Textfeld.
	 * @return Das erstellte Panel
	 */
	public JPanel createNextButtonOutputPanel() {
		JPanel panel = new JPanel();
		GridBagConstraints constraints = new GridBagConstraints();
		JScrollPane scrollTxt = new JScrollPane(txtOutputMenu); // Scrollbare TextArea, falls Text zu lang
		btnNext = new JButton("Weiter");
		btnNext.addActionListener(new nextButtonControl(model));
		Dimension btnSize = btnNext.getPreferredSize();
        btnSize.height = 50; // bevorzugte H�he setzen
        btnNext.setPreferredSize(btnSize);
		panel.setLayout(new GridBagLayout());
		// Layout f�r Weiter-Button definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.weightx = 0.5;
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.PAGE_END;
		panel.add(btnNext, constraints); // Button mit Constraints hinzuf�gen
		// Layout f�r TextArea definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(scrollTxt, constraints); // ScrollPane mit Constraints zum Panel hinzuf�gen
		return panel;
	}
	
	/**
	 * Erstellt ein Panel mit Weiter-Button und Ausgabe-Textfeld.
	 * @return Das erstellte Panel
	 */
	public JPanel createBackNextButtonsOutputPanel(JTextArea txt) {
		JPanel panel = new JPanel();
		GridBagConstraints constraints = new GridBagConstraints();
		JScrollPane scrollTxt = new JScrollPane(txt); // Scrollbare TextArea, falls Text zu lang
		JButton btnBack = new JButton("Zur�ck");
		btnBack.setEnabled(true);
		btnBack.addActionListener(new backButtonControl(model));
		Dimension btnSize = btnBack.getPreferredSize();
        btnSize.height = 50; // bevorzugte H�he setzen
        btnBack.setPreferredSize(btnSize);
		btnNextStep = new JButton("Weiter");
		btnNextStep.setEnabled(true);
		btnNextStep.addActionListener(new nextButtonTaskControl(model));
//		btnSize = btnNextStep.getPreferredSize();
//        btnSize.height = 50; // bevorzugte H�he setzen
        btnNextStep.setPreferredSize(btnSize);
		panel.setLayout(new GridBagLayout());
		// Layout f�r Zur�ck-Button definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.weightx = 0.5;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.PAGE_START;
		panel.add(btnBack, constraints); // Button mit Constraints hinzuf�gen
		// Layout f�r Start-Button definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.weightx = 0.5;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.PAGE_END;
		panel.add(btnNextStep, constraints); // Button mit Constraints hinzuf�gen
		// Layout f�r TextArea definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(scrollTxt, constraints); // ScrollPane mit Constraints zum Panel hinzuf�gen
		return panel;
	}
	
	/**
	 * Wechselt zum Panel, dessen Name �bergeben werden muss.
	 * @param panel Die Bezeichnung f�r das Panel
	 */
	public void switchToPanel(String panel) {
		cardLayout.show(getContentPane(), panel);
		refresh();
	}
}
