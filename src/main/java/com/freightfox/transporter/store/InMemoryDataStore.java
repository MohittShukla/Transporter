package com.freightfox.transporter.store;

import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.Transporter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe in-memory store for trade lanes and transporter data.
 * Uses atomic reference swapping over immutable snapshots to guarantee thread-safe
 * read/write operations and eliminate compound-operation race conditions.
 */
@Component
public class InMemoryDataStore {

    public record DataSnapshot(List<Lane> lanes, List<Transporter> transporters) {}

    private final AtomicReference<DataSnapshot> snapshot = new AtomicReference<>(
            new DataSnapshot(List.of(), List.of())
    );

    public void saveData(List<Lane> lanes, List<Transporter> transporters) {
        this.snapshot.set(new DataSnapshot(
                lanes != null ? List.copyOf(lanes) : List.of(),
                transporters != null ? List.copyOf(transporters) : List.of()
        ));
    }

    public List<Lane> getLanes() {
        return snapshot.get().lanes();
    }

    public List<Transporter> getTransporters() {
        return snapshot.get().transporters();
    }

    public boolean hasData() {
        DataSnapshot current = snapshot.get();
        return !current.lanes().isEmpty() && !current.transporters().isEmpty();
    }

    public void clear() {
        snapshot.set(new DataSnapshot(List.of(), List.of()));
    }
}
