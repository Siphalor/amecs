#!/usr/bin/env sh

export_png() {
  file="$1-$2.png"
  file_min="$1-$2.min.png"
  inkscape --export-area-drawing --export-width="$2" --export-height="$2" --export-filename="$file" "$1.svg"
  pngquant --quality 20-80 "$file" -o "$file_min" -f --skip-if-larger
  mv "$file_min" "$file"
}

export_png core-logo 48
export_png core-logo 128
