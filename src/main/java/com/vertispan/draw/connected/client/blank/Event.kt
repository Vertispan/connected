package com.vertispan.draw.connected.client.blank

/*
 * #%L
 * Connected
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

import org.w3c.dom.events.EventListener
import kotlin.browser.window

/**
 * Created by colin on 9/16/17.
 */
object Event {
    private val dispatchCapturedMouseEvent: EventListener
    private val dispatchCapturedEvent: EventListener
    private val previewHandlers = ArrayList<(Event.NativePreviewEvent) -> Unit>()

    init {
        dispatchCapturedEvent = EventListener { dispatchCapturedEvent(it) }
        dispatchCapturedMouseEvent = EventListener { dispatchCapturedEvent(it) }

        window.addEventListener("click", dispatchCapturedMouseEvent, true)
        window.addEventListener("dblclick", dispatchCapturedMouseEvent, true)
        window.addEventListener("mousedown", dispatchCapturedMouseEvent, true)
        window.addEventListener("mouseup", dispatchCapturedMouseEvent, true)
        window.addEventListener("mousemove", dispatchCapturedMouseEvent, true)
        window.addEventListener("mouseover", dispatchCapturedMouseEvent, true)
        window.addEventListener("mouseout", dispatchCapturedMouseEvent, true)
        window.addEventListener("mousewheel", dispatchCapturedMouseEvent, true)

        window.addEventListener("keydown", dispatchCapturedEvent, true)
        window.addEventListener("keyup", dispatchCapturedEvent, true)
        window.addEventListener("keypress", dispatchCapturedEvent, true)

        window.addEventListener("touchstart", dispatchCapturedMouseEvent, true)
        window.addEventListener("touchend", dispatchCapturedMouseEvent, true)
        window.addEventListener("touchmove", dispatchCapturedMouseEvent, true)
        window.addEventListener("touchcancel", dispatchCapturedMouseEvent, true)
        window.addEventListener("gesturestart", dispatchCapturedMouseEvent, true)
        window.addEventListener("gestureend", dispatchCapturedMouseEvent, true)
        window.addEventListener("gesturechange", dispatchCapturedMouseEvent, true)

    }

    interface NativePreviewHandler {
        fun onPreviewNativeEvent(var1: Event.NativePreviewEvent)
    }

    class NativePreviewEvent private constructor() {
        val isCanceled = false
        val isConsumed = false
        private val isFirstHandler = false
        var nativeEvent: org.w3c.dom.events.Event? = null

        companion object {
            val singleton = NativePreviewEvent()
        }
    }

    fun addNativePreviewHandler(handler: (Event.NativePreviewEvent) -> Unit): () -> Unit {
        previewHandlers.add(handler)//TODO consider a nicer way to add this after events finish going off like HandlerManager does?

        return { previewHandlers.remove(handler) }
    }

    private fun dispatchCapturedEvent(event: org.w3c.dom.events.Event) {
        //in theory we could branch here and support gwt-user's old mouse capture tools

        //all handlers get a crack at this, then we check if it was canceled
        NativePreviewEvent.singleton.nativeEvent = event
        for (i in previewHandlers.indices) {
            previewHandlers[i](NativePreviewEvent.singleton)
        }

        val ret = !NativePreviewEvent.singleton.isCanceled || NativePreviewEvent.singleton.isConsumed
        if (!ret) {
            event.stopPropagation()
            event.preventDefault()
        }
    }
}
