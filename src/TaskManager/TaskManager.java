package TaskManager;

import Task.Subtask;
import Task.Task;
import Task.Epic;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    Task createTask(Task task);

    Task getTaskById(int id);

    ArrayList<Task> getAllTasks();

    void deleteTaskById(int id);

    void deleteAllTasks();

    void updateTask(Task task);

    Epic createEpic(Epic epic);

    Epic getEpicById(int id);

    ArrayList<Epic> getAllEpics();

    void deleteEpicById(int id);

    void deleteAllEpics();

    void updateEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    Subtask getSubtaskById(int id);

    ArrayList<Subtask> getAllSubtasks();

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    void updateSubtask(Subtask subtask);

    ArrayList<Subtask> getSubtasksOfEpic(int epicId);

    void updateEpicStatus(int epicId);

    List<Task> getHistory();
}
