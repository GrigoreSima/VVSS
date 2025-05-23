package tasks.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.model.Task;
import tasks.services.DateService;
import tasks.services.TaskIOService;
import tasks.services.TasksService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class NewController {

    private static Button clickedButton;

    private static final Logger log = Logger.getLogger(NewController.class.getName());

    public static void setClickedButton(Button clickedButton) {
        NewController.clickedButton = clickedButton;
    }

    public static void setCurrentStage(Stage currentStage) {
        NewController.currentStage = currentStage;
    }

    private static Stage currentStage;

    private Task currentTask;
    private TasksService service;
    private DateService dateService;


    private boolean incorrectInputMade;
    @FXML
    private TextField fieldTitle;
    @FXML
    private DatePicker datePickerStart;
    @FXML
    private TextField txtFieldTimeStart;
    @FXML
    private DatePicker datePickerEnd;
    @FXML
    private TextField txtFieldTimeEnd;
    @FXML
    private TextField fieldInterval;
    @FXML
    private CheckBox checkBoxActive;
    @FXML
    private CheckBox checkBoxRepeated;

    private static final String DEFAULT_START_TIME = "8:00";
    private static final String DEFAULT_END_TIME = "10:00";
    private static final String DEFAULT_INTERVAL_TIME = "0:30";

    public void setTasksList(ObservableList<Task> tasksList) {
        TaskIOService.setTaskList(tasksList);
    }

    public void setService(TasksService service) {
        this.service = service;
        this.dateService = new DateService(service);
    }

    public void setCurrentTask(Task task) {
        this.currentTask = task;
        if (clickedButton.getId().equals("btnNew")) {
            initNewWindow("New Task");
        }
    }

    @FXML
    public void initialize() {
        log.info("new/edit window initializing");
    }

    private void initNewWindow(String title) {
        currentStage.setTitle(title);
        datePickerStart.setValue(LocalDate.now());
        txtFieldTimeStart.setText(DEFAULT_START_TIME);
    }

    @FXML
    public void switchRepeatedCheckbox(ActionEvent actionEvent) {
        CheckBox source = (CheckBox) actionEvent.getSource();
        if (source.isSelected()) {
            hideRepeatedTaskModule(false);
        } else if (!source.isSelected()) {
            hideRepeatedTaskModule(true);
        }
    }

    private void hideRepeatedTaskModule(boolean toShow) {
        datePickerEnd.setDisable(toShow);
        fieldInterval.setDisable(toShow);
        txtFieldTimeEnd.setDisable(toShow);

        datePickerEnd.setValue(LocalDate.now());
        txtFieldTimeEnd.setText(DEFAULT_END_TIME);
        fieldInterval.setText(DEFAULT_INTERVAL_TIME);
    }

    @FXML
    public void saveChanges() {
        Task task = collectFieldsData();
        if (incorrectInputMade) return;

        if (currentTask == null) {//no task was chosen -> add button was pressed=
            TaskIOService.add(task);
        }
        Controller.newStage.close();
    }

    @FXML
    public void closeDialogWindow() {
        Controller.newStage.close();
    }

    private Task collectFieldsData() {
        incorrectInputMade = false;
        Task result = null;
        try {
            result = makeTask();
        } catch (RuntimeException e) {
            incorrectInputMade = true;
            try {
                Stage stage = new Stage();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/field-validator.fxml"));
                stage.setScene(new Scene(root, 350, 150));
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (IOException ioe) {
                log.error("error loading field-validator.fxml");
            }
        }
        return result;
    }

    private Task makeTask() {
        Task result;
        String newTitle = fieldTitle.getText();
        Date startDateWithNoTime = dateService.getDateValueFromLocalDate(datePickerStart.getValue());//ONLY date!!without time
        Date newStartDate = dateService.getDateMergedWithTime(txtFieldTimeStart.getText(), startDateWithNoTime);
        if (checkBoxRepeated.isSelected()) {
            Date endDateWithNoTime = dateService.getDateValueFromLocalDate(datePickerEnd.getValue());
            Date newEndDate = dateService.getDateMergedWithTime(txtFieldTimeEnd.getText(), endDateWithNoTime);
            int newInterval = service.parseFromStringToSeconds(fieldInterval.getText());
            if (newStartDate.after(newEndDate)) throw new IllegalArgumentException("Start date should be before end");
            result = new Task(newTitle, newStartDate, newEndDate, newInterval);
        } else {
            result = new Task(newTitle, newStartDate);
        }
        boolean isActive = checkBoxActive.isSelected();
        result.setActive(isActive);
        System.out.println(result);
        return result;
    }


}
