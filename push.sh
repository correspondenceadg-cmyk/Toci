#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

MSG="${1:-update}"

git add -A
git diff --cached --quiet && { echo "nothing to push"; exit 0; }
git commit -m "$MSG"

XY=$(head -1 VERSION | tr -d '[:space:]')
BASE=$(git log -1 --format=%H -- VERSION)
N=$(git rev-list --count "${BASE}..HEAD")
CODE=$(git rev-list --count HEAD)

echo ""
echo "  will release  v${XY}.${N}"
echo "  versionCode   ${CODE}"
echo ""

git push origin main

echo ""
echo "  pushed. CI is building and will publish:"
echo "  https://github.com/correspondenceadg-cmyk/Toci/releases/tag/v${XY}.${N}"
