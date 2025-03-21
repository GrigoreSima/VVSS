package tasks.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.model.Task;
import tasks.services.DateService;
import tasks.services.TaskIOService;
import tasks.services.TasksService;
import tasks.view.Main;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Controller {
    private static final Logger log = Logger.getLogger(Controller.class.getName());
    public ObservableList<Task> tasksList;
    TasksService service;
    DateService dateService;

    public static Stage newStage;
    public static final Stage editStage = new Stage();
    public static Stage infoStage;

    public static TableView mainTable;

    @FXML
    public TableView<Task> tasks;
    @FXML
    private TableColumn<Task, String> columnTitle;
    @FXML
    private TableColumn<Task, String> columnTime;
    @FXML
    private TableColumn<Task, String> columnRepeated;
    @FXML
    private Label labelCount;
    @FXML
    private DatePicker datePickerFrom;
    @FXML
    private TextField fieldTimeFrom;
    @FXML
    private DatePicker datePickerTo;
    @FXML
    private TextField fieldTimeTo;

    public void setService(TasksService service) {
        this.service = service;
        this.dateService = new DateService(service);
        this.tasksList = service.getObservableList();
        updateCountLabel(tasksList);
        tasks.setItems(tasksList);
        mainTable = tasks;

        tasksList.addListener((ListChangeListener.Change<? extends Task> c) -> {
                    updateCountLabel(tasksList);
                    tasks.setItems(tasksList);
                }
        );
    }

    @FXML
    public void initialize() {
        log.info("Main controller initializing");
        columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("formattedDateStart"));
        columnRepeated.setCellValueFactory(new PropertyValueFactory<>("formattedRepeated"));
    }

    private void updateCountLabel(ObservableList<Task> list) {
        labelCount.setText(list.size() + " elements");
    }

    @FXML
    public void showTaskDialogNew(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        NewController.setClickedButton((Button) source);

        try {
            newStage = new Stage();
            NewController.setCurrentStage(newStage);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/new-task.fxml"));
            Parent root = loader.load();//getClass().getResource("/fxml/new-task.fxml"));
            NewController editCtrl = loader.getController();
            editCtrl.setService(service);
            editCtrl.setTasksList(tasksList);
            editCtrl.setCurrentTask((Task) mainTable.getSelectionModel().getSelectedItem());
            newStage.setScene(new Scene(root, 600, 350));
            newStage.setResizable(false);
            newStage.initOwner(Main.primaryStage);
            newStage.initModality(Modality.APPLICATION_MODAL);//??????
            newStage.show();
        } catch (IOException e) {
            log.error("Error loading new-task.fxml");
        }
    }

    @FXML
    public void showTaskDialogEdit(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        EditController.setClickedButton((Button) source);

        try {
            EditController.setCurrentStage(editStage);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit-task.fxml"));
            Parent root = loader.load();//getClass().getResource("/fxml/new-task.fxml"));
            EditController editCtrl = loader.getController();
            editCtrl.setService(service);
            editCtrl.setTasksList(tasksList);
            editCtrl.setCurrentTask((Task) mainTable.getSelectionModel().getSelectedItem());
            editStage.setScene(new Scene(root, 600, 350));
            editStage.setResizable(false);
            editStage.initOwner(Main.primaryStage);
            editStage.initModality(Modality.APPLICATION_MODAL);//??????
            editStage.show();
        } catch (IOException e) {
            log.error("Error loading new-task.fxml");
        }
    }

    @FXML
    public void deleteTask() {
        Task toDelete = tasks.getSelectionModel().getSelectedItem();

        if (toDelete == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a task", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        tasksList.remove(toDelete);
        TaskIOService.rewriteFile(tasksList);
    }

    @FXML
    public void showDetailedInfo() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/task-info.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root, 550, 350));
            stage.setResizable(false);
            stage.setTitle("Info");
            stage.initModality(Modality.APPLICATION_MODAL);//??????
            infoStage = stage;
            stage.show();
        } catch (IOException e) {
            log.error("error loading task-info.fxml");
        }
    }

    @FXML
    public void showFilteredTasks() {
        Date start = getDateFromFilterField(datePickerFrom.getValue(), fieldTimeFrom.getText());
        Date end = getDateFromFilterField(datePickerTo.getValue(), fieldTimeTo.getText());

        if (start == null || end == null || start.after(end)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Start date cannot be after end date!", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        Iterable<Task> filtered = service.filterTasks(start, end);

        ObservableList<Task> observableTasks = FXCollections.observableList((ArrayList) filtered);
        tasks.setItems(observableTasks);
        updateCountLabel(observableTasks);
    }

    private Date getDateFromFilterField(LocalDate localDate, String time) {
        Date date = dateService.getDateValueFromLocalDate(localDate);
        return dateService.getDateMergedWithTime(time, date);
    }


    public void resetFilteredTasks() {
        tasks.setItems(tasksList);
        //abc
    }
}