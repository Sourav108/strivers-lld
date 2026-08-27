#!/usr/bin/env bash
set -e

# Scaffolds and verifies all 18 folders of Striver's A2Z DSA Sheet (474 Problems)
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DSA_DIR="$ROOT_DIR/DSA-A2Z-Sheet"

mkdir -p "$DSA_DIR"

declare -a FOLDERS=(
  "01-Learn-the-Basics"
  "02-Sorting-Techniques"
  "03-Arrays"
  "04-Binary-Search"
  "05-Strings-Basic"
  "06-LinkedList"
  "07-Recursion"
  "08-Bit-Manipulation"
  "09-Stacks-and-Queues"
  "10-Sliding-Window-Two-Pointer"
  "11-Heaps"
  "12-Greedy"
  "13-Binary-Trees"
  "14-Binary-Search-Trees"
  "15-Graphs"
  "16-Dynamic-Programming"
  "17-Tries"
  "18-Strings-Advanced"
)

echo "🚀 Scaffolding Striver's A2Z DSA Sheet (474 Problems)..."

for folder in "${FOLDERS[@]}"; do
  mkdir -p "$DSA_DIR/$folder"
  if [ ! -f "$DSA_DIR/$folder/README.md" ]; then
    echo "Creating stub for $folder..."
    cat <<EOF > "$DSA_DIR/$folder/README.md"
# $folder

Problem checklist and solutions for $folder.
EOF
  fi
done

echo "✅ All 18 folders verified in $DSA_DIR."
