package ic2.util;

import ic2.IC2;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerTaskQueue {
    private static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue();

    public static void post(Runnable task) {
        ServerTaskQueue.TASKS.add(task);
    }

    public static void drain() {
        Runnable task;
        while ((task = ServerTaskQueue.TASKS.poll()) != null) {
            try {
                task.run();
            }
            catch (Throwable t) {
                IC2.LOGGER.error("server task failed", t);
            }
        }
    }
}
