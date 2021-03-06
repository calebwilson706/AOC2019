import java.io.File
import java.util.*

object Day5 {

    private val input = File("/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day5Input.txt").readText()

    fun part1() {
        var currentIndex = 0
        val map = parseInput().toMutableMap()
        val output = Stack<Int>()

        while (true) {
            val currentOpcode = map[currentIndex]!!

            when (currentOpcode.toString().last().asInt()) {
                1, 2 -> {
                    map.applyThreeParameterOpcode(
                        currentOpcode,
                        map[currentIndex + 1]!!,
                        map[currentIndex + 2]!!,
                        map[currentIndex + 3]!!
                    )
                    currentIndex += 4
                }
                3 -> {
                    map.applyOpcode3(currentOpcode, map[currentIndex + 1]!!, 1)
                    currentIndex += 2
                }
                4 -> {
                    output.applyOpcode4(currentOpcode, map[currentIndex + 1]!!, map)
                    currentIndex += 2
                }
                9 -> break
            }


        }

        println(output)
    }

    fun part2() {
        println(
            IntcodeComputer(input).carryOutProgram(mutableListOf(5))
        )
    }

    private fun MutableMap<Int, Int>.applyThreeParameterOpcode(intCode : Int, first : Int, second : Int, third : Int) {
        val (firstModeKey, secondModeKey) = getParameterModesForThreeArgumentOperation(intCode)

        val firstValue = this.getValue(firstModeKey, first)
        val secondValue = this.getValue(secondModeKey, second)

        this[third] = getThreeParameterOperation(intCode)(firstValue, secondValue)
    }

    private fun MutableMap<Int, Int>.shouldJump(intCode : Int, first : Int): Boolean {
        val (parameterMode, _) = getTwoParameterAccessModes(intCode)
        val opcode = intCode.toString().last().asInt()
        val parameter = this.getValue(parameterMode, first)
        val isTrue = parameter != 0

        return if (opcode == 5) {
            isTrue
        } else {
            !isTrue
        }
    }

    private fun getTwoParameterAccessModes(intCode : Int) : Pair<Int, Int> {
        return getParameterModesForThreeArgumentOperation(intCode)
    }

    private fun parseInput(): MutableMap<Int, Int> {
        val map = mutableMapOf<Int, Int>()

        input.split(",").forEachIndexed { index, code ->
            map[index] = code.toInt()
        }

        return map
    }

    private fun getParameterModesForThreeArgumentOperation(intCode : Int) : Pair<Int, Int> {

        if (intCode < 10) {
            return 0 to 0
        }

        val codeAsString = intCode.toString().dropLast(2)

        return when (codeAsString.length) {
            1 -> {
                1 to 0
            }
            else -> {
                codeAsString[1].asInt() to codeAsString[0].asInt()
            }
        }
    }

    private fun MutableMap<Int, Int>.applyOpcode3(intCode : Int, argument : Int, input : Int) {
        this[argument] = input
    }

    private fun Stack<Int>.applyOpcode4(intCode : Int, argument : Int, map : Map<Int, Int>) {
        if (intCode == 14) {
            this.push(argument)
        } else {
            this.push(map[argument]!!)
        }
    }

    fun Map<Int, Int>.getValue( parameterModeNumber : Int, value : Int): Int {
        return when (getParameterMode(parameterModeNumber)) {
            ParameterMode.Position -> this[value]!!
            ParameterMode.Value -> value
        }
    }

    private fun getThreeParameterOperation(intCode : Int) : (Int, Int) -> Int {
        return when (intCode.toString().last()) {
            '1' -> {
                ::add
            }
            '2' -> {
                ::multiply
            }
            '7'-> {
                ::lessThan
            }
            else -> {
                ::equalTo
            }
        }
    }

    private fun add(a : Int, b : Int) = a + b

    private fun multiply(a : Int, b : Int) = a * b

    private fun lessThan(a : Int, b : Int) = if (a < b) 1 else 0

    private fun equalTo(a : Int, b : Int) = if (a == b) 1 else 0

    private fun getParameterMode(number : Int) = if (number == 0) ParameterMode.Position else ParameterMode.Value

}
