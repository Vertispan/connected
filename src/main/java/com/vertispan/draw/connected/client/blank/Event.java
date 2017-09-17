package com.vertispan.draw.connected.client.blank;

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

import com.vertispan.draw.connected.client.blank.SelectionEvent.HandlerRegistration;
import elemental2.dom.DomGlobal;
import elemental2.dom.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by colin on 9/16/17.
 */
public class Event {
    private static final EventListener dispatchCapturedMouseEvent;
    private static final EventListener dispatchCapturedEvent;
    private static final List<NativePreviewHandler> previewHandlers = new ArrayList<>();
    static {
        dispatchCapturedEvent = Event::dispatchCapturedEvent;
        dispatchCapturedMouseEvent = Event::dispatchCapturedEvent;

        DomGlobal.window.addEventListener("click", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("dblclick", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("mousedown", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("mouseup", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("mousemove", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("mouseover", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("mouseout", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("mousewheel", dispatchCapturedMouseEvent, true);

        DomGlobal.window.addEventListener("keydown", dispatchCapturedEvent, true);
        DomGlobal.window.addEventListener("keyup", dispatchCapturedEvent, true);
        DomGlobal.window.addEventListener("keypress", dispatchCapturedEvent, true);

        DomGlobal.window.addEventListener("touchstart", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("touchend", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("touchmove", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("touchcancel", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("gesturestart", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("gestureend", dispatchCapturedMouseEvent, true);
        DomGlobal.window.addEventListener("gesturechange", dispatchCapturedMouseEvent, true);

    }
    public interface NativePreviewHandler {
        void onPreviewNativeEvent(Event.NativePreviewEvent var1);
    }

    public static class NativePreviewEvent {
        private static Event.NativePreviewEvent singleton = new NativePreviewEvent();
        private boolean isCanceled = false;
        private boolean isConsumed = false;
        private boolean isFirstHandler = false;
        private elemental2.dom.Event nativeEvent;

        private NativePreviewEvent() {
        }

        public elemental2.dom.Event getNativeEvent() {
            return nativeEvent;
        }
    }

    public static HandlerRegistration addNativePreviewHandler(NativePreviewHandler handler) {
        previewHandlers.add(handler);//TODO consider a nicer way to add this after events finish going off like HandlerManager does?

        return () -> previewHandlers.remove(handler);
    }

    private static void dispatchCapturedEvent(elemental2.dom.Event event) {
        //in theory we could branch here and support gwt-user's old mouse capture tools

        //all handlers get a crack at this, then we check if it was canceled
        NativePreviewEvent.singleton.nativeEvent = event;
        for (int i = 0; i < previewHandlers.size(); i++) {
            previewHandlers.get(i).onPreviewNativeEvent(NativePreviewEvent.singleton);
        }

        boolean ret = !NativePreviewEvent.singleton.isCanceled || NativePreviewEvent.singleton.isConsumed;
        if (!ret) {
            event.stopPropagation();
            event.preventDefault();
        }
    }
}
