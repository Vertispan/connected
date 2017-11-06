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

import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

/**
 * Mouse tracking through preview events, with a callback interface to implement behavior for a given drag
 */
class DragTracker {

    /**

     */
    interface DragHandling {
        open fun click(event: MouseEvent) {
        }

        fun startDrag(event: MouseEvent) {
        }

        open fun moveDrag(event: MouseEvent) {
        }

        open fun endDrag(event: MouseEvent) {
        }

        open fun cancelDrag() {
        }
    }

    private var mouseEventPreview: (() -> Unit)? = null
    private var moved: Boolean = false
    private var dragging = false

    private var handler: DragHandling? = null

    fun start(event: Event, handler: DragHandling) {
        this.handler = handler
//        assert(!dragging)
        dragging = true
        moved = false

        handler.startDrag(event as MouseEvent)//ok, this seems a bit silly, since we are calling it, not vice versa...
        //TODO switch to User, don't yet know the new metaphor for this...
        mouseEventPreview = com.vertispan.draw.connected.client.blank.Event.addNativePreviewHandler { captured ->
            val nativeEvent = captured.nativeEvent
            when (nativeEvent!!.type) {
                "mousemove" -> move(nativeEvent as MouseEvent)

                "mouseup" ->
                    //click, or release drag
                    if (moved) {
                        endDrag(nativeEvent as MouseEvent)
                    } else {
                        endClick(nativeEvent as MouseEvent)
                    }
            }
        }

        event.preventDefault()
    }

    private fun endClick(nativeEvent: MouseEvent) {
        //technically not dragging. probably should be called tracking instead.
//        assert(dragging)
//        assert(!moved)

        if (handler != null) {
            handler!!.click(nativeEvent)
        }

        dragging = false
        mouseEventPreview!!()
    }

    private fun endDrag(nativeEvent: MouseEvent) {
//        assert(dragging)
//        assert(moved)

        if (handler != null) {
            handler!!.endDrag(nativeEvent)
        }

        dragging = false
        mouseEventPreview!!()
    }

    private fun move(nativeEvent: MouseEvent) {
//        assert(dragging)
        moved = true

        if (handler != null) {
            handler!!.moveDrag(nativeEvent)
        }
    }

    fun cancel() {
        dragging = false
        mouseEventPreview!!()

        if (handler != null) {
            handler!!.cancelDrag()
        }
    }
}
