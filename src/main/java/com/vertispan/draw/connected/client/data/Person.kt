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

import java.util.Date

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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val person = o as Person?

        if (if (id != null) id != person!!.id else person!!.id != null) return false
        if (if (name != null) name != person.name else person.name != null) return false
        if (if (sex != null) sex != person.sex else person.sex != null) return false
        if (if (birthplace != null) birthplace != person.birthplace else person.birthplace != null) return false
        return !if (birthday != null) birthday != person.birthday else person.birthday != null

    }

    override fun hashCode(): Int {
        var result = if (id != null) id!!.hashCode() else 0
        result = 31 * result + if (name != null) name!!.hashCode() else 0
        result = 31 * result + if (sex != null) sex!!.hashCode() else 0
        result = 31 * result + if (birthplace != null) birthplace!!.hashCode() else 0
        result = 31 * result + if (birthday != null) birthday!!.hashCode() else 0
        return result
    }
}
