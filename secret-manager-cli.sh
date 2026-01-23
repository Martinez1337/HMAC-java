#!/bin/sh

# -------- config --------
APP_NAME="SecretManagerCLI"
MAIN_CLASS="io.github.martinez1337.hmac.util.SecretManagerCLI"

OUT_DIR="out"
LIB_DIR="lib"
GSON_JAR=$(find "$LIB_DIR"/gson-*.jar 2>/dev/null | head -n 1)
# ------------------------

if [ ! -f "$GSON_JAR" ]; then
  echo "gson.jar not found in $LIB_DIR"
  exit 1
fi

# компиляция
mkdir -p "$OUT_DIR"

javac -cp "$GSON_JAR" \
  -d "$OUT_DIR" \
  src/io/github/martinez1337/hmac/util/SecretManagerCLI.java || exit 1

# запуск
java -cp "$OUT_DIR:$GSON_JAR" "$MAIN_CLASS" "$@"
