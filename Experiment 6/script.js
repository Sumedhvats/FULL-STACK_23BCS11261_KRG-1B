const SVG_NS = "http://www.w3.org/2000/svg";

const svg = document.getElementById("drawingArea");
const shapeSelect = document.getElementById("shapeSelect");
const strokeColor = document.getElementById("strokeColor");
const fillColor = document.getElementById("fillColor");
const strokeWidth = document.getElementById("strokeWidth");
const undoBtn = document.getElementById("undoBtn");
const clearBtn = document.getElementById("clearBtn");

let drawing = false;
let start = { x: 0, y: 0 };
let currentShape = null;
const undoStack = [];

// Utility: Get coordinates relative to SVG
function getMousePosition(e) {
  const rect = svg.getBoundingClientRect();
  return { x: e.clientX - rect.left, y: e.clientY - rect.top };
}

function createShape(type) {
  const el = document.createElementNS(SVG_NS, type);
  el.setAttribute("stroke", strokeColor.value);
  el.setAttribute("stroke-width", strokeWidth.value);
  el.setAttribute("fill", type === "line" ? "none" : fillColor.value);
  el.setAttribute("opacity", "0.9");
  el.style.transition = "all 0.1s ease";
  return el;
}

svg.addEventListener("mousedown", (e) => {
  drawing = true;
  start = getMousePosition(e);
  const type = shapeSelect.value;
  currentShape = createShape(type);

  switch (type) {
    case "circle":
      currentShape.setAttribute("cx", start.x);
      currentShape.setAttribute("cy", start.y);
      currentShape.setAttribute("r", 0);
      break;
    case "rect":
      currentShape.setAttribute("x", start.x);
      currentShape.setAttribute("y", start.y);
      currentShape.setAttribute("width", 0);
      currentShape.setAttribute("height", 0);
      break;
    case "line":
      currentShape.setAttribute("x1", start.x);
      currentShape.setAttribute("y1", start.y);
      currentShape.setAttribute("x2", start.x);
      currentShape.setAttribute("y2", start.y);
      break;
  }

  svg.appendChild(currentShape);
});

svg.addEventListener("mousemove", (e) => {
  if (!drawing || !currentShape) return;

  const pos = getMousePosition(e);
  const type = shapeSelect.value;

  switch (type) {
    case "circle":
      const r = Math.hypot(pos.x - start.x, pos.y - start.y);
      currentShape.setAttribute("r", r);
      break;

    case "rect":
      const x = Math.min(pos.x, start.x);
      const y = Math.min(pos.y, start.y);
      const w = Math.abs(pos.x - start.x);
      const h = Math.abs(pos.y - start.y);
      currentShape.setAttribute("x", x);
      currentShape.setAttribute("y", y);
      currentShape.setAttribute("width", w);
      currentShape.setAttribute("height", h);
      break;

    case "line":
      currentShape.setAttribute("x2", pos.x);
      currentShape.setAttribute("y2", pos.y);
      break;
  }
});

svg.addEventListener("mouseup", () => {
  if (currentShape) undoStack.push(currentShape);
  drawing = false;
  currentShape = null;
});

svg.addEventListener("mouseleave", () => {
  drawing = false;
  currentShape = null;
});

undoBtn.addEventListener("click", () => {
  const last = undoStack.pop();
  if (last) svg.removeChild(last);
});

clearBtn.addEventListener("click", () => {
  svg.innerHTML = "";
  undoStack.length = 0;
});

// Keyboard shortcut for undo
document.addEventListener("keydown", (e) => {
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === "z") {
    undoBtn.click();
  }
});
