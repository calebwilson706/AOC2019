//
//  Day2.swift
//  2019SwiftSolutions
//
//  Created by Caleb Wilson on 05/07/2021.
//

import Foundation
import PuzzleBox

class Day2 : PuzzleClass {
    
    init() {
        super.init(filePath: "/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day2Input.txt")
    }
    
    var numberList : [Int] {
        inputStringUnparsed!.components(separatedBy: ",").map { Int($0)! }
    }
    
    func part1() {
        print(carryOutProgramAndGetFirst(noun: 12, verb: 2, numbers: numberList))
    }
    
    func part2() {
        let result = findPair(target: 19690720)!
        
        print(100 * result.noun + result.verb)
    }
    
    func findPair(target : Int) -> (noun : Int, verb : Int)? {
        let range = 0...99
        let startList = numberList
        
        for noun in range {
            for verb in range {
                if carryOutProgramAndGetFirst(noun: noun, verb: verb, numbers: startList) == target {
                    return(noun,verb)
                }
            }
        }
        
        return nil
    }
    
    func carryOutProgramAndGetFirst(noun : Int, verb : Int, numbers : [Int]) -> Int {
        var numbers = numbers
        
        numbers[1] = noun
        numbers[2] = verb
        
        var currentIndex = 0
        
        while numbers[currentIndex] != 99 {
            let opcode = numbers[currentIndex]
            applyOpcode(startIndex: currentIndex, numbers: &numbers, sign: opcode == 1 ? (+) : (*))
            currentIndex += 4
        }
        
        return numbers[0]
    }
    
    func applyOpcode(startIndex : Int, numbers : inout [Int], sign: (Int,Int) -> Int) {
        let calculation = sign(numbers.indirectAccess(index: startIndex + 1), numbers.indirectAccess(index: startIndex + 2))
        numbers[numbers[startIndex + 3]] = calculation
    }
}

extension Collection where Element == Int, Index == Int {
    func indirectAccess(index : Int) -> Int {
        self[self[index]]
    }
}
