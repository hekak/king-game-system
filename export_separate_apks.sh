#!/bin/bash
set -e

echo "=== 1. Creating output folder for APKs ==="
mkdir -p exported_apks

# Backup original files
cp app/src/main/AndroidManifest.xml AndroidManifest.xml.bak
cp app/build.gradle.kts build.gradle.kts.bak

echo "=== 2. Building Player App (KingGame_Player_App.apk) ==="
# Set applicationId to player
sed -i 's/applicationId = "com.aistudio.superace.eobdub"/applicationId = "com.aistudio.kinggame.player"/g' app/build.gradle.kts

# Create Player Manifest without AdminActivity
cat << 'EOF' > app/src/main/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:usesCleartextTraffic="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:icon="@mipmap/ic_launcher"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:label="@string/app_name"
            android:windowSoftInputMode="adjustResize"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
    </application>

</manifest>
EOF

gradle assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk exported_apks/KingGame_Player_App.apk
cp app/build/outputs/apk/debug/app-debug.apk exported_apks/SuperAce_Player_App.apk
echo ">> Player App successfully generated: exported_apks/KingGame_Player_App.apk"

echo "=== 3. Building Admin App (KingGame_Admin_App.apk) ==="
# Restore build.gradle.kts then set applicationId to admin
cp build.gradle.kts.bak app/build.gradle.kts
sed -i 's/applicationId = "com.aistudio.superace.eobdub"/applicationId = "com.aistudio.kinggame.admin"/g' app/build.gradle.kts

# Create Admin Manifest with only AdminActivity and Admin Icon
cat << 'EOF' > app/src/main/AndroidManifest.xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:usesCleartextTraffic="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_admin_launcher"
        android:label="@string/admin_app_name"
        android:roundIcon="@mipmap/ic_admin_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApplication">
        <activity
            android:name=".AdminActivity"
            android:exported="true"
            android:icon="@mipmap/ic_admin_launcher"
            android:roundIcon="@mipmap/ic_admin_launcher_round"
            android:label="@string/admin_app_name"
            android:windowSoftInputMode="adjustResize"
            android:theme="@style/Theme.MyApplication">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
    </application>

</manifest>
EOF

gradle assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk exported_apks/KingGame_Admin_App.apk
cp app/build/outputs/apk/debug/app-debug.apk exported_apks/SuperAce_Admin_App.apk
echo ">> Admin App successfully generated: exported_apks/KingGame_Admin_App.apk"

echo "=== 4. Restoring original environment and combined configuration ==="
cp AndroidManifest.xml.bak app/src/main/AndroidManifest.xml
cp build.gradle.kts.bak app/build.gradle.kts
rm -f AndroidManifest.xml.bak build.gradle.kts.bak

gradle assembleDebug
cp app/build/outputs/apk/debug/app-debug.apk exported_apks/KingGame_DualLauncher_App.apk
cp app/build/outputs/apk/debug/app-debug.apk exported_apks/SuperAce_DualLauncher_App.apk

echo "=== 5. Finished! Exported APKs list: ==="
ls -lh exported_apks/
