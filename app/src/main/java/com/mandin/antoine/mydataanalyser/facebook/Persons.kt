package com.mandin.antoine.mydataanalyser.facebook

import com.mandin.antoine.mydataanalyser.facebook.model.Person

object Persons {
    private val persons = HashMap<String, Person>()

    fun getPerson(name: String): Person? {
        return persons[name]
    }

    fun addPerson(person: Person) {
        person.name?.let { name ->
            persons[name] = person
        }
    }

    fun addPersonIfNew(person: Person) {
        person.name?.let { name ->
            if (!persons.containsKey(name))
                persons[name] = person
        }
    }
}