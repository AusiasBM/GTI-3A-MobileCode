package com.proyecto3ausiasbm.mobilecode.controlador;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;

import com.proyecto3ausiasbm.mobilecode.modelo.Medicion;
import com.proyecto3ausiasbm.mobilecode.modelo.PeticionarioREST;
import com.proyecto3ausiasbm.mobilecode.modelo.TramaIBeacon;
import com.proyecto3ausiasbm.mobilecode.modelo.Utilidades;
import com.proyecto3ausiasbm.mobilecode.vista.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class BluetoothBTLE {

    // Variables
    private static final String ETIQUETA_LOG = ">>>>";
    private BluetoothLeScanner elEscanner;
    private ScanCallback callbackDelEscaneo = null;
    public List<Medicion> mediciones;

    public BluetoothBTLE(BluetoothLeScanner elEscanner) {
        this.elEscanner = elEscanner;
    }

    // Métodos
    public void buscarTodosLosDispositivosBTLE() {

        this.detenerBusquedaDispositivosBTLE();
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                mostrarInformacionDispositivoBTLE( resultado );
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        this.elEscanner.startScan( this.callbackDelEscaneo);

    } // ()

    // --> "AusiasBM-GTI"
    public void buscarEsteDispositivoBTLE(final String dispositivoBuscado ) {

        this.detenerBusquedaDispositivosBTLE();
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");
                mostrarInformacionDispositivoBTLE( resultado );
                anyadirBeacon( dispositivoBuscado, resultado );
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {

                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };



        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado );
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
        //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        ScanFilter scanFilter = new ScanFilter.Builder().setDeviceName(dispositivoBuscado).build();
        ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();
        filters.add(scanFilter);
        ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();

        this.elEscanner.startScan(filters, settings, this.callbackDelEscaneo );
    } // ()

    public void detenerBusquedaDispositivosBTLE() {

        if ( this.callbackDelEscaneo == null ) {
            return;
        }

        this.elEscanner.stopScan( this.callbackDelEscaneo );
        this.callbackDelEscaneo = null;

    } // ()

    private void mostrarInformacionDispositivoBTLE( ScanResult resultado ) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi );

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

        if(Utilidades.bytesToInt(tib.getMajor()) == 11){
            MainActivity.textCo2.setText(Utilidades.bytesToInt(tib.getMinor()) + "");
        }else{
            MainActivity.textTemp.setText(Utilidades.bytesToInt(tib.getMinor()) + "");
        }

    } // ()

    private void anyadirBeacon(final String dispositivoBuscado, ScanResult resultado) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();
        byte[] bytes = resultado.getScanRecord().getBytes();
        TramaIBeacon tib = new TramaIBeacon(bytes);

        if ((bluetoothDevice.getName() + "").equals(dispositivoBuscado)) {
            Log.d("Dispositivo encontrado correcto -->", dispositivoBuscado);
            Log.d("Dispositivo encontrado correcto -->", "Preparamos el objeto");

            //Utilidades.bytesToInt(tib.getMajor())
            //Utilidades.bytesToInt(tib.getMinor())

            mediciones.add(
                    new Medicion(
                            Utilidades.bytesToInt(tib.getMinor()),
                            Utilidades.bytesToInt(tib.getMajor()),
                            29.99600901262704,
                            -5.16582290057630056
                    )
            );

            Log.d("Dispositivo encontrado correcto -->", "Lo añadimos al array de 50");
            if (mediciones.size() > 50) {
                try {
                    Log.d("Dispositivo encontrado correcto -->", "Enviamos las 50 mediciones");
                    // Enviar por POST
                    for (Medicion med : mediciones) {
                        enviarPeticionRest(
                                "POST",
                                med.toString(),
                                "http://192.168.1.42:3500/api/anyadir-medicion"
                        );
                    }
                    mediciones.clear();
                    Log.d("Dispositivo encontrado correcto -->", "Medidas enviadas correctamente");
                } catch (Exception error) {
                    Log.d("Dispositivo encontrado correcto -->", "Error al enviar las medidas");
                }
            }

        }
    }

    // Peticiones REST

    // "http://192.168.1.34:3500/api/todas-las-mediciones"
    private void enviarPeticionRest(String tipo, String body, String ruta) {
        Log.d("clienterestandroid", "Entramos en petición REST");

        // ojo: creo que hay que crear uno nuevo cada vez
        PeticionarioREST elPeticionario = new PeticionarioREST();
        elPeticionario.hacerPeticionREST(tipo,  ruta,
                body,
                new PeticionarioREST.RespuestaREST () {
                    @Override
                    public void callback(int codigo, String cuerpo) {
                        Log.d(ETIQUETA_LOG, "codigo respuesta= " + codigo + " <-> \n" + cuerpo);
                    }
                }
        );

    }

}
