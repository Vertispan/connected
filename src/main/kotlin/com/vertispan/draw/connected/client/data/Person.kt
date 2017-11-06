package com.vertispan.draw.connected.client.data

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

import com.vertispan.draw.connected.client.lib.Point

import kotlin.js.Date

/**
 * Simple "vertex" or "box" data model
 */
class Person (
    val id: String,

    var name: String,
    var sex: String,
    var birthplace: String,
    var birthday: Date,

    var pos: Point
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.js != other::class.js) return false

        other as Person

        if (id != other.id) return false
        if (name != other.name) return false
        if (sex != other.sex) return false
        if (birthplace != other.birthplace) return false
        if (birthday != other.birthday) return false
        if (pos != other.pos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + sex.hashCode()
        result = 31 * result + birthplace.hashCode()
        result = 31 * result + birthday.hashCode()
        result = 31 * result + pos.hashCode()
        return result
    }
}
