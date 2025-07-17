package taskmanager;

import java.io.File;
import java.io.IOException;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    FileBackedTaskManager createManager() {
        try {
            File file = File.createTempFile("test", ".csv");
            return FileBackedTaskManager.loadFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл для теста", e);
        }
    }
}
