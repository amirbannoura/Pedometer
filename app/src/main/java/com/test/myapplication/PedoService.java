package com.test.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;

public class PedoService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private float x = 0, y = 0, z = 0, xOrientation,yOrientation,zOrientation;
    private float g2,giroX,giroY,giroZ,mX,mY,mZ;

    @Override
    public void onCreate() {
        //register sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager
                        .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager
                        .getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this, sensorManager
                        .getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    public PedoService() {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            /*
             * values[0]: Azimuth, angle between the magnetic north direction and
             * the Y axis, around the Z axis (0 to 359). 0=North, 90=East,
             * 180=South, 270=West
             *
             * values[1]: Pitch, rotation around X axis (-180 to 180), with positive
             * values when the z-axis moves toward the y-axis.
             *
             * values[2]: Roll, rotation around Y axis (-90 to 90), with positive
             * values when the x-axis moves away from the z-axis.
             */

            xOrientation = event.values[0];
            yOrientation = event.values[1];
            zOrientation = event.values[2];
        }

        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {


            /*
             * All values are in SI units (m/s^2) and measure the acceleration
             * applied to the phone minus the force of gravity. values[0]:
             * Acceleration minus Gx on the x-axis values[1]: Acceleration minus Gy
             * on the y-axis values[2]: Acceleration minus Gz on the z-axis
             */

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            //float g1 = (float) Math.sqrt(x * x + y * y);
            g2 = (float) Math.sqrt(x * x + y * y + z * z);

        }

        else if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE){

            /*
             * Gyroscope to calculate the the rotation around the axis rad/s
             * values[0] = rate of rotation around the x axis
             * values[1] = rate of rotation around the y axis
             * values[2] = rate of rotation around the z axis
             */

            giroX = event.values[0];
            giroY = event.values[1];
            giroZ = event.values[2];
        }

        else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){

            /*
             * Magnetic field All values are in micro-Tesla (uT) and
             * measure the ambient magnetic field in the X, Y and Z axis:
             * values[0] = Angular speed around the x-axis
             * values[1] = Angular speed around the y-axis
             * values[2] = Angular speed around the z-axis
             */

            mX = event.values[0];
            mY = event.values[1];
            mZ = event.values[2];
        }
        // http://developer.android.com/reference/android/hardware/SensorEvent.html
//		WriteFile();
    }

    public void onDestroy() {

        sensorManager.unregisterListener(this);
        sensorManager = null;


        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private final PedoInterface.Stub PedoServiceBinder = new PedoInterface.Stub() {


        public float getAccelometer() throws RemoteException {

            return g2;
        }

        public float getAccelX() throws RemoteException {
            return x;
        }

        public float getAccelY() throws RemoteException {
            return y;
        }

        public float getAccelZ() throws RemoteException {
            return z;
        }

        public float getXOrientation() throws RemoteException {

            return xOrientation;
        }

        public float getYOrientation() throws RemoteException {
            // TODO Auto-generated method stub
            return yOrientation;
        }

        public float getZOrientation() throws RemoteException {
            // TODO Auto-generated method stub
            return zOrientation;
        }

        public float getGiroX() throws RemoteException {
            // TODO Auto-generated method stub
            return giroX;
        }

        public float getGiroY() throws RemoteException {
            // TODO Auto-generated method stub
            return giroY;
        }

        public float getGiroZ() throws RemoteException {
            // TODO Auto-generated method stub
            return giroZ;
        }

        public float getMX() throws RemoteException {
            // TODO Auto-generated method stub
            return mX;
        }

        public float getMY() throws RemoteException {
            // TODO Auto-generated method stub
            return mY;
        }

        public float getMZ() throws RemoteException {
            // TODO Auto-generated method stub
            return mZ;
        }



    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return PedoServiceBinder;
    }
}