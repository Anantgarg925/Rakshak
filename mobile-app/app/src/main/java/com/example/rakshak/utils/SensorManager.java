package com.example.rakshak.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List; // Keep this import for the interface
import com.example.rakshak.model.SensorData; // Keep this import for the interface

public class SensorManager implements SensorEventListener {

    public interface OnSensorListener {
        void onAccidentDetected();
        // You can keep this if your UI still needs live data updates
        void onSensorDataUpdated(List<SensorData> sensorDataList);
    }

    private OnSensorListener onSensorListener;
    private static final String TAG = "SensorManager";

    // --- ML Model Configuration ---
    private static final String MODEL_FILE_NAME = "model.tflite";
    private static final int WINDOW_SIZE = 128; // Must match the window size used in training
    private static final int NUM_FEATURES = 6;  // acc_x, y, z, gyro_x, y, z
    private static final float PREDICTION_THRESHOLD = 0.95f; // Confidence threshold for triggering an alert

    private final android.hardware.SensorManager systemSensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private boolean isMonitoring = false;

    // --- TensorFlow Lite Interpreter ---
    private Interpreter tflite;

    // --- Data Buffering ---
    private final Deque<float[]> sensorDataQueue = new ArrayDeque<>(WINDOW_SIZE);
    private final float[] lastGyroValues = new float[3];

    public SensorManager(Context context) {
        this.systemSensorManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        initializeSensors();
        try {
            tflite = new Interpreter(loadModelFile(context));
        } catch (IOException e) {
            Log.e(TAG, "Error loading TensorFlow Lite model.", e);
        }
    }

    public void setOnSensorListener(OnSensorListener listener) {
        this.onSensorListener = listener;
    }

    private void initializeSensors() {
        if (systemSensorManager != null) {
            accelerometer = systemSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = systemSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }
    }

    public void startMonitoring() {
        if (isMonitoring || tflite == null) return;
        isMonitoring = true;
        sensorDataQueue.clear();
        systemSensorManager.registerListener(this, accelerometer, android.hardware.SensorManager.SENSOR_DELAY_GAME);
        systemSensorManager.registerListener(this, gyroscope, android.hardware.SensorManager.SENSOR_DELAY_GAME);
        Log.d(TAG, "ML-based sensor monitoring started");
    }

    public void stopMonitoring() {
        if (!isMonitoring) return;
        isMonitoring = false;
        systemSensorManager.unregisterListener(this);
        Log.d(TAG, "ML-based sensor monitoring stopped");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isMonitoring) return;

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            System.arraycopy(event.values, 0, lastGyroValues, 0, 3);
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] combinedReading = new float[NUM_FEATURES];
            System.arraycopy(event.values, 0, combinedReading, 0, 3);
            System.arraycopy(lastGyroValues, 0, combinedReading, 3, 3);

            sensorDataQueue.add(combinedReading);

            Log.d(TAG, "Data Point Added: " + Arrays.toString(combinedReading) + " | Queue size: " + sensorDataQueue.size());



            if (sensorDataQueue.size() > WINDOW_SIZE) {
                sensorDataQueue.removeFirst();
            }

            if (sensorDataQueue.size() == WINDOW_SIZE) {
                runInference();
            }
        }
    }

    private void runInference() {
        if (tflite == null) return;

        float[][][] input = new float[1][WINDOW_SIZE][NUM_FEATURES];
        int i = 0;
        for (float[] data : sensorDataQueue) {
            input[0][i++] = data;
        }

        float[][] output = new float[1][1];
        tflite.run(input, output);

        float prediction = output[0][0];

        if (prediction > PREDICTION_THRESHOLD) {
            Log.e(TAG, "ACCIDENT DETECTED! Model confidence: " + prediction);
            if (onSensorListener != null) {
                onSensorListener.onAccidentDetected();
            }
            stopMonitoring(); // Stop to prevent multiple alerts
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE_NAME);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }
}