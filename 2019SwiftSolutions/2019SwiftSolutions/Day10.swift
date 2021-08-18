//
//  Day10.swift
//  2019SwiftSolutions
//
//  Created by Caleb Wilson on 17/08/2021.
//

import Foundation
import PuzzleBox
import Collections

class Day10 : PuzzleClass {
    init() {
        super.init(filePath: "/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day10Input.txt")
    }
    
    func part1() {
        let map = AsteroidMap(mapString: inputStringUnparsed!)
        map.part1()
    }
    
    func part2() {
        let map = AsteroidMap(mapString: inputStringUnparsed!)
        map.part2()
    }
}

class AsteroidMap {
    let map: [[Character]]
    
    init(mapString: String) {
        self.map = mapString.components(separatedBy: .newlines).map { line in
            [Character](line)
        }
    }
    
    func part1() {
        let allPoints = getAllPoints()
        
        print(getAsteroidPoints().map {
            countAsteroids(start: $0, allPoints: allPoints)
        }.max() ?? "")
    }
    
    func part2() {
        let asteroid = getDestroyedAsteroid(n: 200)
        print(asteroid.x*100 + asteroid.y)
    }
    
    func getDestroyedAsteroid(n: Int) -> Point {
        let laserLocation = getLocationOfStation()
        var asteroidsGroupedByGradient = groupPointsBySimplifiedOffsets(start: laserLocation, allPoints: getAsteroidPoints())
        var asteroidGradientsQueue = Deque(getGradientsSortedByLaserOrder(gradients: asteroidsGroupedByGradient.keys.map { $0 }))
        var destroyedAsteroids = Deque<Point>()
        
        while let gradient = asteroidGradientsQueue.popFirst() {
            if let nextAsteroid = asteroidsGroupedByGradient[gradient]?.first {
                asteroidsGroupedByGradient[gradient]!.removeFirst()
                destroyedAsteroids.append(nextAsteroid)
                asteroidGradientsQueue.append(gradient)
            }
            
            if destroyedAsteroids.count == n {
                break
            }
        }
        
        return(destroyedAsteroids.last!)
    }
    
    func getGradientsSortedByLaserOrder(gradients: [Point]) -> [Point] {
        gradients.sorted(by: {
            getClockwiseAngle(point: $0) < getClockwiseAngle(point: $1)
        })
    }
    
    
    func toDegrees(n: Double) -> Double {
        return((n/3.141592653589793) * 180)
    }

    func getClockwiseAngle(point: Point) -> Double {
        let x = point.x
        let y = -point.y
        
        let arg = toDegrees(n: atan2(Double(x), Double(y)))
        
        if x < 0 {
            return 360 + arg
        }
        
        return arg
    }
    
    func getLocationOfStation() -> Point {
        let allPoints = getAllPoints()
        
        return getAsteroidPoints().max {
            countAsteroids(start: $0, allPoints: allPoints) < countAsteroids(start: $1, allPoints: allPoints)
        }!
    }
    
    func getAllPoints() -> Set<Point> {
        var result = Set<Point>()
        
        map.indices.forEach { y in
            map[y].indices.forEach { x in
                result.insert(Point(x: x, y: y))
            }
        }
        
        return result
    }
    
    func getAsteroidPoints() -> Set<Point> {
        getAllPoints().filter{ point in
            map[point.y][point.x].isAsteroid
        }
    }
    
    func countAsteroids(start: Point, allPoints: Set<Point>) -> Int {
        var pointsToCheck = getQueueOfPointsToCheck(start: start, allPoints: allPoints)
        var crossedOffPoints = Set<Point>([start])
        var total = 0
        
        while let nextPointAndOffset = pointsToCheck.popFirst() {
            let nextPoint = nextPointAndOffset.point
            
            if crossedOffPoints.contains(nextPoint) {
                continue
            }
            
            if map[nextPoint.y][nextPoint.x].isAsteroid {
                total += 1
                crossedOffPoints = crossedOffPoints.union(getNewCrossedOffPoints(asteroid: nextPointAndOffset, allPoints: allPoints))
            }
        }
        
        return total
    }
    
    func getNewCrossedOffPoints(asteroid: PointWithOffset, allPoints: Set<Point>) -> Set<Point> {
        var result = Set<Point>()
        var currentPoint = asteroid.point
        
        let offset = asteroid.offset.simplifyOffset()
        
        while allPoints.contains(currentPoint) {
            result.insert(currentPoint)
            currentPoint = currentPoint + offset
        }
        
        return result
    }

    func groupPointsBySimplifiedOffsets(start: Point, allPoints: Set<Point>) -> [Point : [Point]] {
        var result = [Point : [Point]]()
        let pointsAndOffsets = getSortedPointsAndOffset(start: start, allPoints: getAsteroidPoints())
        
        pointsAndOffsets.forEach {
            let simplifiedOffset = $0.value.simplifyOffset()
            let arrayForOffset = (result[simplifiedOffset] ?? []) + [$0.key]
            result[simplifiedOffset] = arrayForOffset
        }
        
        return result
    }
    
    func getQueueOfPointsToCheck(start: Point, allPoints: Set<Point>) -> Deque<PointWithOffset> {
        var result = Deque<PointWithOffset>()
        
        getSortedPointsAndOffset(start: start, allPoints: allPoints).forEach { (point, offset) in
            result.append(PointWithOffset(point: point, offset: offset))
        }
        
        return result
    }
    
    func getSortedPointsAndOffset(start: Point, allPoints: Set<Point>) -> [Dictionary<Point, Point>.Element] {
        getOffsets(from: start, to: allPoints).sorted(by: {
            let (point1, _) = $0
            let (point2, _) = $1
            
            return point1.manhattanDistance(from: start) < point2.manhattanDistance(from: start)
        })
    }
    

    func getOffsets(from start: Point, to allPoints: Set<Point>) -> [Point : Point] {
        var result = [Point : Point]()
        
        allPoints.forEach { point in
            result[point] = getOffset(from: start, to: point)
        }
        
        return result
    }
    
    func getOffset(from: Point, to: Point) -> Point {
        Point(x: to.x - from.x, y: to.y - from.y)
    }
    
    struct PointWithOffset {
        let point: Point
        let offset: Point
    }
}

extension Character {
    var isAsteroid : Bool {
        self == "#"
    }
}

extension Point {
    mutating func makeXNegative() {
        self.x = -self.x
    }
    
    mutating func makeYNegative() {
        self.y = -self.y
    }
    
    static func +(lhs: Point, rhs: Point) -> Point {
        Point(x: lhs.x + rhs.x, y: lhs.y + rhs.y)
    }
    
    func simplifyOffset() -> Point {
        var result = Point(x: 0, y: 0)
        
        if (x == 0){
            result = Point(x: 0, y: 1)
        } else if (y == 0){
            result = Point(x: 1, y: 0)
        } else {
            result = simplifyRatioIntoPoint(x, y)
        }
        
        if (x < 0){
            result.makeXNegative()
        }
        
        if (y < 0){
            result.makeYNegative()
        }
        
        return result
    }
    
}

func greatestCommonFactor(_ a: Int, _ b: Int) -> Int {
  let r = a % b
  if r != 0 {
    return greatestCommonFactor(b, r)
  } else {
    return b
  }
}

func simplifyRatioIntoPoint(_ x : Int, _ y : Int) -> Point {
    let absX = abs(x)
    let absY = abs(y)
    let greatestCommonFactor = greatestCommonFactor(absX, absY)
    return Point(x: absX/greatestCommonFactor, y: absY/greatestCommonFactor)
}
