package com.example.test.mydataanalyser.facebook

import com.example.test.mydataanalyser.facebook.model.Person

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