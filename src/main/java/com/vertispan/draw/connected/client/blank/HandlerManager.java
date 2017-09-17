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
import com.vertispan.draw.connected.client.blank.SelectionEvent.HasSelectionHandlers;
import com.vertispan.draw.connected.client.blank.SelectionEvent.SelectionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by colin on 9/16/17.
 */
public class HandlerManager {
    private final HasSelectionHandlers object;
    private Map<Class<?>, List<SelectionHandler<?>>> handlers = new HashMap<>();

    public HandlerManager(HasSelectionHandlers object) {

        this.object = object;
    }

    public <B> HandlerRegistration addHandler(Class<SelectionEvent> selectionEventClass, SelectionHandler<B> selectionHandler) {
        handlers.computeIfAbsent(selectionEventClass, ignore -> new ArrayList<>()).add(selectionHandler);
        return () -> {
            handlers.get(selectionEventClass).remove(selectionHandler);
        };
    }

    public void fireEvent(SelectionEvent<?> gwtEvent) {
        handlers.get(SelectionEvent.class).forEach(handler -> ((SelectionHandler)handler).onSelection(gwtEvent));
    }
}
