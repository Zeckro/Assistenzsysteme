package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
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
	private ArrayList<Task[]> tasks = new ArrayList<>();
	private ArrayList<String> tasksName = new ArrayList<>();
	private Task[] currentTasks;
	private String image = "image.jpg";

	// Konstruktor
	public View(Model model) {
		this.model = model;
		Task[] test = {new Task("Test1S1", "Schritt 1 von Test1"), new Task("Test1S2", "Schritt 2 von Test1")};
		tasks.add(test);
		tasksName.add("Test 1");
		Task[] test2 = {new Task("Test2S1", "Schritt 1 von Test2"), new Task("Test2S2", "Schritt 2 von Test2"), new Task("Test2S3", "Schritt 3 von Test2")};
		tasks.add(test2);
		tasksName.add("Test 2");
		
		cardLayout = new CardLayout(0, 0);
		setOutputTxtAreas();

		setTitle("Assistenzsystem für die Montage von Bauteilen GUI");
		setMinimumSize(new Dimension(900, 800));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(cardLayout);
		
		JPanel menuPanel = new JPanel();
		menuPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		menuPanel.setLayout(new BorderLayout(3, 3));
		add(menuPanel, "Menu");
		
		// Menüzeile hinzufügen
		createMenuBar();
		
		JPanel optionsPanel = createOptionsPanel();
		menuPanel.add(optionsPanel);

		JPanel southArea = createNextButtonOutputPanel();
		menuPanel.add(southArea, BorderLayout.SOUTH);
		
		// Panels für Eingabe bei Funktionen erstellen
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
		taskInfo.add(new ImagePanel(image), BorderLayout.CENTER);
		tasksPanel.add(taskInfo);
		JPanel southAreaTasks = createBackNextButtonsOutputPanel(txtOutputTasks);
		tasksPanel.add(southAreaTasks, BorderLayout.SOUTH);
		
		refresh();

		setLocation(250, 60);
		setBounds(100, 100, 450, 310);
		setVisible(true);
	}

	// Getters/Setters
	
	/**
	 * @return the cardLayout
	 */
	public CardLayout getCardLayout() {
		return cardLayout;
	}

	/**
	 * @return the currentTasks
	 */
	public ArrayList<Task[]> getTasks() {
		return tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(ArrayList<Task[]> tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return the currentTasks
	 */
	public Task[] getCurrentTasks() {
		return currentTasks;
	}

	/**
	 * @param currentTasks the currentTasks to set
	 */
	public void setCurrentTasks(Task[] currentTasks) {
		this.currentTasks = currentTasks;
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

	// Methoden

	/**
	 * Wird ausgeführt, wenn sich etwas an den Daten im Model ändert.
	 */
	public void refresh() {
		txtOutputMenu.setText(model.getOutput());
		txtOutputTasks.setText(model.getOutput());
	}
	
	/**
	 * Erstellt die benötigten Ausgabe-TextAreas.
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
	 * Erstellt die Menüzeile.
	 */
	public void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// Menü-Einträge werden angelegt und zur Menüleiste hinzugefügt
		JMenu fileMenu = new JMenu("Weiteres");
		menuBar.add(fileMenu);
		JMenu helpMenu = new JMenu("Hilfe");
		menuBar.add(helpMenu);

		helpMenu.add(new aboutMenuControl());

		fileMenu.add(new homeMenuControl(model));
		fileMenu.add(new quitMenuControl());

		// Menüleiste und Fenster/Frame werden verbunden
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
		String array[] = new String[tasksName.size()];
		for (int i = 0; i < tasksName.size(); i++) {
			array[i]=(tasksName.get(i));
		}
		cb = new JComboBox<String>(array);
		panel.add(cb, BorderLayout.NORTH);
		panel.add(new ImagePanel("image.jpg"), BorderLayout.CENTER);
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
		panel.setLayout(new GridBagLayout());
		// Layout für Weiter-Button definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.weightx = 0.5;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.PAGE_END;
		panel.add(btnNext, constraints); // Button mit Constraints hinzufügen
		// Layout für TextArea definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(scrollTxt, constraints); // ScrollPane mit Constraints zum Panel hinzufügen
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
		JButton btnBack = new JButton("Zurück");
		btnBack.setEnabled(true);
		btnBack.addActionListener(new backButtonControl(model));
		btnNextStep = new JButton("Weiter");
		btnNextStep.setEnabled(true);
		btnNextStep.addActionListener(new nextButtonTaskControl(model));
		panel.setLayout(new GridBagLayout());
		// Layout für Zurück-Button definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.weightx = 0.5;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.PAGE_START;
		panel.add(btnBack, constraints); // Button mit Constraints hinzufügen
		// Layout für Start-Button definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.weightx = 0.5;
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 10);
		constraints.anchor = GridBagConstraints.PAGE_END;
		panel.add(btnNextStep, constraints); // Button mit Constraints hinzufügen
		// Layout für TextArea definieren
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.ipady = 0;
		constraints.gridwidth = 2;
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(scrollTxt, constraints); // ScrollPane mit Constraints zum Panel hinzufügen
		return panel;
	}
	
	/**
	 * Wechselt zum Panel, dessen Name übergeben werden muss.
	 * @param panel Die Bezeichnung für das Panel
	 */
	public void switchToPanel(String panel) {
		cardLayout.show(getContentPane(), panel);
		refresh();
	}
	
	public class ImagePanel extends JPanel{

	    private static final long serialVersionUID = 1L;
		private ImageIcon icon;
		private double imageScale = 1.0; // Initial image scale

	    public ImagePanel(String path) {
	       try {
	          icon = new ImageIcon(path);
	          int width = icon.getImage().getWidth(null)/2;
	          int height = icon.getImage().getHeight(null)/2;
	          setPreferredSize(new Dimension(width, height));
	       } catch (Exception ex) {
	            // handle exception...
	       }
	       
	       addComponentListener(new ComponentAdapter() {
	            @Override
	            public void componentResized(ComponentEvent e) {
	                // Adjust the image scale to fit the panel size
	                int panelWidth = getWidth();
	                int panelHeight = getHeight();
	                int imageWidth = icon.getIconWidth();
	                int imageHeight = icon.getIconHeight();

	                double widthScale = (double) panelWidth / imageWidth;
	                double heightScale = (double) panelHeight / imageHeight;
	                imageScale = Math.min(widthScale, heightScale);

	                repaint();
	            }
	        });
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	    	super.paintComponent(g);
	        if (icon != null) {
	            int width = (int) (icon.getIconWidth() * imageScale);
	            int height = (int) (icon.getIconHeight() * imageScale);
	            int x = (getWidth() - width) / 2;
	            int y = (getHeight() - height) / 2;

	            g.drawImage(icon.getImage(), x, y, width, height, null);
	        }
	    }

	}
}
