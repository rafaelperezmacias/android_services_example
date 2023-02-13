package com.rafael_15300643.p4_servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TimeService extends Service{

    NotificationManager notificationManager;
    private static final int ID_NOTIFICATION = 585;
    private static final String ID_CHANNEL = "MiCanal1";
    Timer timer;
    String fecha;
    public static UpdateTimeServiceListener UPDATE_TIME_LISTENER = null;
    private static final long UPDATE_INTERVAL = 10000;

    public TimeService(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        timer = new Timer();
        startTime();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.cancel();
        }
        notificationManager.cancel(ID_NOTIFICATION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Servicio iniciado " + startId, Toast.LENGTH_SHORT).show();

        //Sets a pattern for vibration.
        long vibratePattern[] = {0, 200, 300, 100};
        //Builds a notification with some special features
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(), ID_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle("Servicios")
                .setContentText("Servicio de Hora Activo")
                .setColorized(true)
                .setVibrate(vibratePattern)
                .setTicker("Soy una notificación")
                .setColor(Color.argb(1, 255, 0, 0))
                .setLights(Color.argb(1, 255, 0, 0), 500, 100)
                .setWhen(System.currentTimeMillis());

        //Creates a notification channel used for Android Oreo or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nameChannel = "MiCanal1";
            NotificationChannel notificationChannel = new NotificationChannel(ID_CHANNEL,
                    nameChannel, NotificationManager.IMPORTANCE_HIGH);
            notification.setChannelId(ID_CHANNEL);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Launchs notification previously built.
        notificationManager.notify(ID_NOTIFICATION, notification.build());

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startTime(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                int s = c.get(Calendar.SECOND);
                int h = c.get(Calendar.HOUR);
                int m = c.get(Calendar.MINUTE);
                fecha = "Hora Actual: "+h + ":" + m + ":" +s;
                handler.sendEmptyMessage(0);
            }
        },0,UPDATE_INTERVAL);
    }

    public static void setUpdateTimeListener(UpdateTimeServiceListener listener){
        UPDATE_TIME_LISTENER = listener;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (UPDATE_TIME_LISTENER != null)
                UPDATE_TIME_LISTENER.updateTime(fecha);
        }
    };
}
