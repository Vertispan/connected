package com.vertispan.draw.connected.client

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

import com.vertispan.draw.connected.client.blank.DateTimeFormat
import com.vertispan.draw.connected.client.blank.DateTimeFormat.PredefinedFormat
import com.google.gwt.core.client.EntryPoint
import com.vertispan.draw.connected.client.data.IsParentRelationship
import com.vertispan.draw.connected.client.data.Person
import com.vertispan.draw.connected.client.lib.ConnectedComponent
import com.vertispan.draw.connected.client.lib.Point
import com.vertispan.draw.connected.client.lib.Rect
import elemental2.dom.DomGlobal

import java.util.Date

/**
 * Demo app that uses the Connected module to edit some data
 */
class FlowChartEntryPoint : EntryPoint {
    override fun onModuleLoad() {

        // Create the component, and tell it how to interact with our data (via lambdas)
        val boxesAndLines = ConnectedComponent<Person, IsParentRelationship>(
                { person -> person.id },
                { person -> Rect(person.pos.x, person.pos.y, 200.0, 150.0) },
                { rect ->
                    val person = Person(nextId(), "", "", "", Date(), rect.center())
                    person
                },
                { person -> person.name + "\n\n" + format.format(person.birthday) + " \n   in " + person.birthplace },
                { person, rect -> person.pos = rect.topLeft },
                IsParentRelationship::childId,
                IsParentRelationship::parentId
        ) { p1, p2 -> IsParentRelationship(p1.id, p2.id) }

        // Listen for selection, so we can prompt the user in some way to edit the data
        boxesAndLines.addSelectionHandler { event ->
            val person = event.selectedItem
            if (person.birthday == null) {
                person.birthday = Date()
            }
            val newName = DomGlobal.prompt("change name?", person.name)
            person.name = newName
            boxesAndLines.updateBox(person)
        }

        // Sample data
        val colin = Person(
                nextId(),
                "Colin",
                "M",
                "Annapolis, MD",
                Date(85, 3, 26),
                Point(10.0, 10.0)
        )
        boxesAndLines.addBox(colin)

        val karen = Person(
                nextId(),
                "Karen",
                "F",
                "Pontiac, MI",
                Date(84, 4, 13),
                Point(300.0, 10.0)
        )

        val abigail = Person(
                nextId(),
                "Abigail",
                "F",
                "Maple Grove, MN",
                Date(116, 8, 24),
                Point(150.0, 200.0)
        )
        boxesAndLines.addBox(abigail)

        // Sample relationships
        boxesAndLines.addLine(IsParentRelationship(abigail.id, colin.id))
        boxesAndLines.addLine(IsParentRelationship(abigail.id, karen.id))


        // Actually add the element to the body
        DomGlobal.document.body.appendChild(boxesAndLines.element)
    }

    companion object {
        private var nextId = 0
        private fun nextId(): String {
            return "" + ++nextId
        }

        private val format = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)
    }
}
