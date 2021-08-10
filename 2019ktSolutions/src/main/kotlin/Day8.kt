import java.io.File
import java.util.Collections.swap
import kotlin.math.max

object Day8 {
    val computer = IntcodeComputer(File("/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day8Input.txt").readText())

    fun part1() {
        println(listOf(0,1,2,3,4).permutations().map { thrusterSequence ->
            var secondInput = 0
            var result = 0

            thrusterSequence.forEach {
                secondInput = computer.carryOutProgram(mutableListOf(it, secondInput))!!
                result = max(secondInput, result)
            }

            result
        }.maxOrNull())
    }

    fun part2() {
        println(listOf(5,6,7,8,9).permutations().map { thrusterSequence ->
            var secondInput = 0
            var result = 0

            thrusterSequence.forEach {
                secondInput = computer.carryOutProgram(mutableListOf(it, secondInput))!!
                result = max(secondInput, result)
            }

            result
        }.maxOrNull())
    }


    fun <V> List<V>.permutations(): List<List<V>> {
        val retVal: MutableList<List<V>> = mutableListOf()

        fun generate(k: Int, list: List<V>) {
            // If only 1 element, just output the array
            if (k == 1) {
                retVal.add(list.toList())
            } else {
                for (i in 0 until k) {
                    generate(k - 1, list)
                    if (k % 2 == 0) {
                        swap(list, i, k - 1)
                    } else {
                        swap(list, 0, k - 1)
                    }
                }
            }
        }

        generate(this.count(), this.toList())
        return retVal
    }
}