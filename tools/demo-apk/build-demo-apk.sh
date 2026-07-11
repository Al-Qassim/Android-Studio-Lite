#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
REPO="$(cd "$ROOT/../.." && pwd)"
OUT="$REPO/feature/buildapk/data/src/main/assets/demo-sample.apk"
SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
BT="$(ls -d "$SDK/build-tools"/*/ | sort -V | tail -1)"
PLATFORM="$(ls -d "$SDK/platforms"/android-* | sort -V | tail -1)"
WORK="$(mktemp -d)"
trap 'rm -rf "$WORK"' EXIT

mkdir -p "$WORK"/{src/com/robotopia/androidstudiolite/demo,gen,obj,bin,res/values}

cat > "$WORK/AndroidManifest.xml" <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.robotopia.androidstudiolite.demo">
    <uses-sdk android:minSdkVersion="26" android:targetSdkVersion="35" />
    <application
        android:label="@string/app_name"
        android:allowBackup="false"
        android:hasCode="true">
        <activity
            android:name="com.robotopia.androidstudiolite.demo.DemoActivity"
            android:exported="true"
            android:label="@string/app_name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
EOF

cat > "$WORK/res/values/strings.xml" <<'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">ASL Demo</string>
</resources>
EOF

cat > "$WORK/src/com/robotopia/androidstudiolite/demo/DemoActivity.java" <<'EOF'
package com.robotopia.androidstudiolite.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class DemoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("ASL Demo");
        tv.setTextSize(24f);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.rgb(18, 23, 28));
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);
    }
}
EOF

"$BT/aapt2" compile --dir "$WORK/res" -o "$WORK/compiled.zip"
"$BT/aapt2" link -o "$WORK/bin/base.apk" \
  -I "$PLATFORM/android.jar" \
  --manifest "$WORK/AndroidManifest.xml" \
  --java "$WORK/gen" \
  "$WORK/compiled.zip"

javac --release 11 \
  -classpath "$PLATFORM/android.jar" \
  -d "$WORK/obj" \
  $(find "$WORK/src" "$WORK/gen" -name '*.java')

"$BT/d8" --lib "$PLATFORM/android.jar" --min-api 26 --output "$WORK/bin" \
  $(find "$WORK/obj" -name '*.class')

cp "$WORK/bin/base.apk" "$WORK/bin/unsigned.apk"
(cd "$WORK/bin" && zip -u unsigned.apk classes.dex)
"$BT/zipalign" -f 4 "$WORK/bin/unsigned.apk" "$WORK/bin/aligned.apk"
"$BT/apksigner" sign \
  --ks "${DEBUG_KEYSTORE:-$HOME/.android/debug.keystore}" \
  --ks-pass pass:android --key-pass pass:android \
  --ks-key-alias androiddebugkey \
  --out "$OUT" "$WORK/bin/aligned.apk"

echo "Wrote $OUT"
"$BT/aapt2" dump badging "$OUT" | head -8
