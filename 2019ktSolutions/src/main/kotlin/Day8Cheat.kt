import java.io.File

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*


fun <T> permute(input: List<T>): List<List<T>> {
    if (input.size == 1) return listOf(input)
    val perms = mutableListOf<List<T>>()
    val toInsert = input[0]
    for (perm in permute(input.drop(1))) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, toInsert)
            perms.add(newPerm)
        }
    }
    return perms
}

suspend fun compute(input: Channel<Int>?, output: Channel<Int>?, done: Channel<Int>?) {
    while(true) {
        val code = ArrayList<Int>()
        File("/Users/calebjw/Documents/Developer/AdventOfCode/2019/Inputs/Day8Input.txt").readLines().joinToString().split(",").forEach { code.add(it.toInt()) }
        for (i in 0..4) code.add(0)

        fun get(mode: String,pos: Int) =
            if (mode=="1") code[pos] else code.get(code.get(pos))

        var pc = 0
        while (pc <= code.size ) {
            val instr = (code[pc] +100000).toString()
            val op= instr.substring(4,6)
            val c = instr.substring(3,4)
            val b = instr.substring(2,3)
            //var a = instr.substring(1,2)
            //println("OP=${instr}:${a}:${b}:${c}:${op}")
            when(op) {
                "01" -> {
                    code[code[pc+3]] = get(c,pc+1)+get(b,pc+2); pc+=4; } // add
                "02" -> {
                    code[code[pc+3]] = get(c,pc+1)*get(b,pc+2); pc+=4; } // multiply
                "03" -> {
                    code[code[pc+1]] = input!!.receive(); pc+=2; } // input
                "04" -> { output!!.send(get(c,pc+1)); pc+=2; } // output
                "05" -> { if (get(c,pc+1)!=0) pc=get(b,pc+2) else pc+=3; } // jump-if-true
                "06" -> { if (get(c,pc+1)==0) pc=get(b,pc+2) else pc+=3; } // jump-if-false
                "07" -> { if (get(c,pc+1)< get(b,pc+2)) code.set(code.get(pc+3),1) else code.set(code.get(pc+3),0); pc+=4; } // less than
                "08" -> { if (get(c,pc+1)==get(b,pc+2)) code.set(code.get(pc+3),1) else code.set(code.get(pc+3),0); pc+=4; } // equals
            }
            if (op=="99") {
                if (done!=null) { done.send(1); return; } // stop signal
                break // restart from scratch
            }
        }
    }
}

fun exec(sequence: List<Int>): Int {
    val wire: Array<Channel<Int>?> = arrayOfNulls<Channel<Int>>(7)
    wire.forEachIndexed { i, _ ->
        wire[i] = Channel<Int>(10)
    }
    GlobalScope.launch {
        sequence.forEachIndexed { i, phase ->
            wire[i]!!.send(phase)
            if (i==0) wire[i]!!.send(0)
        }
    }

    var ans = 0
    sequence.forEachIndexed { i, _ ->
        GlobalScope.launch {
            compute( wire[i], wire[(i+1)%sequence.size],
                if(i==0) wire[wire.size-1] else null )
        }
    }
    runBlocking {
        var wait = wire[wire.size-1]!!.receive()
        ans = wire[0]!!.receive()
    }
    return ans
}

fun run(sequence: List<Int>): Int {
    var max = 0
    permute<Int>(sequence).forEach {
        var thrust = exec(it)
        if (thrust>max) {
            max = thrust
        }
    }
    return max
}

fun main() {
    //println("Answer part 1: " + run(listOf<Int>(0, 1, 2, 3, 4))); // Answer part 1: 199988
    println("Answer part 2: " + run(listOf<Int>(5, 6, 7, 8, 9))); // Answer part 2: 17519904
}