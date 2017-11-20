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

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = "fin.desktop")
public class Application {
    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Options {/*TODO*/}
    @JsFunction
    public interface SuccessCallback {
        void onSuccess(Application result);
    }
    @JsFunction
    public interface FailureCallback {
        void onFailure(Object result);
    }
    public Application(Options options, SuccessCallback success, FailureCallback failure) {

    }
    public static native void createFromManifest(String manifestUrl, SuccessCallback success, FailureCallback failure);

    public static native Application getCurrent();

    public static native Application wrap(String uuid);

    
}
