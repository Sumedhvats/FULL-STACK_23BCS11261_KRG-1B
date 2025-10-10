# SVG Drawing Tool

**What this project contains**
- `index.html` — main page with toolbar and SVG canvas.
- `styles.css` — simple styling for the toolbar and canvas.
- `script.js` — all drawing logic: mousedown, mousemove, mouseup, undo, clear, download.

**How to run (step-by-step)**
1. Extract the ZIP and open the folder in **VS Code** (or any code editor).
2. If you have the **Live Server** extension in VS Code, right-click `index.html` → *Open with Live Server*.
   Otherwise, open `index.html` directly in Chrome/Firefox (double-click or `File → Open`).
3. Use the color picker to select a fill color.
4. Click and hold on the canvas, drag the mouse to set circle radius, release to finalize the circle.
5. Use the **Undo** button or `Ctrl/Cmd + Z` to undo the last shape. Use **Clear** to remove all shapes.
6. Click **Download SVG** to save your drawing as a standalone `.svg` file.

**Notes & extensions you can add**
- Support for rectangles/lines/ellipses by adding shape mode buttons and creating different elements.
- Add touch support by handling `touchstart`, `touchmove`, and `touchend` and normalizing coordinates.
- Add snapping, grid, or background images.
- Export to PNG by drawing the SVG on a canvas and using `canvas.toDataURL()`.

Enjoy — you can open `index.html` now to try it.
