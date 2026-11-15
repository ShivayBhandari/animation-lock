#!/usr/bin/env bash
# Builds and signs build/anim-lock.apk without Gradle. Runs in Git Bash on Windows and on Linux.
#
# Needs: JAVA_HOME (JDK 17), ANDROID_HOME (with platforms;android-35 and build-tools;36.0.0),
# KEYSTORE_PATH and KEYSTORE_PASSWORD. VERSION_CODE and VERSION_NAME are optional.
set -euo pipefail

cd "$(dirname "$0")"

: "${JAVA_HOME:?set JAVA_HOME to a JDK 17}"
: "${ANDROID_HOME:?set ANDROID_HOME to the Android SDK folder}"
: "${KEYSTORE_PATH:?set KEYSTORE_PATH to the signing keystore}"
: "${KEYSTORE_PASSWORD:?set KEYSTORE_PASSWORD}"

export PATH="$JAVA_HOME/bin:$PATH"
BUILD_TOOLS="$ANDROID_HOME/build-tools/36.0.0"
ANDROID_JAR="$ANDROID_HOME/platforms/android-35/android.jar"

version_args=()
if [ -n "${VERSION_CODE:-}" ]; then
  version_args+=(--version-code "$VERSION_CODE" --version-name "${VERSION_NAME:-$VERSION_CODE}" --replace-version)
fi

rm -rf build
mkdir -p build/classes build/dex

"$BUILD_TOOLS/aapt2" link \
  --manifest AndroidManifest.xml \
  -I "$ANDROID_JAR" \
  "${version_args[@]}" \
  -o build/unsigned.apk

javac --release 17 -cp "$ANDROID_JAR" -d build/classes $(find src -name '*.java')

java -cp "$BUILD_TOOLS/lib/d8.jar" com.android.tools.r8.D8 \
  --release --min-api 34 --lib "$ANDROID_JAR" --output build/dex \
  $(find build/classes -name '*.class')

(cd build/dex && jar uf ../unsigned.apk classes.dex)

"$BUILD_TOOLS/zipalign" -f -p 4 build/unsigned.apk build/aligned.apk

java -jar "$BUILD_TOOLS/lib/apksigner.jar" sign \
  --ks "$KEYSTORE_PATH" \
  --ks-pass env:KEYSTORE_PASSWORD \
  --out build/anim-lock.apk \
  build/aligned.apk

echo "Built build/anim-lock.apk"
