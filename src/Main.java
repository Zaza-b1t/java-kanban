import task.Task;
import task.Epic;
import task.Subtask;
import task.Status;
import taskmanager.TaskManager;
import managers.Managers;
import taskmanager.TaskOverlapException;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws TaskOverlapException {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2023, 1, 1, 9, 0));
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2023, 1, 1, 11, 0));
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);
        Subtask sub1 = new Subtask(0, "Подзадача 1.1", "Описание подзадачи 1.1", Status.NEW, epic1.getId(),
                Duration.ofMinutes(15), LocalDateTime.of(2023, 1, 1, 10, 0));
        Subtask sub2 = new Subtask(0, "Подзадача 1.2", "Описание подзадачи 1.2", Status.NEW, epic1.getId(),
                Duration.ofMinutes(20), LocalDateTime.of(2023, 1, 1, 10, 30));
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2");
        manager.createEpic(epic2);
        Subtask sub3 = new Subtask(0, "Подзадача 2.1", "Описание подзадачи 2.1", Status.NEW, epic2.getId(),
                Duration.ofMinutes(25), LocalDateTime.of(2023, 1, 2, 9, 0));
        manager.createSubtask(sub3);

        printAllTasks(manager);

        task1.setStatus(Status.DONE);
        manager.updateTask(task1);

        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);

        printAllTasks(manager);

        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
