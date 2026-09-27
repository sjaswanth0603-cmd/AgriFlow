package com.agriflow.m6_parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reservoir Sampling (Algorithm R) for Continuous Farm Sensor Streams
 * 
 * Agricultural Use Case:
 * A modern smart farm generates millions of continuous IoT sensor observations
 * (soil moisture, sap flow, solar radiation) every minute.
 * Reservoir Sampling extracts an unbiased, uniform random sample of size K
 * from an unbounded stream in a single pass without storing the full history.
 * 
 * Invariant: After N items, each item has an identical K / N probability of inclusion.
 * Time Complexity: O(N) single-pass stream processing
 * Space Complexity: O(K) memory storage
 */
public class ReservoirSampler<T> {

    private final int sampleSizeK;
    private final List<T> reservoir;
    private int totalProcessed;
    private final Random rng;

    public ReservoirSampler(int sampleSizeK) {
        this.sampleSizeK = sampleSizeK;
        this.reservoir = new ArrayList<>(sampleSizeK);
        this.totalProcessed = 0;
        this.rng = new Random();
    }

    /**
     * Ingests the next sensor observation from the real-time telemetry stream.
     */
    public synchronized void ingest(T sensorData) {
        totalProcessed++;

        if (reservoir.size() < sampleSizeK) {
            // First K items go directly into the reservoir
            reservoir.add(sensorData);
        } else {
            // For items past K, pick a random index between 0 and totalProcessed - 1
            int j = rng.nextInt(totalProcessed);
            if (j < sampleSizeK) {
                reservoir.set(j, sensorData);
            }
        }
    }

    /**
     * Returns a copy of the current representative sample.
     */
    public synchronized List<T> getSample() {
        return new ArrayList<>(reservoir);
    }

    public synchronized int getTotalProcessed() {
        return totalProcessed;
    }

    public static class SensorReading {
        public final long timestamp;
        public final String sensorId;
        public final double moisturePercent;
        public final double temperatureCelsius;

        public SensorReading(long timestamp, String sensorId, double moisturePercent, double temperatureCelsius) {
            this.timestamp = timestamp;
            this.sensorId = sensorId;
            this.moisturePercent = moisturePercent;
            this.temperatureCelsius = temperatureCelsius;
        }

        @Override
        public String toString() {
            return String.format("[%s @ %dms] Soil: %.1f%%, Temp: %.1f°C",
                    sensorId, timestamp, moisturePercent, temperatureCelsius);
        }
    }

    public static void main(String[] args) {
        int k = 5; // Reservoir capacity
        ReservoirSampler<SensorReading> sampler = new ReservoirSampler<>(k);

        // Simulate a stream of 1,000 sensor observations
        Random rand = new Random(42);
        for (int i = 1; i <= 1000; i++) {
            sampler.ingest(new SensorReading(
                System.currentTimeMillis() + i * 1000L,
                "SENSOR-" + (i % 20 + 1),
                20.0 + rand.nextDouble() * 30.0,
                18.0 + rand.nextDouble() * 15.0
            ));
        }

        System.out.println("Total IoT telemetry observations ingested: " + sampler.getTotalProcessed());
        System.out.println("Uniform Random Reservoir Sample of size " + k + ":");
        for (SensorReading r : sampler.getSample()) {
            System.out.println("  * " + r);
        }
    }
}
