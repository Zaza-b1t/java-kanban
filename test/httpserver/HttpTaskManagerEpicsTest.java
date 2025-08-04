package httpserver;

import org.junit.jupiter.api.Test;
import task.Epic;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerEpicsTest extends BaseHttpTest {

    public HttpTaskManagerEpicsTest() {
    }

    @Test
    public void testAddEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(0, "Test 1", "Testing epics 2");

        String epicJson = gson.toJson(epic);

        assertEquals(HttpStatus.CREATED.getCode(), sendPost(EPICS_URI, epicJson).statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1", epicsFromManager.getFirst().getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void testGetAllEpics() throws Exception {
        Epic epic = new Epic(0, "Test 2", "Get Test");
        String epicJson = gson.toJson(epic);

        sendPost(EPICS_URI,epicJson);

        HttpResponse<String> getResponse = sendGet(EPICS_URI);

        assertEquals(HttpStatus.OK.getCode(), getResponse.statusCode());

        Epic[] epics = gson.fromJson(getResponse.body(), Epic[].class);
        assertEquals(1, epics.length);
        assertEquals("Test 2", epics[0].getTitle());
    }

    @Test
    public void testDeleteEpics() throws Exception {
        Epic epic = new Epic(0, "Test delete", "Desc");
        String epicJson = gson.toJson(epic);

        sendPost(EPICS_URI,epicJson);

        int id = manager.getAllEpics().getFirst().getId();

        assertEquals(HttpStatus.OK.getCode(), sendDelete(EPICS_URI,id).statusCode());
        assertEquals(0, manager.getAllEpics().size());
    }
}
