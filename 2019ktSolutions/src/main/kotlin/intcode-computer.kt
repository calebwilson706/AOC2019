import java.io.File
import java.util.*



class IntcodeComputer(private val inputText: String) {

    fun feedbackLoopProgram(startIndex : Int,startValues : MutableMap<Int, Int>?, inputSequence: MutableList<Int>): Int? {
        var currentIndex = startIndex
        val map = startValues ?: parseInput().toMutableMap()
        var inputIndex = 0
        val output = Stack<Int>()

        while (true) {
            val currentOpcode = map[currentIndex]!!


            val opcodeFinalChar = currentOpcode.toString().last().asInt()

            if (opcodeFinalChar == 9) {
                break
            }

            val firstParameter = map[currentIndex + 1]!!
            val secondParameter = map[currentIndex + 2]!!

            when (opcodeFinalChar) {
                1, 2, 7, 8 -> {
                    map.applyThreeParameterOpcode(
                        currentOpcode,
                        firstParameter,
                        secondParameter,
                        map[currentIndex + 3]!!
                    )
                    currentIndex += 4
                }
                3 -> {
                    map.applyOpcode3(currentOpcode, firstParameter, inputSequence[inputIndex % inputSequence.size])
                    inputIndex += 1
                    currentIndex += 2
                }
                4 -> {
                    output.applyOpcode4(currentOpcode, firstParameter, map)
                    currentIndex += 2

                }

                5,6 -> {
                    if (map.shouldJump(currentOpcode, firstParameter)) {
                        val (_, parameterMode) = getTwoParameterAccessModes(currentOpcode)
                        currentIndex = map.getValue(parameterMode, secondParameter)
                    } else {
                        currentIndex += 3
                    }
                }
            }


        }

        return output.pop()
    }

    fun carryOutProgram(inputSequence: MutableList<Int>): Int? {
        var currentIndex = 0
        val map = parseInput().toMutableMap()
        var inputIndex = 0
        val output = Stack<Int>()

        while (true) {
            val currentOpcode = map[currentIndex]!!


            val opcodeFinalChar = currentOpcode.toString().last().asInt()

            if (opcodeFinalChar == 9) {
                break
            }

            val firstParameter = map[currentIndex + 1]!!
            val secondParameter = map[currentIndex + 2]!!

            when (opcodeFinalChar) {
                1, 2, 7, 8 -> {
                    map.applyThreeParameterOpcode(
                        currentOpcode,
                        firstParameter,
                        secondParameter,
                        map[currentIndex + 3]!!
                    )
                    currentIndex += 4
                }
                3 -> {
                    map.applyOpcode3(currentOpcode, firstParameter, inputSequence[inputIndex % inputSequence.size])
                    inputIndex += 1
                    currentIndex += 2
                }
                4 -> {
                    output.applyOpcode4(currentOpcode, firstParameter, map)

                    currentIndex += 2
                }

                5,6 -> {
                    if (map.shouldJump(currentOpcode, firstParameter)) {
                        val (_, parameterMode) = getTwoParameterAccessModes(currentOpcode)
                        currentIndex = map.getValue(parameterMode, secondParameter)
                    } else {
                        currentIndex += 3
                    }
                }
            }


        }

        return output.pop()
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

        inputText.split(",").forEachIndexed { index, code ->
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

enum class ParameterMode {
    Position, Value
}

fun Char.asInt() = toString().toInt()