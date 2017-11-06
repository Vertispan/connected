package com.vertispan.draw.connected.client.lib

/*
 * #%L
 * connected
 * %%
 * Copyright (C) 2017 Vertispan
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vertispan.draw.connected.client.blank.HandlerManager
import com.vertispan.draw.connected.client.blank.SelectionEvent
import com.vertispan.draw.connected.client.blank.SelectionEvent.HandlerRegistration
import com.vertispan.draw.connected.client.blank.SelectionEvent.HasSelectionHandlers
import com.vertispan.draw.connected.client.blank.SelectionEvent.SelectionHandler
import com.vertispan.draw.connected.client.blank.StyleInjector
import com.vertispan.draw.connected.client.lib.DragTracker.DragHandling
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

import kotlin.browser.document
import kotlin.browser.window

/**
 * Base "widget" for this project. Not a GWT Widget, but wraps a dom element
 * and wires it up for easy use in a project.
 */
class ConnectedComponent<B, L>(
        private val boxIdFunct: (B) -> String,
        private val boxPosFunct: (B) -> Rect,
        private val boxCreator: (Rect) -> B,
        private val boxTextFunct: (B) -> String,
        private val boxPositionUpdater: (B, Rect) -> Unit,
        private val startFunct: (L) -> String,
        private val endFunct: (L) -> String,
        private val lineCreator: (B, B) -> L
) : HasSelectionHandlers<B> {
    //    /**
    //     * Simple interface to describe a tool's behavior when the user clicks or drags on
    //     * the canvas. Both methods take the event to interact with, and a callback to
    //     * request that an update occurs.
    //     *
    //     * @todo how to add extra pieces to the draw lifecycle?
    //     */
    //    public interface DrawTool {
    //        boolean handleMouseDown(MouseEvent event, FrameScheduler frameScheduler);
    //        boolean handleClick(MouseEvent event, FrameScheduler frameScheduler);
    //    }
    //    public interface FrameScheduler {
    //        void request();
    //    }

    private val handlerManager = HandlerManager(this)

    //dom
    val element: Element
    private val drawBoxTool: HTMLButtonElement
    private val drawLineTool: HTMLButtonElement
    private val moveTool: HTMLButtonElement
    private val canvasWrapper: HTMLDivElement
    private val canvas: HTMLCanvasElement

    //logic
    enum class DrawMode {
        MOVE, DRAW_BOX, DRAW_LINE
    }

    private var drawMode: DrawMode? = null

    //wiring
    private val boxes = LinkedHashMap<String, B>()

    private val lines = LinkedHashSet<L>()

    private val dragTracker = DragTracker()
    private var startingBoxForNewLine: B? = null
    private var currentEndForNewLine: Point? = null


    init {

        element = document.createElement("div")
        element.className = "boxes-and-lines"
        val buttonBar = document.createElement("div")
        buttonBar.classList.add("button-bar")

        drawBoxTool = document.createElement("button") as HTMLButtonElement
        drawBoxTool.onclick = this::drawBox
        drawBoxTool.innerHTML = "Draw Box"
        drawBoxTool.className = "button"

        drawLineTool = document.createElement("button") as HTMLButtonElement
        drawLineTool.onclick = this::drawLine
        drawLineTool.innerHTML = "Draw Line"
        drawLineTool.className = "button"

        moveTool = document.createElement("button") as HTMLButtonElement
        moveTool.onclick = this::move
        moveTool.innerHTML = "Move"
        moveTool.className = "button"

        canvasWrapper = document.createElement("div") as HTMLDivElement
        canvasWrapper.className = "canvas-wrapper"


        canvas = document.createElement("canvas") as HTMLCanvasElement
        //        canvas.width = 1000;
        //        canvas.height = 1000;
        canvas.onmousedown = this::canvasMouseDown//use for drags, captured events deal with the rest

        //TODO CSS that doesn't look terrible, and HTML template for this whole thing
        buttonBar.appendChild(drawBoxTool)
        buttonBar.appendChild(drawLineTool)
        buttonBar.appendChild(moveTool)
        element.appendChild(buttonBar)
        canvasWrapper.appendChild(canvas)
        element.appendChild(canvasWrapper)

        setDrawMode(DrawMode.MOVE)

        StyleInjector.inject("html,body{width:100%;height:100%;margin:0;}\n" +
                "body { display: flex; }\n" +
                "\n" +
                "\n" +
                ".button { background-color: white; }\n" +
                "button.button-on { background-color:gray; }\n" +
                "\n" +
                ".boxes-and-lines { display: flex; flex-flow: row nowrap; align-items: stretch; flex: 1 1 auto; }\n" +
                "\n" +
                ".button-bar { flex: 0 1 auto; }\n" +
                ".button-bar button {display:block}\n" +
                "\n" +
                ".canvas-wrapper { flex: 1 1 auto; overflow: hidden; }")


        //TODO this will leak after widget is detached...
        window.addEventListener("resize", { event -> scheduleFrame() })
    }

    override fun addSelectionHandler(selectionHandler: (SelectionEvent<B>) -> Unit): (Unit) -> Unit {
        return handlerManager.addHandler(selectionHandler)
    }

    override fun fireEvent(event: SelectionEvent<B>) {
        handlerManager.fireEvent(event)
    }

    fun setDrawMode(drawMode: DrawMode) {
        if (this.drawMode != drawMode) {
            //TODO cancel current drag, if any, to allow this to work at any time

            //turn off all buttons
            val buttons = element.querySelectorAll("button.button")
            for (i in 0..buttons.length - 1) {
                (buttons.get(i) as HTMLButtonElement).classList.remove("button-on")
            }

            //turn on currently set button
            when (drawMode) {
                ConnectedComponent.DrawMode.MOVE -> moveTool.classList.add("button-on")
                ConnectedComponent.DrawMode.DRAW_BOX -> drawBoxTool.classList.add("button-on")
                ConnectedComponent.DrawMode.DRAW_LINE -> drawLineTool.classList.add("button-on")
            }


            //actually set the current draw mode, so later mouse operations make sense
            this.drawMode = drawMode
        }
    }

    private fun drawBox(event: Event) {
        setDrawMode(DrawMode.DRAW_BOX)
    }

    private fun drawLine(event: Event) {
        setDrawMode(DrawMode.DRAW_LINE)
    }

    private fun move(event: Event) {
        setDrawMode(DrawMode.MOVE)
    }

    private fun canvasMouseDown(event: Event) {
        if (drawMode == DrawMode.DRAW_BOX) {
            //track mouse, but use drag tool to detect click to avoid moving to a new place
            dragTracker.start(event, object : DragHandling {
                override fun click(event: MouseEvent) {
                    //create a box at mouse coords
                    val mouse = pointFromMouseEvent(event)
                    val box = boxCreator(Rect(mouse.x, mouse.y, 10.0, 10.0))
                    addBox(box)
                    editBox(box)
                }
            })
        } else if (drawMode == DrawMode.DRAW_LINE) {
            //mark the box where we are starting
            startingBoxForNewLine = boxAtPoint(pointFromMouseEvent(event as MouseEvent))
            dragTracker.start(event, object : DragHandling {
                override fun click(event: MouseEvent) {
                    // ignore as a drag, perform edit instead
                    startingBoxForNewLine = null
                    currentEndForNewLine = null

                    val box = boxAtPoint(pointFromMouseEvent(event))
                    if (box != null) {
                        editBox(box)
                    }
                }

                override fun moveDrag(event: MouseEvent) {
                    // move the point since the user dragged
                    currentEndForNewLine = pointFromMouseEvent(event)
                    scheduleFrame()
                }

                override fun endDrag(event: MouseEvent) {
                    //create the line
                    val box = boxAtPoint(pointFromMouseEvent(event))
                    if (box != null) {
                        val line = lineCreator(startingBoxForNewLine!!, box)
                        addLine(line) //includes scheduleFrame
                    }
                    startingBoxForNewLine = null
                    currentEndForNewLine = null
                    scheduleFrame()
                }

                override fun cancelDrag() {
                    startingBoxForNewLine = null
                    currentEndForNewLine = null
                }
            })
        } else if (drawMode == DrawMode.MOVE) {
            //(startmouse - endmouse) + startCoords
            //
            val mouseStartPosition = pointFromMouseEvent(event as MouseEvent)
            val box = boxAtPoint(mouseStartPosition)

            val start = if (box == null) null else boxPosFunct(box)
            //            Point offset = start.getTopLeft().relativeTo(mouseStartPosition);
            dragTracker.start(event, object : DragHandling {
                override fun click(event: MouseEvent) {
                    //leave it, perform an edit instead

                    val box = boxAtPoint(pointFromMouseEvent(event))
                    if (box != null) {
                        editBox(box)
                    }
                }

                override fun moveDrag(event: MouseEvent) {
                    if (start == null) {
                        return
                    }
                    val currentMousePosition = pointFromMouseEvent(event)
                    val newBounds = start.translate(mouseStartPosition.relativeTo(currentMousePosition))
                    boxPositionUpdater(box!!, newBounds)
                    scheduleFrame()
                }
            })
        }
    }

    private fun boxAtPoint(point: Point): B? {
        return boxes.values.filter({ box -> boxPosFunct(box).contains(point) }).firstOrNull()
    }

    private fun pointFromMouseEvent(event: MouseEvent): Point {
        //offset x/y relies on the mouse staying over the element
        return Point(event.pageX - canvas.offsetLeft, event.pageY - canvas.offsetTop)
    }

    private fun editBox(box: B) {
        SelectionEvent.fire(this, box)
    }

    fun addLine(line: L) {
        lines.add(line)
        scheduleFrame()
    }

    fun removeLine(line: L) {
        lines.remove(line)
        scheduleFrame()
    }

    fun addBox(box: B) {
        val id = boxIdFunct(box)
        boxes.put(id, box)
        scheduleFrame()
    }

    fun removeBox(box: B) {
        val id = boxIdFunct(box)
        boxes.remove(id)
        lines.removeAll { line -> startFunct(line) == id || endFunct(line) == id }
        scheduleFrame()
    }

    fun updateBox(box: B) {
        val id = boxIdFunct(box)
        boxes.put(id, box)

        scheduleFrame()
    }

    fun getBoxes(): List<B> {
        return ArrayList(boxes.values)
    }

    fun getLines(): List<L> {
        return ArrayList(lines)
    }


    private var frameScheduled = false
    private fun scheduleFrame() {
        if (frameScheduled) {
            return
        }
        frameScheduled = true
        window.requestAnimationFrame { timestamp ->
            frameScheduled = false
            draw()
        }
    }

    private fun draw() {
        //casts to get context, bug in elemental2 beta...
        val context = canvas.getContext("2d") as CanvasRenderingContext2D

        //resize to fit, if needed (this is ... expensive to check, and wrong if we are on a devicePixelRatio!=1 screen)
        val size = canvasWrapper.getBoundingClientRect()
        if (size.height.toInt() != canvas.height || size.width.toInt() != canvas.width) {
            //assuming there is something to be gained by not tweaking these directly, but should measure...
            //            Double devicePixelRatio = ((JsPropertyMap<Double>) DomGlobal.window).get("devicePixelRatio");
            canvas.height = size.height.toInt() - 10// * devicePixelRatio;
            canvas.width = size.width.toInt()// * devicePixelRatio;
            //            canvas.style.height = HeightUnionType.of(size.height + "px");
            //            canvas.style.width = WidthUnionType.of(size.height + "px");
        }


        //remove all current content
        //TODO in the future detect changes and apply a clip?
        context.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        context.fillStyle = "#ffffff"
        context.strokeStyle = "#000000"
        context.font = "14px sans-serif"

        //draw all lines, then all boxes. boxes have a fill, so the lines always are from the center of a box, starting at the edge
        lines.forEach { line ->
            val start = boxes[startFunct(line)]
            val startPoint = boxPosFunct(start!!).center()
            val end = boxes[endFunct(line)]
            val endPoint = boxPosFunct(end!!).center()
            context.beginPath()
            context.moveTo(startPoint.x, startPoint.y)
            context.lineTo(endPoint.x, endPoint.y)
            context.stroke()
        }

        //draw the line we're creating, if any
        if (startingBoxForNewLine != null && currentEndForNewLine != null) {
            val startPoint = boxPosFunct(startingBoxForNewLine!!).center()
            context.beginPath()
            context.moveTo(startPoint.x, startPoint.y)
            context.lineTo(currentEndForNewLine!!.x, currentEndForNewLine!!.y)
            context.stroke()
        }

        boxes.values.forEach { box ->
            val position = boxPosFunct(box)
            context.fillRect(position.x, position.y, position.w, position.h)
            context.strokeRect(position.x, position.y, position.w, position.h)

            val padding = 10
            val fontHeight = 14
            val lines = boxTextFunct(box).split("\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            context.fillStyle = "#000000"
            for (lineNo in lines.indices) {
                context.fillText(lines[lineNo], padding + position.x, fontHeight.toDouble() + padding.toDouble() + position.y + (fontHeight * lineNo).toDouble())
            }
            context.fillStyle = "#ffffff"
        }

    }
}
