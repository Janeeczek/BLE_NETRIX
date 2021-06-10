package com.megster.cordova.ble.central;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;

import androidx.core.app.NotificationCompat;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ForegroundService extends Service {

    private final IBinder binder = new LocalBinder();
    private NotificationManager mNotificationManager;
    public static final String IS_ON_UUID = "is_on_uuid";
    public boolean is_on_uuid;
    private Notification notification;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String TAG = "BLEPlugin";
    public UUID[] serviceUUIDs;
    public String[] manufactureIds;
    public int scanSeconds;
    public BluetoothLeScanner bluetoothLeScanner;
    public CallbackContext discoverCallback;
    public Map<String, Peripheral> peripherals = new LinkedHashMap<String, Peripheral>();
    public ScanCallback leScanCallback;
    public boolean bounded = false;
    public class LocalBinder extends Binder {
        ForegroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ForegroundService.this;
        }
    }



    // scan options
    boolean reportDuplicates = false;
    @Override
    public IBinder onBind(Intent intent) {
        LOG.d(TAG, "onBind");
        bounded = true;
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        stopScanning();
        return true;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Usługa się uruchamia", Toast.LENGTH_SHORT).show();
        //is_on_uuid = intent.getBooleanExtra(IS_ON_UUID,true);
        LOG.d(TAG, "onStartCommand");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, ForegroundService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Aktywna usługa Foreground")
                .setContentText("test")
                .setSmallIcon(getApplicationInfo().icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);






        return START_STICKY;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private Notification buildForegroundNotification(String input, PendingIntent pendingIntent) {
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Aktywne pobieranie lokalizacji w tle")
                .setContentText(input)
                .setSmallIcon(getApplicationInfo().icon)/////////////////////////////////////////////////////////TUTAJ MOZNA ZMIENIC IKONE
                .setContentIntent(pendingIntent)
                .setSound(null)
                .build();
        return notification;
    }
    public void startScanning(){
        if(is_on_uuid) {
            if (serviceUUIDs != null && serviceUUIDs.length > 0) {
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                for (UUID uuid : serviceUUIDs) {
                    //ScanFilter filter = new ScanFilter.Builder().setServiceUuid(new ParcelUuid(uuid)).build();
                    ScanFilter filter = new ScanFilter.Builder().setServiceData(new ParcelUuid(uuid),new byte[]{}).build();
                    filters.add(filter);
                }
                ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                bluetoothLeScanner.startScan(filters, settings, leScanCallback);
            } else {
                bluetoothLeScanner.startScan(leScanCallback);
            }

            if (scanSeconds > 0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LOG.d(TAG, "Stopping Scan");
                        bluetoothLeScanner.stopScan(leScanCallback);
                    }
                }, scanSeconds * 1000);
            }
        }else {
            if (manufactureIds != null && manufactureIds.length > 0) {
                List<ScanFilter> filters = new ArrayList<ScanFilter>();
                for (String manufactureId : manufactureIds) {

                    ScanFilter filter = new ScanFilter.Builder().setManufacturerData(Integer.parseInt( manufactureId, 16 ), new byte[] {}).build();
                    filters.add(filter);
                }
                ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
                bluetoothLeScanner.startScan(filters, settings, leScanCallback);
                LOG.d(TAG, "manufactureIds");
            } else {
                LOG.d(TAG, "bez");
                scanSeconds = 1;
            }
            LOG.d(TAG, "przed if");
            if (scanSeconds > 0) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LOG.d(TAG, "Stopping Scan");
                        bluetoothLeScanner.stopScan(leScanCallback);
                    }
                }, scanSeconds * 1000);
            }
        }


    }
    public void stopScanning(){
        stopForeground(true);
        stopSelf();
    }

    private ScanCallback leScanCallback2 = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            LOG.w(TAG, "Scan Result1");
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String address = device.getAddress();

            boolean alreadyReported = peripherals.containsKey(address) && !peripherals.get(address).isUnscanned();

            if (!alreadyReported) {

                Peripheral peripheral = new Peripheral(device, result.getRssi(), result.getScanRecord().getBytes());
                peripherals.put(device.getAddress(), peripheral);

                if (discoverCallback != null) {
                    //Toast.makeText(cordova.getContext(),"Mam :" + peripheral.toString(), Toast.LENGTH_LONG).show();
                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, peripheral.asJSONObject());
                    pluginResult.setKeepCallback(true);
                    discoverCallback.sendPluginResult(pluginResult);
                }

            } else {
                Peripheral peripheral = peripherals.get(address);
                if (peripheral != null) {
                    peripheral.update(result.getRssi(), result.getScanRecord().getBytes());
                    if (reportDuplicates && discoverCallback != null) {
                        //Toast.makeText(cordova.getContext(),"Mam :" + peripheral.toString(), Toast.LENGTH_LONG).show();
                        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, peripheral.asJSONObject());
                        pluginResult.setKeepCallback(true);
                        discoverCallback.sendPluginResult(pluginResult);
                    }
                }
            }
        }
    };



}