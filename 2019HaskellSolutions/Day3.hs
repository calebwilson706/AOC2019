module Day3 where

import Helpers

import qualified Data.Set as DS
import qualified Data.Map as DM

day3FilePath = "/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day3Input.txt"

parseInput :: String -> [[String]]
parseInput = map (splitBy ",") . lines

getPaths input = (head parsed, last parsed)
    where parsed = parseInput input

move :: (Int, Int) -> String  -> (Int, Int)
move origin = addTwoPoints origin . getOffset

getOffset :: String  -> (Int, Int)
getOffset (direction:numberString) = result
    where magnitude = read numberString :: Int
          result = case direction of
            'U' -> (0,magnitude)
            'R' -> (magnitude, 0)
            'D' -> (0,-magnitude)
            'L' -> (-magnitude, 0)

---part 1

moveAndUpdateVisited :: ((Int, Int), DS.Set (Int, Int)) -> String -> ((Int, Int), DS.Set (Int, Int))
moveAndUpdateVisited (origin, existingVisited) instruction = (image, newVisited)
    where image = move origin instruction
          newVisited = foldr DS.insert existingVisited (getAllPoints origin image)

getFinalVisited :: [String] -> DS.Set (Int, Int)
getFinalVisited = snd . foldl moveAndUpdateVisited ((0,0), DS.fromList [])

part1 :: String -> IO()
part1 input = print $ minimum $ DS.map distanceFromOrigin collisions
    where pathInstructions = getPaths input
          path1Distances = getFinalVisited $ fst pathInstructions
          path2Distances = getFinalVisited $ snd pathInstructions
          collisions = DS.intersection path1Distances path2Distances


---part 2 

moveAndUpdateMapOfSteps :: ((Int, Int), Int, DM.Map (Int, Int) Int) -> String -> ((Int, Int), Int, DM.Map (Int, Int) Int)
moveAndUpdateMapOfSteps (origin, stepsToOrigin, visitedSteps) instruction = (image, fst newMapOfSteps - 1, snd newMapOfSteps)
    where image = move origin instruction
          direction = head instruction
          isReverse = direction == 'D' || direction == 'L'
          (start, end) = if isReverse then (image, origin) else (origin, image)
          allPoints = getAllPoints start end
          pointsInOrder = if isReverse then reverse allPoints else allPoints
          newMapOfSteps = foldl (\(steps, acc) newPoint -> (steps + 1, DM.insertWith min newPoint steps acc )) (stepsToOrigin, visitedSteps) pointsInOrder

getFinalSteps :: [String] -> DM.Map (Int, Int) Int
getFinalSteps input = result
    where (_, _, result) = foldl moveAndUpdateMapOfSteps ((0,0), 0, DM.empty) input

part2 input = print closest
    where pathInstructions = getPaths input
          path1Distances = getFinalSteps $ fst pathInstructions
          path2Distances = getFinalSteps $ snd pathInstructions
          collisionsWithTotal = DM.intersectionWith (+) path1Distances path2Distances
          closest = minimum $ filter (/= 0) $ DM.elems collisionsWithTotal


main ::IO()
main = do inputString <- readFile day3FilePath
          part2 inputString