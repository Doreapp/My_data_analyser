package com.mandin.antoine.mydataanalyser.facebook

import com.mandin.antoine.mydataanalyser.facebook.model.Person

object Persons {
    private val persons = HashMap<String, Person>()

    fun getPerson(name: String): Person {
        var result = persons[name]
        if (result == null) {
            result = Person(name)
            persons[name] = result

        }
        return result
    }
}