package bob.gui;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import bob.BobException;
import bob.processor.Bob;
import bob.task.Task;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for MainWindow. Provides the layout for the other controls.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bob bob;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/patrick.png"));
    private Image bobImage = new Image(this.getClass().getResourceAsStream("/images/bob.png"));

    /**
     * Initializes the GUI.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        String greeting = "Hello! I'm Bob :D\nWhat can I do for you?\n"
                + "Type 'help' for a list of commands.\n";
        dialogContainer.getChildren().addAll(
                DialogBox.getBobDialog(greeting, bobImage)
        );
    }

    public void setBob(Bob d) {
        bob = d;
        for (Task task : bob.getTasks().getTaskList()) {
            if (task.getReminderDateTime() != null) {
                setReminder(task);
            }
        }
    }

    public void setReminder(Task task) {
        LocalDateTime reminderTime = task.getReminderDateTime();
        Timer timer = new Timer();
        TimerTask newTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dialogContainer.getChildren().addAll(
                                DialogBox.getBobDialog("*Reminder: " + task.toString(), bobImage));
                    }
                });
                task.removeReminder();
                bob.getTasks().removeReminder(reminderTime, task);
                try {
                    bob.getStorage().rewrite(bob.getTasks());
                } catch (BobException e) {
                    System.out.println(e.getMessage());
                }
            }
        };
        Date date = Date.from(reminderTime.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(newTask, date);
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = bob.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBobDialog(response, bobImage)
        );
        if (response.startsWith("A new reminder")) {
            setReminder(bob.getTasks().getTaskWithReminder());
        }
        userInput.clear();
        if (response.equals("Bye! See you soon!")) {
            Platform.exit();
        }
    }
}
