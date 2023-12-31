package fp.exercise3_1

import java.nio.file.Files
import java.nio.file.Paths

data class Results(val id: Int, val name: String, val points: List<Int>)

enum class Grade {
    EXCELLENT, GOOD, SATISFACTORY, SUFFICIENT, INSUFFICIENT
}

private const val ID_INDEX = 0
private const val NAME_INDEX = 1
private const val ASSIGNMENT_START_INDEX = 2

private const val MINIMUM_POINTS = 3
private const val MINIMUM_TASKS_SOLVED = 8

fun main(args: Array<String>) {

    val lines: List<String> = Files.readAllLines(Paths.get("files/results.csv"))

    lines.forEachIndexed { index, s -> println("Line $index: $s") }
    println("\n")

    // Task 1: List of Results objects

    val resultList: List<Results> = lines.filter { it[0].isDigit() }.map {
        val elements: List<String> = it.split(",")
        Results(
            elements[ID_INDEX].toInt(),
            elements[NAME_INDEX],
            elements.slice(ASSIGNMENT_START_INDEX..<elements.size).map { points -> points.trim().toInt() })
    }
    println("resultList = $resultList \n")

    // Task 2: Number of solved tasks

    val nSolvedPerStnd: Map<String, Int> =
        resultList.associateBy({ it.name }) {
            it.points
                .filter { points -> points >= MINIMUM_POINTS }
                .size
        }
    println("nSolvedPerStnd = $nSolvedPerStnd \n")

// Task 3: Sufficient tasks solved
    val (suff: List<String>, notSuff: List<String>) =
        Pair(
            nSolvedPerStnd.filter { entry -> entry.value >= MINIMUM_TASKS_SOLVED }.map { it.key },
            nSolvedPerStnd.filterNot { entry -> entry.value >= MINIMUM_TASKS_SOLVED }.map { it.key })


    println("suffientSolved = $suff, not sufficient solved = $notSuff \n")

// Task 4: Grading

    val grades: Map<String, Grade> =
        resultList.associateBy({ it.name }) { computeGrade(it.points) }
    println("grades = $grades \n")

// Task 5: Grade statistics

    val nStudentsWithGrade: Map<Grade, Int> = resultList.groupingBy { computeGrade(it.points) }.eachCount()
    println("nStudentsWithGrade = $nStudentsWithGrade \n")

// Task 6: Number solved per assignment

    val nSolvedPerAssnmt: List<Pair<Int, Int>> = (1..10).map { index ->
        Pair<Int, Int>(
            index,
            resultList.filter { it.points[index - 1] >= MINIMUM_POINTS }
                .size
        )
    }
    println("nSolvedPerAssnmt = $nSolvedPerAssnmt \n")

}

fun computeGrade(points: List<Int>): Grade {
    val grade = if (points.filter { p -> p >= 3 }.count() < 8) Grade.INSUFFICIENT
    else {
        val avrg = points.sorted().drop(2).sum() / 8.0
        if (avrg < 5.0) Grade.INSUFFICIENT
        else if (avrg < 6.5) Grade.SUFFICIENT
        else if (avrg < 8.0) Grade.SATISFACTORY
        else if (avrg < 9.0) Grade.GOOD
        else Grade.EXCELLENT
    }
    return grade
}