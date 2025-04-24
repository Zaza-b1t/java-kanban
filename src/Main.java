import Task.*;
import TaskManager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = new Task(0, "Задача 1", "", Status.NEW);
        Task task2 = new Task(0, "Задача 2", "", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic(0, "Эпик 1", "");
        manager.createEpic(epic1);
        Subtask sub1 = new Subtask(0, "Подзадача 1.1", "", Status.NEW, epic1.getId());
        Subtask sub2 = new Subtask(0, "Подзадача 1.2", "", Status.NEW, epic1.getId());
        manager.createSubtask(sub1);
        manager.createSubtask(sub2);

        Epic epic2 = new Epic(0, "Эпик 2", "");
        manager.createEpic(epic2);
        Subtask sub3 = new Subtask(0, "Подзадача 2.1", "", Status.NEW, epic2.getId());
        manager.createSubtask(sub3);

        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.getTitle());
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.getTitle() + " | Статус: " + epic.getStatus());
        }

        System.out.println("Подзадачи:");
        for (Subtask sub : manager.getAllSubtasks()) {
            System.out.println(sub.getTitle() + " | Эпик: " + sub.getEpicId());
        }

        task1.setStatus(Status.DONE);
        manager.updateTask(task1);
        sub1.setStatus(Status.DONE);
        sub2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(sub1);
        manager.updateSubtask(sub2);
        sub3.setStatus(Status.DONE);
        manager.updateSubtask(sub3);

        System.out.println("Статусы эпиков после обновлений:");
        System.out.println(epic1.getTitle() + " | " + manager.getEpicById(epic1.getId()).getStatus());
        System.out.println(epic2.getTitle() + " | " + manager.getEpicById(epic2.getId()).getStatus());

        manager.deleteTaskById(task2.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("Оставшиеся задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task.getTitle());
        }

        System.out.println("Оставшиеся эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic.getTitle());
        }

        System.out.println("Оставшиеся подзадачи:");
        for (Subtask sub : manager.getAllSubtasks()) {
            System.out.println(sub.getTitle());
        }
    }
}
