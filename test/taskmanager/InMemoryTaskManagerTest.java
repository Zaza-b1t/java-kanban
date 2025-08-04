package taskmanager;

import managers.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}
