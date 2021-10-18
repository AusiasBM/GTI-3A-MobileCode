package com.proyecto3ausiasbm.mobilecode.vista;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.proyecto3ausiasbm.mobilecode.R;
import com.proyecto3ausiasbm.mobilecode.controlador.BluetoothBTLE;
import com.proyecto3ausiasbm.mobilecode.controlador.ServicioEscuharBeacons;

/**
 * Esa clase es la principal de toda la app y es la que esta vinculada con la vista principal.
 * @author ausiasbm
 * @version 1.0
 * 3º GTI - Proyecto
 */

public class MainActivity extends AppCompatActivity {

    // Variables estaticas
    private static final String ETIQUETA_LOG = ">>>>";
    private static final int CODIGO_PETICION_PERMISOS = 11223344;
    // Variables
    private BluetoothLeScanner elEscanner;
    private Intent elIntentDelServicio = null;
    public static BluetoothBTLE bluetoothBTLE;
    public static TextView textCo2, textTemp;


    /**
     *
     * Esta método es el principal de toda la app.
     * @param: A este método nosotros no le pasamos nada
     * @see: Este método llama a inicializarBluetooth()
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        textCo2 = findViewById(R.id.textViewCo2);
        textTemp = findViewById(R.id.textViewTemp);

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");

    } // onCreate()

    // Inicializa el Bluetooth
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitamos adaptador BT ");

        bta.enable();

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): habilitado =  " + bta.isEnabled() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): estado =  " + bta.getState() );

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        if ( this.elEscanner == null ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): Socorro: NO hemos obtenido escaner btle  !!!!");

        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): voy a perdir permisos (si no los tuviera) !!!!");

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        )
        {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION},
                    CODIGO_PETICION_PERMISOS);
        }
        else {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");

        }
        bluetoothBTLE = new BluetoothBTLE(elEscanner);
    } // ()

    // Pide permisos si no los tiene
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {

                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()

    // Botones para Arrancar y parar el SERVICIO -------------

    public void botonArrancarServicio(View v ) {
        Log.d(ETIQUETA_LOG, " boton arrancar servicio Pulsado" );

        if ( this.elIntentDelServicio != null ) {
            // ya estaba arrancado
            return;
        }

        Toast.makeText(this, "Detección del sensor Activado", Toast.LENGTH_LONG).show();

        Log.d(ETIQUETA_LOG, " MainActivity.constructor : voy a arrancar el servicio");

        this.elIntentDelServicio = new Intent(this, ServicioEscuharBeacons.class);

        this.elIntentDelServicio.putExtra("tiempoDeEspera", (long) 5000);
        startService( this.elIntentDelServicio );

    } // ()

    public void botonDetenerServicio(View v ) {

        if ( this.elIntentDelServicio == null ) {
            // no estaba arrancado
            return;
        }

        Toast.makeText(this, "Detección del sensor Desactivado", Toast.LENGTH_LONG).show();

        stopService( this.elIntentDelServicio );

        this.elIntentDelServicio = null;

        Log.d(ETIQUETA_LOG, " boton detener servicio Pulsado" );

        textTemp.setText("--");
        textCo2.setText("--");
    } // ()

} // class
