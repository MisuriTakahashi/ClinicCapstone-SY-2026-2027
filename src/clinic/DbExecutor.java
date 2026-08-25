/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;

/**
 *
 * @author PC
 */

/**
 * Runs database work off the Swing EDT, one task at a time, on a single
 * background thread. A single thread is used deliberately: DatabaseManager
 * hands out one shared Connection, and JDBC Connections are not safe for
 * concurrent use from multiple threads. Funneling every DB call through one
 * worker thread means the shared connection is never touched by two threads
 * at once — no connection pool, no synchronized blocks needed elsewhere.
 */

public class DbExecutor {

    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "db-worker");
        t.setDaemon(true);
        return t;
    });

    public interface DbTask<T> {
        T run() throws Exception;
    }

    public interface OnSuccess<T> {
        void accept(T result);
    }

    public interface OnError {
        void accept(Exception ex);
    }

    /**
     * Runs task on the background DB thread, then delivers the result (or
     * error) back on the Swing EDT.
     */
    public static <T> void run(DbTask<T> task, OnSuccess<T> onSuccess, OnError onError) {
        WORKER.submit(() -> {
            try {
                T result = task.run();
                SwingUtilities.invokeLater(() -> onSuccess.accept(result));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> onError.accept(ex));
            }
        });
    }

    /** Call once, on application exit, to stop the worker thread cleanly. */
    public static void shutdown() {
        WORKER.shutdown();
    }
}
