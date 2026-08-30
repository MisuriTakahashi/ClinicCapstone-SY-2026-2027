/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

/**
 * Small reusable background executor for running database work off the
 * Swing Event Dispatch Thread (EDT).
 *
 * This intentionally does NOT pool JDBC connections — each DB call still
 * opens/closes its own H2 connection as before. This class only controls
 * WHICH THREAD the call happens on, so the UI never blocks while a query
 * or update runs.
 *
 * Usage:
 *
 *   DatabaseExecutor.run(
 *       () -> someService.loadAll(),      // runs on a background thread
 *       result -> table.setModel(...),    // runs back on the EDT
 *       ex -> JOptionPane.showMessageDialog(this, ex.getMessage())
 *   );
 */

public final class DatabaseExecutor {

    // 3 threads is enough to keep the UI responsive without letting an
    // embedded H2 database get hammered by unlimited concurrent writers.
    private static final int THREAD_POOL_SIZE = 3;

    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(
                    THREAD_POOL_SIZE,
                    DatabaseExecutor::newDaemonWorker
            );

    private static Thread newDaemonWorker(Runnable r) {
        Thread t = new Thread(r, "clinic-db-worker");
        t.setDaemon(true); // never block application shutdown
        return t;
    }

    private DatabaseExecutor() {
    }

    @FunctionalInterface
    public interface DbTask<T> {
        T run() throws Exception;
    }

    @FunctionalInterface
    public interface VoidDbTask {
        void run() throws Exception;
    }

    /**
     * Runs {@code task} on a background thread. On success, {@code onSuccess}
     * is invoked on the EDT with the result. On failure, {@code onError} is
     * invoked on the EDT with the exception (or the stack trace is printed
     * if {@code onError} is null).
     */
    public static <T> void run(
            DbTask<T> task,
            Consumer<T> onSuccess,
            Consumer<Exception> onError) {

        EXECUTOR.submit(() -> {
            try {
                T result = task.run();

                SwingUtilities.invokeLater(() -> {
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                });

            } catch (Exception ex) {

                SwingUtilities.invokeLater(() -> {
                    if (onError != null) {
                        onError.accept(ex);
                    } else {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

    /** Same as {@link #run}, for tasks that don't return a value. */
    public static void runVoid(
            VoidDbTask task,
            Runnable onSuccess,
            Consumer<Exception> onError) {

        run(
                () -> {
                    task.run();
                    return null;
                },
                ignored -> {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                },
                onError
        );
    }

    /** Call once, on application exit, so background threads stop cleanly. */
    public static void shutdown() {
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(3, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException ex) {
            EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
