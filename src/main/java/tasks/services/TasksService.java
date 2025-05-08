package tasks.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tasks.model.ArrayTaskList;
import tasks.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class TasksService {

    private Logger logger = Logger.getLogger(TasksService.class.getName());
    private ArrayTaskList tasks;

    public TasksService(ArrayTaskList tasks) {
        this.tasks = tasks;
    }

    public ObservableList<Task> getObservableList() {
        return FXCollections.observableArrayList(tasks.getAll());
    }

    public String getIntervalInHours(Task task) {
        int seconds = task.getRepeatInterval();
        int minutes = seconds / DateService.SECONDS_IN_MINUTE;
        int hours = minutes / DateService.MINUTES_IN_HOUR;
        minutes = minutes % DateService.MINUTES_IN_HOUR;
        return formTimeUnit(hours) + ":" + formTimeUnit(minutes);//hh:MM
    }

    public String formTimeUnit(int timeUnit) {
        StringBuilder sb = new StringBuilder();
        if (timeUnit < 10) sb.append("0");
        if (timeUnit == 0) sb.append("0");
        else {
            sb.append(timeUnit);
        }
        return sb.toString();
    }

    public int parseFromStringToSeconds(String stringTime) {//hh:MM
        String[] units = stringTime.split(":");
        int hours = Integer.parseInt(units[0]);
        int minutes = Integer.parseInt(units[1]);
        int result = (hours * DateService.MINUTES_IN_HOUR + minutes) * DateService.SECONDS_IN_MINUTE;
        return result;
    }

    public Iterable<Task> filterTasks(Date start, Date end) throws IllegalArgumentException {

        if (end.before(start)) {
            throw new IllegalArgumentException("Filter end date should be after start date");
        }

        ArrayList<Task> showedTasks = new ArrayList<>(getObservableList());
        ArrayList<Task> incomingTasks = new ArrayList<>();
        for (Task t : showedTasks) {
            Date nextTime = t.nextTimeAfter(start);

            if (nextTime == null) {
                continue;
            }
            if (nextTime.before(end)) {
                incomingTasks.add(t);
                logger.info(t.getTitle());
                continue;
            }
            if (nextTime.equals(end)) {
                incomingTasks.add(t);
                logger.info(t.getTitle());
            }
        }
        return incomingTasks;
    }
}
