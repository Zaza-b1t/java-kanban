package taskmanager;

import task.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager history;

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
    }

    private int generateId() {
        return idCounter++;
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public Task createTask(Task task) {
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            history.add(task);
        }
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            history.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        return epic;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            history.add(epic);
        }
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
                history.remove(subId);
            }
        }
        history.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Integer subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
                history.remove(subId);
            }
            history.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic.getId());
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(id);
            updateEpicStatus(epic.getId());
        }
        return subtask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            history.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        history.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            history.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> result = new ArrayList<>();
        if (epic != null) {
            for (Integer subId : epic.getSubtaskIds()) {
                Subtask sub = subtasks.get(subId);
                if (sub != null) {
                    result.add(sub);
                }
            }
        }
        return result;
    }

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Integer> subIds = epic.getSubtaskIds();
        if (subIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Set<Status> statuses = new HashSet<>();
        for (Integer subId : subIds) {
            Subtask sub = subtasks.get(subId);
            if (sub != null) {
                statuses.add(sub.getStatus());
            }
        }

        if (statuses.size() == 1) {
            epic.setStatus(statuses.iterator().next());
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
