package com.freightfox.transporter.store;

import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.Transporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Concurrency and unit tests for InMemoryDataStore.
 */
class InMemoryDataStoreTest {

    private InMemoryDataStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryDataStore();
    }

    @Test
    @DisplayName("Initial store is empty")
    void initialState_isEmpty() {
        assertFalse(store.hasData());
        assertTrue(store.getLanes().isEmpty());
        assertTrue(store.getTransporters().isEmpty());
    }

    @Test
    @DisplayName("saveData: stores lanes and transporters atomically")
    void saveData_success() {
        List<Lane> lanes = List.of(new Lane(1, "Mumbai", "Delhi"));
        List<Transporter> transporters = List.of(new Transporter(1, "T1", List.of()));

        store.saveData(lanes, transporters);

        assertTrue(store.hasData());
        assertEquals(1, store.getLanes().size());
        assertEquals(1, store.getTransporters().size());
    }

    @Test
    @DisplayName("Concurrent reads and writes do not throw ConcurrentModificationException")
    void concurrentReadWrite_threadSafe() throws InterruptedException, ExecutionException {
        int threads = 10;
        int operationsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            futures.add(executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    if (threadId % 2 == 0) {
                        // Writer thread
                        store.saveData(
                                List.of(new Lane(j, "Origin" + j, "Dest" + j)),
                                List.of(new Transporter(j, "Trans" + j, List.of()))
                        );
                    } else {
                        // Reader thread - iterating over returned lists
                        List<Lane> lanes = store.getLanes();
                        for (Lane lane : lanes) {
                            assertNotNull(lane.getOrigin());
                        }
                        store.hasData();
                    }
                }
            }));
        }

        for (Future<?> f : futures) {
            f.get(); // ensure no exception was thrown
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
}

