package com.vertispan.draw.connected.client;

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

import java.nio.channels.Channel;

public class StaticTest {
    public static class A {
        public static String string = "1123";
        public static String str() {
            return string;
        }
    }
    public static class B extends A {
        @GwtIncompatible
        private Channel file;
        @GwtIncompatible
        public static String string = "asd";
        public static String str() {
            return string;
        }
    }

    @interface GwtIncompatible {}
}
