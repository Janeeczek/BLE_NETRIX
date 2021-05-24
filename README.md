# Bluetooth Low Energy (BLE) Central Plugin for Apache Cordova EDITED

#INSTALACJA
1. instalacja plugman z npm: npm install -g plugman
2. pobranie repozytorium.
3. rozpakowanie go.
4. instalacja pluginu komendą : plugman install --platform android --project <scieżka do folderu \platoforms\android w projekcie do którego ma być wgrany plugin> --plugin <ścieżka do folderu z pluginem>
5. 
    Przykład : plugman install --platform android --project C:\Users\Jan.Mazurek\CordovaProjects\test\platforms\android --plugin        C:\Users\Jan.Mazurek\plugins\BLE_NETRIX


# API

## Methods

- [ble.startScanWithOptionsWithId](#startscanwithoptionswithid)
- [ble.stopScan](#stopscan)




## startScanWithOptionsWithId

Metoda pozwala na ciągłe skanowanie urządzeń nawet przy wyłączonym wyświetlaczu. Wymagane jest podanie identyfikatora/ów producenta/ów.

    ble.startScanWithOptionsWithId(manufactureId, options, success, failure);

### Description

Function `startScanWithOptionsWithId` scans for BLE devices. It operates similarly to the `startScan` function, but allows you to specify extra options (like allowing duplicate device reports).  The success callback is called each time a peripheral is discovered. Scanning will continue until `stopScan` is called.

    {
        "name": "TI SensorTag",
        "id": "BD922605-1B07-4D55-8D09-B66653E51BBA",
        "rssi": -79,
        "advertising": /* ArrayBuffer or map */
    }

Advertising information format varies depending on your platform. See [Advertising Data](#advertising-data) for more information.

See the [location permission notes](#location-permission-notes) above for information about Location Services in Android SDK >= 23.

### Parameters

- __ids__: Lista identyfikatorów producentów. Nie można zostawić tego pola na []!
- __options__: an object specifying a set of name-value pairs. The currently acceptable options are:
- _reportDuplicates_: true if duplicate devices should be reported, false (default) if devices should only be reported once. [optional]
- __success__: Success callback function that is invoked which each discovered device.
- __failure__: Error callback function, invoked when error occurs. [optional]

### Quick Example

    ble.startScanWithOptionsWithId("00C7",
        { reportDuplicates: true }
        function(device) {
            console.log(JSON.stringify(device));
        },
        failure);

    setTimeout(ble.stopScan,
        5000,
        function() { console.log("Scan complete"); },
        function() { console.log("stopScan failed"); }
    );


## stopScan

Wyłącza skanowanie.

    ble.stopScan(success, failure);

### Description

Function `stopScan` stops scanning for BLE devices.

### Parameters

- __success__: Success callback function, invoked when scanning is stopped. [optional]
- __failure__: Error callback function, invoked when error occurs. [optional]

### Quick Example

    ble.startScan([], function(device) {
        console.log(JSON.stringify(device));
    }, failure);

    setTimeout(ble.stopScan,
        5000,
        function() { console.log("Scan complete"); },
        function() { console.log("stopScan failed"); }
    );

    /* Alternate syntax
    setTimeout(function() {
        ble.stopScan(
            function() { console.log("Scan complete"); },
            function() { console.log("stopScan failed"); }
        );
    }, 5000);
    */
