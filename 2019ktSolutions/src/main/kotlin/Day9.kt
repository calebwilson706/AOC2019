import kotlinx.coroutines.runBlocking
import java.io.File

object Day9 {
    val inputText = File("/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day9Input.txt").readText()

    fun parseInput(): Map<Long, Long> {
        val numbers = inputText
            .split(",")
            .map {
                it.toLong()
            }
        return numbers.indices.associate { index ->
            index.toLong() to numbers[index]
        }

    }


    fun part1() {
        print(runComputer(1))
    }

    fun part2() {
        print(runComputer(2))
    }

    private fun runComputer(startState: Long): Long = runBlocking {
        IntCodeComputerMk2(parseInput().toMutableMap()).run {
            input.send(startState)
            runProgram()
            output.receive()
        }
    }
}