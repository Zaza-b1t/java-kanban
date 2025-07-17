package taskmanager;

import task.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager history;

    private final Comparator<Task> taskStartTimeComparator = new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            int cmp = t1.getStartTime().compareTo(t2.getStartTime());
            if (cmp != 0) {
                return cmp;
            }
            return Integer.compare(t1.getId(), t2.getId());
        }
    };

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(taskStartTimeComparator);

    public InMemoryTaskManager(HistoryManager history) {
        this.history = history;
    }

    private int generateId() {
        return idCounter++;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public Task createTask(Task task) {
        for (Task existing : prioritizedTasks) {
            if (isOverlap(existing, task)) {
                throw new RuntimeException("Задачи пересекаются по времени!");
            }
        }
        int id = generateId();
        task.setId(id);
        tasks.put(id, task);
        prioritizedTasks.add(task);
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
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        history.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            prioritizedTasks.remove(tasks.get(id));
            history.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void updateTask(Task task) {
        prioritizedTasks.remove(tasks.get(task.getId()));
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public Epic createEpic(Epic epic) {
        int id = generateId();
        epic.setId(id);
        epics.put(id, epic);
        updateEpicStatus(id);
        updateEpicTimeAndDuration(id);
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
                prioritizedTasks.remove(subtasks.get(subId));
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
                prioritizedTasks.remove(subtasks.get(subId));
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
        updateEpicTimeAndDuration(epic.getId());
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        for (Task existing : prioritizedTasks) {
            if (isOverlap(existing, subtask)) {
                throw new RuntimeException("Задачи пересекаются по времени!");
            }
        }
        int id = generateId();
        subtask.setId(id);
        subtasks.put(id, subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
            updateEpicTimeAndDuration(epic.getId());
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
        prioritizedTasks.remove(subtask);
        history.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTimeAndDuration(epic.getId());
            }
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            prioritizedTasks.remove(subtasks.get(id));
            history.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateEpicStatus(epic.getId());
            updateEpicTimeAndDuration(epic.getId());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        prioritizedTasks.remove(subtasks.get(subtask.getId()));
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);

        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            updateEpicStatus(epic.getId());
            updateEpicTimeAndDuration(epic.getId());
        }
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
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

        boolean allNew = subIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(sub -> sub.getStatus() == Status.NEW);

        boolean allDone = subIds.stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .allMatch(sub -> sub.getStatus() == Status.DONE);

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    private void updateEpicTimeAndDuration(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> subList = epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();

        if (subList.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subList) {
            LocalDateTime subStart = subtask.getStartTime();
            LocalDateTime subEnd = subtask.getEndTime();

            if (subStart != null) {
                if (earliestStart == null || subStart.isBefore(earliestStart)) {
                    earliestStart = subStart;
                }
            }
            if (subEnd != null) {
                if (latestEnd == null || subEnd.isAfter(latestEnd)) {
                    latestEnd = subEnd;
                }
            }
            if (subtask.getDuration() != null) {
                totalDuration = totalDuration.plus(subtask.getDuration());
            }
        }

        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
        epic.setDuration(totalDuration);
    }



    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    private boolean isOverlap(Task t1, Task t2) {
        if (t1.getStartTime() == null || t1.getDuration() == null ||
                t2.getStartTime() == null || t2.getDuration() == null) {
            return false;
        }
        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = t1.getEndTime();
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = t2.getEndTime();
        return start1.isBefore(end2) && start2.isBefore(end1);
    }


    private boolean hasOverlap(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(task -> task.getId() != newTask.getId() && isOverlap(task, newTask));
    }

}
