module Day1 where

day1FilePath = "/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day1Input.txt"

parseInput :: String -> [Int]
parseInput = map (\a -> read a::Int) . lines

getFuelRequired :: Int -> Int 
getFuelRequired num = -2 + div num 3  

getFuelRequiredRecursiveHelper :: Int -> Int -> Int
getFuelRequiredRecursiveHelper mass total = result
    where localAnswer = getFuelRequired mass
          result
            | localAnswer <= 0 = total
            | otherwise = getFuelRequiredRecursiveHelper localAnswer (total + localAnswer)
        
getFuelRequiredRecursive :: Int -> Int
getFuelRequiredRecursive = flip getFuelRequiredRecursiveHelper 0

solution :: (Int -> Int) -> String -> IO()
solution calculator = print . foldl (\acc next -> acc + calculator next) 0 . parseInput

part1 :: String -> IO ()
part1 = solution getFuelRequired

part2 :: String -> IO ()
part2 = solution getFuelRequiredRecursive


main ::IO()
main = do inputString <- readFile day1FilePath
          part2 inputString
