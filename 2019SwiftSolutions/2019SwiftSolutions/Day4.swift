//
//  Day4.swift
//  2019SwiftSolutions
//
//  Created by Caleb Wilson on 18/07/2021.
//

import Foundation


class Day4 {
    let inputRange = 272091 ... 815432
    
    func part1() {
        solution {
            $0.isValidPassword
        }
    }
    
    func part2() {
        solution {
            $0.isValidPassword && $0.part2Predicate
        }
    }
    
    func solution(predicate : (Int) -> Bool ) {
        print(inputRange.filter(predicate).count)
    }
}

extension Int {
    var isValidPassword : Bool {
        var containsDouble = false
        var onlyIncreasing = true
        
        let string = "\(self)"
        
        ( 1 ..< string.count ).forEach { index in
            let previous = string[index - 1]
            let current = string[index]
            
            if previous == current {
                containsDouble = true
            }
            
            if previous > current {
                onlyIncreasing = false
            }
            
        }
        
        return containsDouble && onlyIncreasing
    }
    
    var part2Predicate : Bool {
        let string = "\(self)"
        var doubles = [(Int, Character)]()
        
        ( 1 ..< string.count ).forEach {
            let current = string[$0]
            if  current == string[$0 - 1] {
                doubles.append(($0, current))
            }
        }
        
        return(!doubles.filter { (x, c) in
            !doubles.contains(where: {
                c == $0.1 && abs($0.0 - x) == 1
            })
        }.isEmpty)
    }
}
