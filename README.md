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
- [ble.startScanWithOptions](#startscanwithoptions)



## startScanWithOptionsWithId

Metoda pozwala na ciągłe skanowanie urządzeń nawet przy wyłączonym wyświetlaczu. Wymagane jest podanie identyfikatora/ów producenta/ów.

    ble.startScanWithOptionsWithId([id], options, success, failure);

### Description

Funckja `startScanWithOptionsWithId` skanuje urządzenia bluetooth. Działa podobnie jak `startScan`, jednakże do swojego działania potrzebuje parametru "id" który przyjmuje postać liczby HEX np: 004D. W trakcie wywołania, plugin tworzy foreground service z widocznym powiadomieniem sygnalizującym pracę usługi. Po wywołaniu metody `stopScan`, usługa zostaje wyłączona.

    {
        "name": "TI SensorTag",
        "id": "BD922605-1B07-4D55-8D09-B66653E51BBA",
        "rssi": -79,
        "advertising": /* ArrayBuffer or map */
    }

### Parameters

- __ids__: Lista identyfikatorów producentów. Nie można zostawić tego pola na []!
- __options__: an object specifying a set of name-value pairs. The currently acceptable options are:
- _reportDuplicates_: true if duplicate devices should be reported, false (default) if devices should only be reported once. [optional]
- __success__: Success callback function that is invoked which each discovered device.
- __failure__: Error callback function, invoked when error occurs. [optional]

### Quick Example

    ble.startScanWithOptionsWithId(["00C7"],
        { reportDuplicates: true },
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
    
## startScanWithOptions

Metoda pozwala na ciągłe skanowanie urządzeń nawet przy wyłączonym wyświetlaczu. Wymagane jest podanie UUID URZĄDZENIA

    ble.startScanWithOptions([UUID], options, success, failure);

### Description

Funckja `startScanWithOptions` skanuje urządzenia bluetooth. Działa podobnie jak `startScan`, jednakże do swojego działania potrzebuje parametru "uuid" który przyjmuje postać xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx. W trakcie wywołania, plugin tworzy foreground service z widocznym powiadomieniem sygnalizującym pracę usługi. Po wywołaniu metody `stopScan`, usługa zostaje wyłączona.

    {
        "name": "TI SensorTag",
        "id": "BD922605-1B07-4D55-8D09-B66653E51BBA",
        "rssi": -79,
        "advertising": /* ArrayBuffer or map */
    }



### Parameters

- __UUID__: Lista uuid urządeń. Nie można zostawić tego pola na []!
- __options__: an object specifying a set of name-value pairs. The currently acceptable options are:
- _reportDuplicates_: true if duplicate devices should be reported, false (default) if devices should only be reported once. [optional]
- __success__: Success callback function that is invoked which each discovered device.
- __failure__: Error callback function, invoked when error occurs. [optional]

### Quick Example

    ble.startScanWithOptions(["0000fe6a-0000-1000-8000-00805f9b34fb"],
        { reportDuplicates: true },
        function(device) {
            console.log(JSON.stringify(device));
        },
        failure);

    setTimeout(ble.stopScan,
        5000,
        function() { console.log("Scan complete"); },
        function() { console.log("stopScan failed"); }
    );
