import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.StringBuilder

private const val black: Long = 0L
private const val white: Long = 1L
private const val left: Long = 0L
private const val right: Long = 1L

object Day11 {
    private val inputText = File("/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day11Input.txt").readText()

    fun part1() = println(paintShip().size)

    fun part2() {
        val ship = paintShip(white)

        val xs = ship.keys.map { it.first }
        val ys = ship.keys.map { it.second }

        val maxX = xs.maxOrNull()!!
        val minX = xs.minOrNull()!!

        val maxY = ys.maxOrNull()!!
        val minY = ys.minOrNull()!!

        val result = StringBuilder()

        (minY..maxY).reversed().forEach { y ->
            (minX..maxX).forEach { x ->
                result.append(ship.getCharacterRepresentation(Pair(x, y)))
            }

            result.append("\n")
        }

        println(result.toString())
    }

    private fun parseInput(): MutableMap<Long, Long> {
        val numbers = inputText
            .split(",")
            .map {
                it.toLong()
            }
        return numbers.indices.associate { index ->
            index.toLong() to numbers[index]
        } as MutableMap<Long, Long>

    }


    private fun paintShip(startingWith: Long = black) = runBlocking {
        val ship = mutableMapOf(Pair(0, 0) to 0L)
        val computer = IntCodeComputerMk2(parseInput())
        val robot = Robot()


        launch {
            computer.runProgram()
        }

        computer.sendInput(startingWith)

        while (!computer.output.isClosedForReceive) {
            val (color, direction) = computer.getColorAndDirection()

            ship[robot.position] = color

            robot.turnAndMove(direction)

            computer.sendInput(
                ship.getColorAt(robot.position)
            )
        }

        ship
    }

    private fun MutableMap<Pair<Int, Int>, Long>.getColorAt(point: Pair<Int, Int>) = getOrDefault(point, black)

    private fun MutableMap<Pair<Int, Int>, Long>.getCharacterRepresentation(point: Pair<Int, Int>) =
        if (getColorAt(point) == black) ' ' else '#'

    private suspend fun IntCodeComputerMk2.getColorAndDirection() = Pair(output.receive(), output.receive())

    private suspend fun IntCodeComputerMk2.sendInput(data: Long) = input.send(data)

}

data class Robot(
    var facing: ScreenDirection = ScreenDirection.North,
    var position: Pair<Int, Int> = Pair(0, 0)
) {

    fun turnAndMove(instruction: Long) {
        when (instruction) {
            left -> turnLeft()
            right -> turnRight()
            else -> throw IllegalStateException("Invalid direction")
        }

        moveForward()
    }

    private fun turnRight() {
        facing = facing.turnRight()
    }

    private fun turnLeft() {
        facing = facing.turnLeft()
    }

    private fun moveForward() {
        position = position.move(facing)
    }
}

fun Pair<Int, Int>.move(direction: ScreenDirection): Pair<Int, Int> {
    val (x, y) = this

    return when (direction) {
        ScreenDirection.North -> Pair(x, y + 1)
        ScreenDirection.East -> Pair(x + 1, y)
        ScreenDirection.South -> Pair(x, y - 1)
        ScreenDirection.West -> Pair(x - 1, y)
    }
}



enum class ScreenDirection {
    North, East, South, West;

    fun turnRight() = when (this) {
        North -> East
        East -> South
        South -> West
        West -> North
    }

    fun turnLeft() = when (this) {
        North -> West
        East -> North
        South -> East
        West -> South
    }
}
