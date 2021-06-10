const fs = require('fs');

const PLUGIN_NAME = "cordova-plugin-ble-central";
const enabledAndroidX = "android.useAndroidX=true";
const disabledAndroidX = "android.useAndroidX=false";
const enabledJetifier = "android.enableJetifier=true";
const disabledJetifier = "android.enableJetifier=false";
const gradlePropertiesPath = "./platforms/android/gradle.properties";

function log(message) {
    console.log(PLUGIN_NAME + ": " + message);
}

function onError(error) {
    log("ERROR: " + error);
}
String.prototype.deleteWord = function (searchTerm) {
    var str = this;
    var n = str.search(searchTerm);
    while (str.search(searchTerm) > -1) {
        n = str.search(searchTerm);
        str = str.substring(0, n) + str.substring(n + searchTerm.length, str.length);
    }
    return str;
}
function run() {
    let gradleProperties = fs.readFileSync(gradlePropertiesPath);

    if (gradleProperties) {
        let updatedGradleProperties = false;
        gradleProperties = gradleProperties.toString();
        log(gradleProperties);
        if (!gradleProperties.match(enabledAndroidX)) {

			gradleProperties = gradleProperties.replace(disabledAndroidX, enabledAndroidX);
			//gradleProperties.split("android.useAndroidX").join("");
			//gradleProperties += "\n" + enabledAndroidX;
            updatedGradleProperties = true;
        }
        if (!gradleProperties.match(enabledJetifier)) {
            gradleProperties = gradleProperties.replace(disabledJetifier, enabledJetifier);
            updatedGradleProperties = true;
        }
        if (updatedGradleProperties) {
            fs.writeFileSync(gradlePropertiesPath, gradleProperties, 'utf8');
            log("Updated gradle.properties to enable AndroidX");
        }
        log(gradleProperties);
    } else {
        log("gradle.properties file not found!")
    }
}

module.exports = function () {
    return new Promise((resolve, reject) => {
        try {
            run();
            resolve();
        } catch (e) {
            onError("EXCEPTION: " + e.toString());
            reject(e);
        }
    });
};