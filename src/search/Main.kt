package search

import java.io.File

fun main(args: Array<String>) {

    val data = File(args[1]).readLines()
    val index = invertedIndex(data)

    while (true) {
        println("=== Menu ===\n" +
                "1. Find a person\n" +
                "2. Print all people\n" +
                "0. Exit")
        when (readLine()!!.toInt()) {
            1 -> searchEngine(data, index)
            2 -> {
                println("=== List of people ===")
                data.forEach { println(it) }
            }
            0 -> {
                println("Bye!")
                break
            }
            else -> println("Incorrect option! Try again.")
        }
    }
}

fun searchEngine(data: List<String>, index: Map<String, Set<Int>>) {
    val strategy = getStrategy()
    println("Enter a name or email to search all suitable people.")
    val words = readLine()!!.split(" ")
    val inIndex = when (strategy) {
        "ALL" -> {
            words.map { getFromIndex(index, it) }.reduce { a, set -> a.intersect(set).toMutableSet() }
        }
        "ANY" -> {
            words.map { getFromIndex(index, it) }.reduce { a, set -> a.union(set).toMutableSet() }
        }
        "NONE" -> {
            var lines = data.indices.toMutableSet()
            for (el in words) {
                lines = lines.minus(getFromIndex(index, el)).toMutableSet()
            }
            lines
        }
        else -> emptySet()
    }

    if (inIndex.isNotEmpty()) {
        inIndex.forEach { println(data[it]) }
    } else
        println("No matching people found.")
}

fun invertedIndex(data: List<String>): Map<String, Set<Int>> {
    val index = mutableMapOf<String, MutableSet<Int>>()
    for (line in data.withIndex()) {
        for (el in line.value.lowercase().split(" ")) {
            val list = index.getOrDefault(el, mutableSetOf())
            list.add(line.index)
            index[el] = list
        }
    }
    return index
}

fun getStrategy(): String {
    while (true) {
        println("Select a matching strategy: ALL, ANY, NONE")
        val strategy = readLine()!!
        if (strategy in listOf("ALL", "ANY", "NONE")) {
            return strategy
        }
    }
}

fun getFromIndex(index: Map<String, Set<Int>>, query: String): Set<Int> {
    return index[query.lowercase()] ?: emptySet()
}