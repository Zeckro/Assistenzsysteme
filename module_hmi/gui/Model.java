package gui;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class Model {
	// View des Programms
	private View view;
	
	// Daten dieses Modells 
	private String displayOutput;
	private Task currentTask;
	private int taskStep = -1;
	private IMqttClient client;
	
	// Konstruktor 
	public Model() {
		displayOutput = "Willkommen beim Assistent für die Montage von Bauteilen!\n"
				+ "Durch Auswahl eines der Aufgaben und Klicken auf 'Weiter' "
				+ "können die verschiedenen Funktionen genutzt werden.";
	}
	
	/**
	 * Fügt eine neue View hinzu
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
	 * Setzt die Größe auf Standard zurück.
	 */
	public void setStandardBounds() {
		view.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	public void mqttConnectAndSubscribe() {
		try {
			client = new MqttClient("tcp://192.168.137.1:1883", "hmi");
			MqttConnectOptions options = new MqttConnectOptions();
			options.setCleanSession(true); // Setze Clean Session auf true, um alte Sitzungen zu löschen
			client.connect(options);

			client.subscribe("master/choose_list", 2, new IMqttMessageListener() {
			    @Override
			    public void messageArrived(String topic, MqttMessage message) throws Exception {
			    	String msgStr = new String(message.getPayload());
			        System.out.println(topic + ": " + msgStr);
			        JSONObject json = new JSONObject(msgStr);
			        /*IPC[] ipcs = {new IPC(
			        		(String) json.get("name")
			        )};
			        view.setArrayIPCs(ipcs);*/
			        publishAssemblyIndex(0);
			        view.refresh();
			    }
			});
			client.subscribe("master/current_task", 2, new IMqttMessageListener() {
			    @Override
			    public void messageArrived(String topic, MqttMessage message) throws Exception {
			    	String msgStr = new String(message.getPayload());
			        System.out.println(topic + ": " + msgStr);
			        JSONObject json = new JSONObject(msgStr);
			        currentTask = new Task(
			        		(int) json.get("index"), 
			        		(String) json.get("name"), 
			        		(String) json.get("description"),
			        		(int) json.getInt("max_index")
			        );
			        taskStep = currentTask.getIndex();
			        switchPanel();
			        view.refresh();
			    }
			});
			client.subscribe("image_topic", 0, new IMqttMessageListener() {
			    @Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					byte[] msg = message.getPayload();
//					System.out.println(topic + ": ");
					BufferedImage image = null;

					try (ByteArrayInputStream bis = new ByteArrayInputStream(msg)) {
						image = ImageIO.read(bis);
					} catch (IOException e) {
						e.printStackTrace();
					}

					if (image != null) {
						view.setImageLabel(new ImageIcon(image));
						view.refresh();
					}
				}
			});
		} catch (Exception e) {
			displayOutput = "Bei der Verbindung zum MQTT-Server ist ein Fehler aufgetreten.\n" +
					"Das Programm muss neu gestartet werden.";
		}
	}

	/**
	 * @return the client
	 */
	public IMqttClient getClient() {
		return client;
	}

	/**
	 * @param close the client
	 */
	public void closeClient() {
		try {
			client.close();
		} catch (MqttException e) {
			
		}
	}

	/**
	 * Wechselt je nach Combobox-Auswahl zum richtigen Panel.
	 */
	public void switchPanel() {
		if (currentTask != null) {
			setNameAndDescription();
			view.getBtnNextStep().setText("Weiter");
			view.switchToPanel("Tasks");
		}/* else if (view.getCurrentTasks() != null && taskIndex > 0) {
			
		}*/ else {
			displayOutput = "Ein Fehler ist aufgetreten.";
			view.switchToPanel("Menu");
		}
	}

	/**
	 * Wechselt zum richtigen Panel.
	 * @param next True setzen wenn zum nächsten Schritt der Task, false wenn zum vorherigen Schritt.
	 */
	public void switchPanel(boolean next) {
		if (currentTask != null && taskStep > 0 && next) {
			publishStep(taskStep, taskStep++);
			if (taskStep > currentTask.getMaxIndex()) {
				toMenuPanel();
			} else {
				setNameAndDescription();
				if (taskStep == currentTask.getMaxIndex()) {
					view.getBtnNextStep().setText("Fertig");
				}
				view.refresh();
			}
		} else if (currentTask != null && taskStep > 0 && !next) {
			publishStep(taskStep, taskStep--);
			if (taskStep == -1) {
				toMenuPanel();
				displayOutput = "IPC ist fertig. Einen neuen IPC auswählen.";
			} else {
				setNameAndDescription();
				view.getBtnNextStep().setText("Weiter");
				view.refresh();
			}
		} else {
			displayOutput = "Ein Fehler ist aufgetreten.";
			view.switchToPanel("Menu");
		}
	}
	
	/**
	 * Wechselt zum Hauptmenü-Panel.
	 */
	public void toMenuPanel() {
		taskStep = 0;
		displayOutput = "Durch Auswahl eines der Aufgaben und Klicken auf 'Weiter' "
				+ "können die verschiedenen Funktionen genutzt werden.";
		view.switchToPanel("Menu");
	}
	
	/**
	 * Setzt den Namen und die Beschreibung der aktuellen Aufgabe.
	 */
	public void setNameAndDescription() {
		view.getTfTaskTitle().setText(
				currentTask.getIndex() + " von " + currentTask.getMaxIndex()
					+ ": " + currentTask.getName()
		);
		displayOutput = currentTask.getDescription();
	}
	
	public void publishStep(int currentIndex, int nextIndex) {
		try {
			client.publish(
					"submodule/task", 
					("{current_task: " + currentIndex + ", new_task: " + nextIndex + "}").getBytes(), 
					1, true
			);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void publishAssemblyIndex(int index) {
		try {
			client.publish(
					"submodule/choose_list", 
					(index + "").getBytes(), 
					1, true
			);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
