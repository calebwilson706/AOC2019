module Helpers where

import Data.Char
import Data.List

import qualified Data.Text as T

getIntegerListFromStringList :: [String] -> [Int]
getIntegerListFromStringList = map read

getCircularIndex :: [a] -> Int -> Int
getCircularIndex list index = rem index (length list)

indices :: [a] -> [Int]
indices list = [0 .. length list - 1]

boolToInt :: Bool -> Int
boolToInt boolean = if boolean then 1 else 0

replaceValue :: Int -> a -> [a] -> [a]
replaceValue indexToReplace newValue originalList = header ++ [newValue] ++ drop 1 footer
    where (header, footer) = splitAt indexToReplace originalList

getAllPoints :: (Int,Int) -> (Int,Int) -> [(Int, Int)]
getAllPoints (minX,minY) (maxX, maxY) = [(x,y) | x <- [minX .. maxX], y <- [minY .. maxY]]

chunks :: Int -> [a] -> [[a]]
chunks _ [] = []
chunks n xs =
    let (ys, zs) = splitAt n xs
    in  ys : chunks n zs

digits :: Int -> [Int]
digits = map digitToInt . show

subsets :: (Eq t, Num t) => t -> [a] -> [[a]]
subsets 0 _ = [[]]
subsets _ [] = []
subsets n (x : xs) = map (x :) (subsets (n - 1) xs) ++ subsets n xs

replaceOccurences :: Eq b => b -> b -> [b] -> [b]
replaceOccurences x y = map (\e -> if e == x then y else e)

getFrequencyMapOfElements :: Ord a => [a] -> [(a, Int)]
getFrequencyMapOfElements xs = map (\x -> (head x, length x)) $ group $ sort xs

count :: (a -> Bool) -> [a] -> Int
count predicate = length . filter predicate

splitBy :: String -> String  -> [String ]
splitBy delimeter string =  map T.unpack $ T.splitOn (T.pack delimeter) (T.pack string)

addTwoPoints :: (Int, Int) -> (Int, Int) -> (Int, Int)
addTwoPoints (x1, y1) (x2, y2) = (x1 + x2, y1 + y2)

manhattanDistance :: (Int, Int) -> (Int, Int) -> Int
manhattanDistance (x1, y1) (x2, y2) = abs (x2 - x1) + abs (y2 - y1)

distanceFromOrigin :: (Int, Int) -> Int
distanceFromOrigin = manhattanDistance (0, 0)