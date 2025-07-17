package taskmanager;

import java.io.File;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    FileBackedTaskManager createManager() {
        return FileBackedTaskManager.loadFromFile(new File("test.csv"));
    }
}
