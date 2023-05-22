package gui;

public class IPC {
	private String name;
	private Task[] tasks;

	public IPC(String name) {
		this.setName(name);
		this.setTasks(null);
	}

	public IPC(String name, Task[] tasks) {
		this.setName(name);
		this.setTasks(tasks);
	}

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
	 * @return the tasks
	 */
	public Task[] getTasks() {
		return tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(Task[] tasks) {
		this.tasks = tasks;
	}
}