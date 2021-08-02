import java.io.File

object Day6 {
    private val input = File("/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day6Input.txt").readText()

    fun part1() {
        println(
            countOrbits(input)
        )
    }

    fun part2() {
        val tree = formTree(input)
        val pathToSanta = tree.path("YOU", "SAN")

        print(pathToSanta.size - 3)
    }
}

fun countOrbits(string: String) : Int {
    val tree = formTree(string)
    val source = tree.getSource()

    return tree.countOrbits(0,source)
}

class OrbitTree {
    private val nodes = mutableMapOf<String, MutableList<String>>()

    fun addChild(parent : String, child : String) {
        val children = (nodes[parent] ?: listOf()).toMutableList()
        children.add(child)
        nodes[parent] = children
    }

    fun getSource() : String {
        val allChildren = nodes.values.flatten()
        val firstItems = nodes.keys.filter { !allChildren.contains(it) }

        return firstItems.first()
    }

    fun countOrbits(currentDepth : Int, startNode : String) : Int {
        val children = this.nodes[startNode] ?: return currentDepth

        var total = currentDepth

        children.forEach {
            total += this.countOrbits(currentDepth + 1, it)
        }

        return total
    }

    fun path(a : String, b : String): List<String> {
        val pathToA = pathTo(a)!!
        val pathToB = pathTo(b)!!
        val collision = pathToA.last { pathToB.contains(it) }
        val collisionPoint = pathToA.indexOf(collision)

        return pathToB.filter { !pathToA.contains(it) }.reversed() + pathToA.filterIndexed { index, _ -> index >= collisionPoint }
    }

    private fun pathTo(node : String) : List<String>? {
        fun pathFinder(currentPath : List<String>) : List<String>? {
            val workingPath = currentPath.toMutableList()
            val nextSteps = nodes[currentPath.last()] ?: return null

            nextSteps.forEach {
                val updated = workingPath + listOf(it)

                if (it == node) {
                    return updated
                }

                val foundPath = pathFinder(updated)

                if (foundPath != null) {
                    return foundPath
                }
            }

            return null
        }

        val starter = listOf(getSource())

        return if (starter[0] == node) {
            starter
        } else {
            pathFinder(starter)
        }
    }
}

fun formTree(input : String) : OrbitTree {
    val tree = OrbitTree()

    input.split("\n").forEach {
        val parts = it.split(")")
        tree.addChild(parts[0], parts[1])
    }

    return tree
}

