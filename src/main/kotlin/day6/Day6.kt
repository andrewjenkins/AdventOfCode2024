package day6

import java.io.File

private const val INPUT_PATH = "src/main/resources/inputs/day6"

private data class Point(val x: Int, val y: Int) {
    operator fun plus(direction: Direction): Point {
        return Point(x + direction.x, y + direction.y)
    }
}

private enum class MapPoint {
    EMPTY,
    OBSTACLE,
    GUARD;


    companion object {
        fun fromSymbol(c: Char): MapPoint {
            return when (c) {
                '.' -> EMPTY
                '#' -> OBSTACLE
                '^' -> GUARD
                else -> throw IllegalArgumentException()
            }
        }
    }
}

private data class VisitedDirection(
    val point: Point,
    val direction: Direction,
)

private enum class Direction(val x: Int, val y: Int) {
    NORTH (0, -1),
    SOUTH (0, 1),
    EAST (1, 0),
    WEST (-1, 0);

    fun rotateNinetyDegrees(): Direction {
        return when (this) {
            NORTH -> EAST
            SOUTH -> WEST
            EAST -> SOUTH
            WEST -> NORTH
        }
    }
}

private fun loadInput(): List<List<MapPoint>> {
    val file = File(INPUT_PATH)
    return buildList {
        file.useLines { lines ->
            lines.forEach { line ->
                add(line.trim().toMapPoints())
            }
        }
    }
}

private fun String.toMapPoints(): List<MapPoint> {
    return toCharArray().map { MapPoint.fromSymbol(it) }
}

private fun determineVisited(map: List<List<MapPoint>>): Array<Array<Boolean>> {
    var guardPoint = map.findGuardPoint()
    val visited: Array<Array<Boolean>> = Array(map.size) { Array (map[0].size) { false } }
    var direction = Direction.NORTH
    while (guardPoint.inBounds(map[0].size, map.size)) {
        visited[guardPoint.y][guardPoint.x] = true
        var maybeNextPoint = guardPoint + direction
        while (maybeNextPoint.inBounds(map[0].size, map.size) &&
            map[maybeNextPoint.y][maybeNextPoint.x] == MapPoint.OBSTACLE) {
            direction = direction.rotateNinetyDegrees()
            maybeNextPoint = guardPoint.plus(direction)
        }
        guardPoint = maybeNextPoint
    }
    return visited
}

private fun List<List<MapPoint>>.findGuardPoint(): Point {
    lateinit var guardPoint: Point
    indices.forEach { y ->
       get(0).indices.forEach { x ->
            if (this[y][x] == MapPoint.GUARD) {
                guardPoint = Point(x, y)
            }
        }
    }
    return guardPoint
}



private fun Array<Array<Boolean>>.countVisited() = flatten().count { it }

private fun Point.inBounds(width: Int, height: Int): Boolean {
    return !(x >= width || x < 0 || y >= height || y < 0)
}

private fun countObstructions(map: List<List<MapPoint>>): Int {
    val potentialObstaclePoints: List<Point> = map.findPotentialObstaclePoints()
    return potentialObstaclePoints.map { map.addObstacle(it) }.count { it.hasCycle() }
}

private fun List<List<MapPoint>>.addObstacle(point: Point): List<List<MapPoint>> {
    val obstacleRow = buildList {
        addAll(this@addObstacle[point.y].subList(0, point.x))
        add(MapPoint.OBSTACLE)
        addAll(this@addObstacle[point.y].subList(point.x + 1, this@addObstacle.size))
    }.also { check(it.size == this@addObstacle[point.y].size)}

    return buildList {
        addAll(this@addObstacle.subList(0, point.y))
        add(obstacleRow)
        addAll(this@addObstacle.subList(point.y + 1, this@addObstacle.size))
    }.also {
        check(it.flatten().size == this@addObstacle.flatten().size)
        check(it.flatten().contains(MapPoint.GUARD))
    }
}

private fun List<List<MapPoint>>.findPotentialObstaclePoints(): List<Point>  {
    val initialVisited = determineVisited(this)
    return buildList {
        this@findPotentialObstaclePoints.indices.forEach { y ->
            this@findPotentialObstaclePoints[0].indices.forEach { x ->
                if (initialVisited[y][x] && this@findPotentialObstaclePoints[y][x] == MapPoint.EMPTY) {
                    this@buildList.add(Point(x, y))
                }
            }
        }
    }
}

private fun List<List<MapPoint>>.hasCycle(): Boolean {
    var guardPoint = findGuardPoint()
    val visited: MutableSet<VisitedDirection> = mutableSetOf()
    var direction = Direction.NORTH
    while (guardPoint.inBounds(this[0].size, this.size)) {
        val visitedEntry = VisitedDirection(guardPoint, direction)
        if (visited.contains(visitedEntry)) {
            return true
        }
        visited.add(visitedEntry)
        var maybeNextPoint = guardPoint + direction
        while (maybeNextPoint.inBounds(this[0].size, this.size) &&
            this[maybeNextPoint.y][maybeNextPoint.x] == MapPoint.OBSTACLE) {
            direction = direction.rotateNinetyDegrees()
            maybeNextPoint = guardPoint.plus(direction)
        }
        guardPoint = maybeNextPoint
    }
    return false
}

fun main() {
    val map = loadInput()
    val initialVisited = determineVisited(map)
    val obstructions = countObstructions(map)
    println("Part 1 result: ${initialVisited.countVisited()}")
    println("Part 2 result: $obstructions")
}