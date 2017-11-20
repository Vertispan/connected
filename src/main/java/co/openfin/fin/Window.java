package co.openfin.fin;

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

import co.openfin.fin.Application.FailureCallback;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.util.function.Consumer;

@JsType(isNative = true, namespace = "fin.desktop")
public class Window {
    @JsType(isNative = true, name = "Object", namespace = JsPackage.GLOBAL)
    public static class Options {/*TODO*/
        public boolean frame;
    }

    @JsFunction
    public interface SuccessCallback<T> {
        void onSuccess(T obj);
    }

    public Window(Options options, SuccessCallback<Void> success, FailureCallback failureCallback) {

    }

    public static native Window getCurrent();

    public native elemental2.dom.Window getNativeWindow();

    public native Application getParentApplication();

    //TODO technically callbacks as args
    public native void enableFrame();
    public native void disableFrame();

    public native void getOptions(SuccessCallback<Options> success);
}
