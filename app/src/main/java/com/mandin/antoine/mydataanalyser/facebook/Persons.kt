package com.mandin.antoine.mydataanalyser.facebook

import com.mandin.antoine.mydataanalyser.facebook.model.Person

/**
 * Person collection storing persons in a map, in order to
 * find a person by its name quickly
 */
object Persons {
    private val persons = HashMap<String, Person>()

    /**
     * Find a person by its name
     */
    fun getPerson(name: String): Person? {
        return persons[name]
    }

    /**
     * Add a person to the collection
     */
    fun addPerson(person: Person) {
        person.name?.let { name ->
            persons[name] = person
        }
    }

    /**
     * If `person` is not in the collection, add it
     */
    fun addPersonIfNew(person: Person) {
        person.name?.let { name ->
            if (!persons.containsKey(name))
                persons[name] = person
        }
    }
}