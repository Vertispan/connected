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

class SelectionEvent<T>
/**
 * Creates a new selection event.

 * @param selectedItem selected item
 */
protected constructor(
        /**
         * Gets the selected item.

         * @return the selected item
         */
        val selectedItem: T) {

    interface SelectionHandler<T> {
        fun onSelection(var1: SelectionEvent<T>)
    }

    interface HasSelectionHandlers<T> {
        fun addSelectionHandler(selectionHandler: (SelectionEvent<T>) -> Unit): (Unit) -> Unit

        fun fireEvent(event: SelectionEvent<T>)
    }

    interface HandlerRegistration {
        fun removeHandler()
    }

    protected fun dispatch(handler: (SelectionEvent<T>) -> Unit) {
        handler(this)
    }

    companion object {


        /**
         * Fires a selection event on all registered handlers in the handler
         * manager.If no such handlers exist, this method will do nothing.

         * @param  the selected item type
         * *
         * @param source the source of the handlers
         * *
         * @param selectedItem the selected item
         */
        fun <T> fire(source: HasSelectionHandlers<T>, selectedItem: T) {
            val event = SelectionEvent(selectedItem)
            source.fireEvent(event)
        }
    }
}
