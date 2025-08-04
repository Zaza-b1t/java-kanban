package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW);
    }

    public List<Integer> getSubtaskIds() {
        if (subtaskIds == null) {
            subtaskIds = new ArrayList<>();
        }
        return subtaskIds;
    }


    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }

    public void removeSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}

