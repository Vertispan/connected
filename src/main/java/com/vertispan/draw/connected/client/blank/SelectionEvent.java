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

public class SelectionEvent<T> {

    public interface SelectionHandler<T> {
        void onSelection(SelectionEvent<T> var1);
    }
    public interface HasSelectionHandlers<T> {
        HandlerRegistration addSelectionHandler(SelectionHandler<T> var1);

        void fireEvent(SelectionEvent<T> event);
    }

    public interface HandlerRegistration {
        void removeHandler();
    }


    /**
     * Fires a selection event on all registered handlers in the handler
     * manager.If no such handlers exist, this method will do nothing.
     *
     * @param <T> the selected item type
     * @param source the source of the handlers
     * @param selectedItem the selected item
     */
    public static <T> void fire(HasSelectionHandlers<T> source, T selectedItem) {
        SelectionEvent<T> event = new SelectionEvent<T>(selectedItem);
        source.fireEvent(event);
    }

    private final T selectedItem;

    /**
     * Creates a new selection event.
     *
     * @param selectedItem selected item
     */
    protected SelectionEvent(T selectedItem) {
        this.selectedItem = selectedItem;
    }

    /**
     * Gets the selected item.
     *
     * @return the selected item
     */
    public T getSelectedItem() {
        return selectedItem;
    }

    protected void dispatch(SelectionHandler<T> handler) {
        handler.onSelection(this);
    }
}
