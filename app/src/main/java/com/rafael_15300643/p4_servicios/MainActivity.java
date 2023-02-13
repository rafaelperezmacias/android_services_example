package com.rafael_15300643.p4_servicios;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements UpdateCounterServiceListener, UpdateTimeServiceListener {

    Intent flashServiceIntent;
    Intent counterServiceIntent;
    Intent timeServiceIntent;
    private Button btnFlash;
    private Button btnConteo;
    private TextView txtConteo;
    private Button btnHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFlash = (Button) findViewById(R.id.btnFlash);
        btnConteo = (Button) findViewById(R.id.btnConteo);
        txtConteo = (TextView) findViewById(R.id.txtConteo);
        btnHora = (Button) findViewById(R.id.btnHora);

        CounterService.setUpdateCounterListener(MainActivity.this);
        TimeService.setUpdateTimeListener(MainActivity.this);

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    launchFlashService();
                }
            }
        });

        btnConteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCounterService();
            }
        });

        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTimeService();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stopService(serviceIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                AlertDialog.Builder cameraDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Permisos de cámara")
                                .setMessage("Esta App necesita usar la cámara solo para encender y apagar el flash")
                                .setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkCameraPermission();
                                    }
                                });
                cameraDialog.show();
            }
        }
    }

    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        20);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    private void launchFlashService() {
        flashServiceIntent = new Intent(MainActivity.this, FlashService.class);
        if (btnFlash.getText().equals("Encender")) {
            startService(flashServiceIntent);
            btnFlash.setText("Apagar");
        } else {
            stopService(flashServiceIntent);
            btnFlash.setText("Encender");
        }
    }

    private void launchCounterService(){
        counterServiceIntent = new Intent(MainActivity.this, CounterService.class);
        if(btnConteo.getText().equals("Comenzar")){
            startService(counterServiceIntent);
            btnConteo.setText("Detener");
        } else {
            stopService(counterServiceIntent);
            btnConteo.setText("Comenzar");
        }
    }

    private void launchTimeService(){
        timeServiceIntent = new Intent(MainActivity.this,TimeService.class);
        if (btnHora.getText().equals("Mostrar")){
            startService(timeServiceIntent);
            btnHora.setText("Detener");
        }else {
            stopService(timeServiceIntent);
            btnHora.setText("Mostrar");
        }
    }
    @Override
        public void updateCounter(int counter) {
        txtConteo.setText("CONTEO: " + counter);
    }

    @Override
    public void updateTime(String time) {
        Toast.makeText(this,time,Toast.LENGTH_LONG).show();
        //Snackbar.make(,Hora,Snackbar.LENGTH_LONG).show();
    }
}
