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

/**
 * Simple double,double point
 */
class Point(val x: Double, val y: Double) {

    fun relativeTo(x: Double, y: Double): Point {
        return Point(x - this.x, y - this.y)
    }

    fun relativeTo(other: Point): Point {
        return Point(other.x - x, other.y - y)
    }
}
