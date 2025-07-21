package taskmanager;

import java.io.File;
import java.io.IOException;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    FileBackedTaskManager createManager() throws IOException {
        File file = File.createTempFile("test", ".csv");
        return FileBackedTaskManager.loadFromFile(file);
    }
}
