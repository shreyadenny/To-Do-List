import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

// Memento Pattern: Memento class to store the state of the task list.

public class Main {
    public static void main(String[] args) {
        ToDoListManager toDoList = new ToDoListManager();

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Add Task");
            System.out.println("2. Mark Completed");
            System.out.println("3. Delete Task");
            System.out.println("4. View Tasks");
            System.out.println("5. Undo");
            System.out.println("6. Redo");
            System.out.println("7. Exit");

            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter due date (optional, format: yyyy-MM-dd): ");
                    String dueDateString = scanner.nextLine();
                    Date dueDate = null;
                    if (!dueDateString.isEmpty()) {
                        try {
                            dueDate = new SimpleDateFormat("yyyy-MM-dd").parse(dueDateString);
                        } catch (ParseException e) {
                            System.out.println("Invalid date format. Task not created.");
                            continue;
                        }
                    }
                    Task task = new Task(description);
                    if (dueDate != null) {
                        task.setDueDate(dueDate);
                    }
                    toDoList.addTask(task);
                    break;
                case "2":
                    System.out.print("Enter task description to mark as completed: ");
                    String completedDescription = scanner.nextLine();
                    toDoList.markCompleted(completedDescription);
                    break;
                case "3":
                    System.out.print("Enter task description to delete: ");
                    String deleteDescription = scanner.nextLine();
                    toDoList.deleteTask(deleteDescription);
                    break;
                case "4":
                    System.out.print("Filter tasks (all/completed/pending): ");
                    String filterType = scanner.nextLine().toLowerCase();
                    List<Task> tasks = toDoList.viewTasks(filterType);
                    for (Task t : tasks) {
                        System.out.println(t.toString());
                    }
                    break;
                case "5":
                    toDoList.undo();
                    break;
                case "6":
                    toDoList.redo();
                    break;
                case "7":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
class TaskListMemento {
    private final List<Task> tasks;

    public TaskListMemento(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    public List<Task> getTasks() {
        return tasks;
    }
}

// Task class using the Builder Pattern.
class Task {
    private final String description;
    private boolean completed;
    private Date dueDate;

    public Task(String description) {
        this.description = description;
        this.completed = false;
        this.dueDate = null;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String toString() {
        String status = completed ? "Completed" : "Pending";
        String dueDateStr = (dueDate != null) ? new SimpleDateFormat("yyyy-MM-dd").format(dueDate) : "No Due Date";
        return description + " - " + status + ", Due: " + dueDateStr;
    }
}

// To-Do List Manager class
class ToDoListManager {
    private final List<Task> tasks;
    private final Stack<TaskListMemento> undoStack;
    private final Stack<TaskListMemento> redoStack;

    public ToDoListManager() {
        tasks = new ArrayList<>();
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveState();
    }

    public void markCompleted(String description) {
        for (Task task : tasks) {
            if (task.toString().startsWith(description)) {
                task.setCompleted(true);
                saveState();
                return;
            }
        }
    }

    public void deleteTask(String description) {
        tasks.removeIf(task -> task.toString().startsWith(description));
        saveState();
    }

    public List<Task> viewTasks(String filterType) {
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : tasks) {
            if (filterType.equals("all") ||
                (filterType.equals("completed") && task.toString().contains("Completed")) ||
                (filterType.equals("pending") && task.toString().contains("Pending"))) {
                filteredTasks.add(task);
            }
        }

        return filteredTasks;
    }

    public void saveState() {
        undoStack.push(new TaskListMemento(tasks));
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.size() > 1) {
            redoStack.push(undoStack.pop());
            tasks.clear();
            tasks.addAll(undoStack.peek().getTasks());
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(redoStack.pop());
            tasks.clear();
            tasks.addAll(undoStack.peek().getTasks());
        }
    }
}

