package taskmanager;

import task.Subtask;
import task.Task;
import task.Epic;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    Task createTask(Task task) throws TaskOverlapException;

    Task getTaskById(int id);

    List<Task> getAllTasks();

    void deleteTaskById(int id);

    void deleteAllTasks();

    void updateTask(Task task);

    Epic createEpic(Epic epic);

    Epic getEpicById(int id);

    List<Epic> getAllEpics();

    void deleteEpicById(int id);

    void deleteAllEpics();

    void updateEpic(Epic epic);

    Subtask createSubtask(Subtask subtask) throws TaskOverlapException;

    Subtask getSubtaskById(int id);

    List<Subtask> getAllSubtasks();

    void deleteSubtaskById(int id);

    void deleteAllSubtasks();

    void updateSubtask(Subtask subtask);

    List<Subtask> getSubtasksOfEpic(int epicId);

    void updateEpicStatus(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
