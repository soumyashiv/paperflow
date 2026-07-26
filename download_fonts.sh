#!/bin/bash
# download_fonts.sh
# Run once from project root to download Inter font files into res/font/
# Inter is licensed under SIL OFL 1.1 — free for commercial use.

FONT_DIR="app/src/main/res/font"
mkdir -p "$FONT_DIR"

BASE="https://github.com/rsms/inter/raw/master/docs/font-files"

curl -L "$BASE/Inter-Regular.ttf"  -o "$FONT_DIR/inter_regular.ttf"
curl -L "$BASE/Inter-Medium.ttf"   -o "$FONT_DIR/inter_medium.ttf"
curl -L "$BASE/Inter-SemiBold.ttf" -o "$FONT_DIR/inter_semibold.ttf"
curl -L "$BASE/Inter-Bold.ttf"     -o "$FONT_DIR/inter_bold.ttf"

echo "Inter fonts downloaded successfully."
