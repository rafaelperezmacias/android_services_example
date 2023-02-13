package com.rafael_15300643.p4_servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class FlashService extends Service {

    NotificationManager notificationManager;
    Vibrator vibrator;
    CameraManager cameraManager;
    Camera.Parameters parameters;
    Camera camera;

    String mIdCamera;
    private static final int ID_NOTIFICATION = 583;
    private static final String ID_CHANNEL = "MiCanal1";

    public FlashService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializes an instance for notification manager getting the service from system.
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT <= 23) {
            if (Camera.getNumberOfCameras() > 0) {
                camera = Camera.open(0);
                parameters = camera.getParameters();
            } else {
                Toast.makeText(this, "Tu dispositivo no tiene camara",
                        Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        } else {
            try {
                cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                if (cameraManager.getCameraIdList().length > 0) {
                    Log.d("Service", cameraManager.getCameraIdList().toString());
                    mIdCamera = cameraManager.getCameraIdList()[0];
                } else {
                    Toast.makeText(this, "Tu dispositivo no tiene camara",
                            Toast.LENGTH_SHORT).show();
                    stopSelf();
                }
            } catch (CameraAccessException e) {
                Log.e(this.getClass().getSimpleName(), "onCreate: " + e.getMessage());
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Servicio iniciado " + startId, Toast.LENGTH_SHORT).show();

        //Sets a pattern for vibration.
        long vibratePattern[] = {0, 200, 300, 100};
        vibrator.vibrate(vibratePattern, -1);
        //Builds a notification with some special features
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(), ID_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Servicios")
                .setContentText("Servicio de Flash Activo")
                .setColorized(true)
                .setTicker("Soy una notificación")
                .setColor(Color.argb(1, 255, 0, 0))
                .setLights(Color.argb(1, 255, 0, 0), 500, 100)
                .setWhen(System.currentTimeMillis());

        //Creates a notification channel used for Android Oreo or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nameChannel = "MiCanal1";
            NotificationChannel notificationChannel = new NotificationChannel(ID_CHANNEL, nameChannel, NotificationManager.IMPORTANCE_HIGH);
            notification.setChannelId(ID_CHANNEL);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Launchs notification previously built.
        notificationManager.notify(ID_NOTIFICATION, notification.build());

        if (Build.VERSION.SDK_INT <= 23) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.startPreview();
        } else {
            try {
                cameraManager.setTorchMode(mIdCamera, true);
            } catch (CameraAccessException e) {
                Log.e(this.getClass().getSimpleName(), "onCreate: " + e.getMessage());
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Servicio detenido", Toast.LENGTH_SHORT).show();
        notificationManager.cancel(ID_NOTIFICATION);
        if (Build.VERSION.SDK_INT <= 23) {
            camera.stopPreview();
            camera.release();
        } else {
            try {
                cameraManager.setTorchMode(mIdCamera, false);
                Log.d(this.getClass().getSimpleName(), "onDestroy: ");

            } catch (CameraAccessException e) {
                Log.e(this.getClass().getSimpleName(), "onCreate: " + e.getMessage());
            }
        }
    }
}
