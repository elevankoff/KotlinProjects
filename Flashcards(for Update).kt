package flashcards
import java.io.File
import java.io.InputStream
import java.lang.Exception
import java.util.Scanner
import kotlin.collections.set

fun readingOfName(scanner: Scanner, definitionOfCards: MutableMap<String, String>,
                  logs: MutableList<String>): String {
    val name : String = scanner.next()
    logs.add(name)
    if (definitionOfCards.containsKey(name)) {
        println("The card \"$name\" already exists.")
        logs.add("The card \"$name\" already exists.")
        throw Exception("AE")
    }
    return name
}

fun readingOfDefinition(scanner: Scanner, nameOfCards: MutableMap<String, String>, logs: MutableList<String>): String {
    val definition : String = scanner.next()
    logs.add(definition)
    if (nameOfCards.containsKey(definition)) {
        println("The definition \"$definition\" already exists.")
        logs.add("The definition \"$definition\" already exists.")
        throw Exception("AE")
    }
    return definition
}

fun add(scanner: Scanner, definitionOfCards: MutableMap<String, String>,
        nameOfCards: MutableMap<String, String>, mistakeCounter: MutableMap<String, Int>,
        logs: MutableList<String>) {
    println("The card:")
    logs.add("The card:")
    val name: String
    try {
        name = readingOfName(scanner, definitionOfCards, logs)
    } catch (e : Exception) {
        return
    }

    println("The definition of the card:")
    logs.add("The definition of the card:")

    val definition : String
    try {
        definition = readingOfDefinition(scanner, nameOfCards, logs)
    } catch(error: Exception) {
        return
    }
    println("The pair (\"$name\":\"$definition\") has been added.")
    logs.add("The pair (\"$name\":\"$definition\") has been added.")

    definitionOfCards[name] = definition
    nameOfCards[definition] = name
    mistakeCounter[name] = 0
}

fun remove(scanner: Scanner, definitionOfCards: MutableMap<String, String>, nameOfCards: MutableMap<String, String>,
           mistakeCounter: MutableMap<String, Int>, logs: MutableList<String>) {
    println("The card:")
    logs.add("The card:")

    val card : String = scanner.next()
    logs.add(card)

    if (!definitionOfCards.containsKey(card)) {
        println("Can't remove \"$card\": there is no such card.")
        logs.add("Can't remove \"$card\": there is no such card.")
    } else {
        mistakeCounter.remove(card)
        nameOfCards.remove(definitionOfCards[card])
        definitionOfCards.remove(card)
        println("The card has been removed.")
        logs.add("The card has been removed.")
    }
}

fun import(scanner: Scanner, definitionOfCards: MutableMap<String, String>, nameOfCards: MutableMap<String, String>,
           mistakeCounter: MutableMap<String, Int>, logs: MutableList<String>) {
    var fileName: String
    println("File name:")
    logs.add("File name:")

    while (true) {
        fileName = scanner.nextLine()
        if (fileName.isNotEmpty()) break
    }
    logs.add(fileName)
    val inputStream: InputStream

    try {
        inputStream = File(fileName).inputStream()
    } catch(e: Exception) {
        println("File not found.")
        logs.add("File not found.")
        return
    }
    val lineList = mutableListOf<String>()

    inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) }}

    for (i in 0..lineList.lastIndex step 3) {
        nameOfCards.remove(definitionOfCards[lineList[i]])
        definitionOfCards[lineList[i]] = lineList[i + 1]
        mistakeCounter[lineList[i]] = lineList[i + 2].toInt()
        nameOfCards[lineList[i + 1]] = lineList[i]
    }

    println("${lineList.size/3} cards have been loaded.")
    logs.add("${lineList.size/3} cards have been loaded.")
}

fun export(scanner: Scanner, definitionOfCards: MutableMap<String, String>, mistakeCounter: MutableMap<String, Int>,
           logs: MutableList<String>) {
    var fileName: String
    println("File name:")
    logs.add("File name:")

    while (true) {
        fileName = scanner.nextLine()
        if (fileName.isNotEmpty()) break
    }
    logs.add(fileName)

    var forPrint: String = ""
    for (i in definitionOfCards) {
        forPrint += i.key + "\n" + i.value + "\n" + mistakeCounter[i.key] + "\n"
    }
    File(fileName).writeText(forPrint)

    println("${definitionOfCards.size} cards have been saved.")
    logs.add("${definitionOfCards.size} cards have been saved.")
}

fun ask(scanner: Scanner, definitionOfCards: MutableMap<String, String>, nameOfCards: MutableMap<String, String>,
        mistakeCounter : MutableMap<String, Int>, logs: MutableList<String>, e: Boolean) {
    val query : String = definitionOfCards.keys.random()
    println("Print the definition of \"$query\":")
    logs.add("Print the definition of \"$query\":")
    var answer : String
    if (e) answer = scanner.nextLine()

        answer = scanner.nextLine()

    logs.add(answer)
    if (definitionOfCards[query].equals(answer)) {
        println("Correct answer.")
        logs.add("Correct answer")
    } else {
        mistakeCounter[query] = mistakeCounter[query]!! + 1

        if (nameOfCards.containsKey(answer)) {
            println(
                    "Wrong answer. The correct one is \"${definitionOfCards[query]}\", you've just written the definition of" +
                            " \"${nameOfCards[answer]}\"."
            )
            logs.add(
                    "Wrong answer. The correct one is \"${definitionOfCards[query]}\", you've just written the definition of" +
                            " \"${nameOfCards[answer]}\"."
            )
        } else {
            println("Wrong answer. The correct one is \"${definitionOfCards[query]}\".")
            logs.add("Wrong answer. The correct one is \"${definitionOfCards[query]}\".")
        }
    }
}

fun asks(scanner: Scanner, definitionOfCards: MutableMap<String, String>, nameOfCards: MutableMap<String, String>,
         mistakeCounter: MutableMap<String, Int>, logs: MutableList<String>){
    println("How many times to ask?")
    logs.add("How many times to ask?")
    val numberOfAsks: Int = scanner.nextInt()
    logs.add(numberOfAsks.toString())
    var e: Boolean = true
    repeat(numberOfAsks) {
        ask(scanner, definitionOfCards, nameOfCards, mistakeCounter, logs, e)
        e = false
    }
}

fun hardestCard(logs: MutableList<String>, mistakeCounter: MutableMap<String, Int>) {
    var maxCount = 0
    var misLines = arrayListOf<String>()
    for (i in mistakeCounter) {
        if (i.value > maxCount) {
            misLines.clear()
            maxCount = i.value
            misLines.add(i.key)
        }
        else if (maxCount != 0 && i.value == maxCount) {
            misLines.add(i.key)
        }
    }
    if (maxCount == 0) {
        println("There are no cards with errors.")
        logs.add("There are no cards with errors.")
    } else {
        var misLine: String = ""
        misLine += "\"${misLines[0]}\""
        for (i in 1..misLines.lastIndex) {
            misLine += ", ${misLines[i]}"
        }
        println("The hardest card is $misLine. You have $maxCount errors answering it.")
        logs.add("The hardest card is $misLine. You have $maxCount errors answering it.")
    }
}

fun resetStats(mistakeCounter: MutableMap<String, Int>, logs: MutableList<String>) {
    mistakeCounter.forEach { mistakeCounter[it.key] = 0 }
    println("Card statistics has been reset.")
    logs.add("Card statistics has been reset.")
}

fun log(scanner: Scanner, logs: MutableList<String>) {
    println("File name:")
    logs.add("File name:")

    var fileName: String
    while (true) {
        fileName = scanner.nextLine()
        if (fileName.isNotEmpty()) break
    }
    logs.add(fileName)
    var result: String = ""
    logs.forEach { result += it + "\n" }
    File(fileName).writeText(result)
    println("The log has been saved.")
    logs.add("The log has been saved.")
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val definitionOfCards = mutableMapOf<String, String>()
    val nameOfCards = mutableMapOf<String, String>()
    val mistakeCounter = mutableMapOf<String, Int>()
    val logs = mutableListOf<String>()
    var inputFileName: String = ""
    var outputFileName: String = ""

    var last: String = ""
    for (str in args) {
        if (last == "-import") {
            inputFileName = str
        }
        else if (last == "-export") {
            outputFileName = str
        }
        last = str
    }

    if (inputFileName.isNotEmpty()) {
        val inputStream: InputStream
        inputStream = File(inputFileName).inputStream()

        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().useLines { lines -> lines.forEach { lineList.add(it) }}

        for (i in 0..lineList.lastIndex step 3) {
            nameOfCards.remove(definitionOfCards[lineList[i]])
            definitionOfCards[lineList[i]] = lineList[i + 1]
            mistakeCounter[lineList[i]] = lineList[i + 2].toInt()
            nameOfCards[lineList[i + 1]] = lineList[i]
        }
        println("${lineList.size/3} cards have been loaded.")
        logs.add("${lineList.size/3} cards have been loaded.")
    }

    loop@ while (true) {
        var type : String
        println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        logs.add("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")

        while (true) {
            type = scanner.nextLine()
            if (type.isNotEmpty()) break
        }
        logs.add(type)
        when (type) {
            "add" -> add(scanner, definitionOfCards, nameOfCards, mistakeCounter, logs)
            "remove" -> remove(scanner, definitionOfCards, nameOfCards, mistakeCounter, logs)
            "import" -> import(scanner, definitionOfCards, nameOfCards, mistakeCounter, logs)
            "export" -> export(scanner, definitionOfCards, mistakeCounter, logs)
            "ask" -> asks(scanner, definitionOfCards, nameOfCards, mistakeCounter, logs)
            "log" -> log(scanner, logs)
            "hardest card" -> hardestCard(logs, mistakeCounter)
            "reset stats" -> resetStats(mistakeCounter, logs)
            "exit" -> {
                println("Bye bye!")
                break@loop
            }
        }
        println()
    }

    if (outputFileName.isNotEmpty()) {
        var forPrint: String = ""
        for (i in definitionOfCards) {
            forPrint += i.key + "\n" + i.value + "\n" + mistakeCounter[i.key] + "\n"
        }
        File(outputFileName).writeText(forPrint)

        println("${definitionOfCards.size} cards have been saved.")
        logs.add("${definitionOfCards.size} cards have been saved.")
    }
}
