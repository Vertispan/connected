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
 * Simple rectangle model for drawing content and checking collisions.
 */
class Rect(val x: Double, val y: Double, val w: Double, val h: Double) {

    fun center(): Point {
        return Point(x + w / 2, y + h / 2)
    }

    operator fun contains(point: Point): Boolean {
        return x <= point.x && x + w >= point.x &&
                y <= point.y && y + h >= point.y
    }

    fun translate(point: Point): Rect {
        return Rect(x + point.x, y + point.y, w, h)
    }

    val topLeft: Point
        get() = Point(x, y)
}
