module Day8 where

import Helpers
import Data.Char(digitToInt)
import Data.List

day8FilePath :: [Char]
day8FilePath = "/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day8Input.txt"

data PixelValue = White | Black
    deriving (Show, Eq)

pixelValueToCharacter :: PixelValue -> Char
pixelValueToCharacter pixelValue = if pixelValue == White then '\x2588' else '.'

getPixelValueFromNumber :: Int -> PixelValue
getPixelValueFromNumber number = if number == 1 then White else Black

parseInput :: String -> [Int]
parseInput = map digitToInt

width :: Int
width = 25

height :: Int
height = 6

countZeros :: [Int] -> Int
countZeros = count (==0)

showPixelValues :: [PixelValue] -> [Char]
showPixelValues values = result
    where characterRepresentations = unflattenLayer $ map pixelValueToCharacter values
          result = foldl (\acc row -> acc ++ row ++ "\n") "" characterRepresentations


getLeastZerosLayer :: [Int] -> [Int] -> [Int]
getLeastZerosLayer x y = if x1 > y1 then y else x
    where x1 = countZeros x
          y1 = countZeros y

getFlatLayers :: [Int] -> [[Int]]
getFlatLayers  = chunks (height * width)

unflattenLayer :: [a] -> [[a]]
unflattenLayer = chunks width

minByCountOfZeros :: [[Int]] -> [Int]
minByCountOfZeros layers = foldr getLeastZerosLayer (head layers) layers

getVisable :: [Int] -> Int
getVisable = head . dropWhile (==2)

part1 :: [Int] -> IO ()
part1 numbers = print (oneCount * twoCount)
    where foundLayer = minByCountOfZeros $ getFlatLayers numbers
          oneCount = count (==1) foundLayer
          twoCount = count (==2) foundLayer

part2 :: [Int] -> IO ()
part2 numbers = putStrLn $ showPixelValues pixelValues
    where flatLayers = getFlatLayers numbers
          pixelArrays = transpose flatLayers
          pixelNumbers = map getVisable pixelArrays
          pixelValues = map getPixelValueFromNumber pixelNumbers

main :: IO()
main = do inputText <- readFile day8FilePath
          part2 $ parseInput inputText