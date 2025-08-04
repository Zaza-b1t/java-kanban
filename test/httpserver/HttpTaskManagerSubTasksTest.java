package httpserver;

import org.junit.jupiter.api.Test;

import task.Epic;
import task.Status;
import task.Subtask;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerSubTasksTest extends BaseHttpTest{

    public HttpTaskManagerSubTasksTest() {
    }

    @Test
    public void testAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Epic for subtask", "Epic desc");
        String epicJson = gson.toJson(epic);

        sendPost(EPICS_URI,epicJson);
        int epicId = manager.getAllEpics().getFirst().getId();

        Subtask subtask = new Subtask(0, "Test 1", "Описание подзадачи 1.2",
                Status.NEW, epicId,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 1, 1, 12, 0));

        String subtaskJson = gson.toJson(subtask);

        assertEquals(HttpStatus.CREATED.getCode(), sendPost(SUBTASKS_URI,subtaskJson).statusCode());


        List<Subtask> subtasksFromManager = manager.getAllSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test 1", subtasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetAllSubTasks() throws Exception {
        Epic epic = new Epic(0, "Epic for subtask", "Epic desc");
        String epicJson = gson.toJson(epic);

        sendPost(EPICS_URI,epicJson);
        int epicId = manager.getAllEpics().getFirst().getId();

        Subtask subtask = new Subtask(0, "Test 2", "Описание подзадачи 1.3",
                Status.NEW, epicId,
                Duration.ofMinutes(15), LocalDateTime.of(2025, 2, 1, 12, 0));

        String subtaskJson = gson.toJson(subtask);
        sendPost(SUBTASKS_URI,subtaskJson);

        HttpResponse<String> getResponse = sendGet(SUBTASKS_URI);

        assertEquals(HttpStatus.OK.getCode(), getResponse.statusCode());

        Subtask[] subtasks = gson.fromJson(getResponse.body(), Subtask[].class);
        assertEquals(1, subtasks.length);
        assertEquals("Test 2", subtasks[0].getTitle());
    }

    @Test
    public void testDeleteSubTask() throws Exception {
        Epic epic = new Epic(0, "Epic for subtask", "Epic desc");
        String epicJson = gson.toJson(epic);

        sendPost(EPICS_URI,epicJson);
        int epicId = manager.getAllEpics().getFirst().getId();

        Subtask subtask = new Subtask(0, "Test delete", "Описание подзадачи 1.4",
                Status.NEW, epicId,
                Duration.ofMinutes(15), LocalDateTime.of(2025, 2, 1, 12, 0));

        String subtaskJson = gson.toJson(subtask);
        sendPost(SUBTASKS_URI,subtaskJson);

        int id = manager.getAllSubtasks().getFirst().getId();

        assertEquals(HttpStatus.OK.getCode(), sendDelete(SUBTASKS_URI,id).statusCode());
        assertEquals(0, manager.getAllSubtasks().size());
    }
}
