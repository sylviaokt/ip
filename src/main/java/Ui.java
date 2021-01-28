import java.util.ArrayList;
import java.util.Scanner;

public class Ui {

    public Ui() {
    }

    public void showLoadingError() {
        System.out.println("There was an error in loading the file as it could not be found.");
    }

    public void showGreetings() {
        System.out.println("Hello! I'm Bob :D\n" + "What can I do for you?");
    }

    public String listenToCommand() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public boolean respondToCommand(String userInput, TaskList tasks, Storage storage) {
        Parser parser = new Parser();
        Command command = parser.parseCommand(userInput);
        switch (command) {
        case BYE:
            System.out.println("Bye! See you soon!");
            return false;
        case LIST:
            System.out.println("This is your list of tasks:\n");
            System.out.println(tasks.toString());
            return true;
        case FIND:
            TaskList findTask = new TaskList();
            try {
                String name = parser.parseName(userInput, 5);
                ArrayList<Task> taskList = tasks.getTaskList();
                for (Task task : taskList) {
                    String taskName = task.name;
                    if (taskName.contains(name)) {
                        findTask.addTask(task);
                    }
                }
                if (findTask.getSize() != 0) {
                    System.out.println("Here are your search results: \n");
                    System.out.println(findTask.toString());
                } else {
                    System.out.println("Oops, there is no matching task!");
                }
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            }
            return true;
        case DONE:
            try {
                int index = parser.parseNumber(userInput, 5);
                Task updatedTask = tasks.changeStatus(index, true);
                System.out.println("Good job! This task has been marked as done :)\n" + updatedTask);
                storage.rewrite(tasks);
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            }
            return true;
        case TODO:
            try {
                String taskName = parser.parseName(userInput, 5);
                Todo newTodo = new Todo(taskName);
                tasks.addToDo(newTodo);
                System.out.println("Alright, I have added this new todo.\n" +
                        newTodo + "\n" + "There are a total of " + tasks.getSize() + " tasks now.");
                storage.appendToDo(newTodo);
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            }
            return true;
        case EVENT:
            try {
                int taskIndex = userInput.indexOf("/at");
                if (taskIndex != -1) {
                    Event newEvent = parser.parseEvent(userInput, taskIndex);
                    tasks.addEvent(newEvent);
                    System.out.println("Alright, I have added this new event.\n" +
                            newEvent + "\n" +
                            "There is a total of " + tasks.getSize() + " tasks now.");
                    storage.appendEvent(newEvent);
                } else {
                    System.out.println("There is no event timing detected!\n" +
                            "Please try again with a correct format");
                }
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            }
            return true;
        case DEADLINE:
            try {
                int deadlineIndex = userInput.indexOf("/by");
                if (deadlineIndex != -1) {
                    Deadline newDeadline = parser.parseDeadline(userInput, deadlineIndex);
                    tasks.addDeadline(newDeadline);
                    System.out.println("Alright, I have added this new deadline.\n" +
                            newDeadline + "\n" +
                            "There is a total of " + tasks.getSize() + " tasks now.");
                    storage.appendDeadline(newDeadline);
                } else {
                    System.out.println("There is no deadline time and date detected!\n" +
                            "Please try again with a correct format");
                }
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            }
            return true;
        case DELETE:
            try {
                int index = parser.parseNumber(userInput, 7);
                Task removedTask = tasks.removeTask(index);
                System.out.println("Alright, this task has been removed.\n" +
                        removedTask + "\nThere are " + tasks.getTaskList() + " tasks left.");
                storage.rewrite(tasks);
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            }
            return true;
        case INVALID:
            System.out.println("Sorry, I have no idea what that means :( Please try again!");
            return true;
        default:
            return true;
        }
    }
}