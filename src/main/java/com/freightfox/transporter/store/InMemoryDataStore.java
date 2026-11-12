package com.freightfox.transporter.store;

import com.freightfox.transporter.model.Lane;
import com.freightfox.transporter.model.Transporter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread-safe in-memory store for trade lanes and transporter data.
 */
@Component
public class InMemoryDataStore {

    private final List<Lane> lanes = Collections.synchronizedList(new ArrayList<>());
    private final List<Transporter> transporters = Collections.synchronizedList(new ArrayList<>());

    public void saveData(List<Lane> lanes, List<Transporter> transporters) {
        this.lanes.clear();
        this.transporters.clear();
        this.lanes.addAll(lanes);
        this.transporters.addAll(transporters);
    }

    public List<Lane> getLanes() {
        return Collections.unmodifiableList(lanes);
    }

    public List<Transporter> getTransporters() {
        return Collections.unmodifiableList(transporters);
    }

    public boolean hasData() {
        return !lanes.isEmpty() && !transporters.isEmpty();
    }

    public void clear() {
        lanes.clear();
        transporters.clear();
    }
}
