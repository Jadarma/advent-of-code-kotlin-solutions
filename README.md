# Advent of Code Kotlin - Project Template

This is a self-contained starter project template for solving [Advent of Code](https://adventofcode.com/) puzzles in 
Kotlin!
It uses [Kotest](https://kotest.io) and [AocKt](https://github.com/Jadarma/advent-of-code-kotlin) to minimize 
boilerplate and provide a smooth development experience.

## 📁 Project Structure

This is a simple Gradle project, with slightly modified source-set directories to specialize it for use in AoC.
For each puzzle, you will have five files, distributed as such:

```text
projectDir
├── inputs
│  └── aockt
│     └── y2015
│        └── d01
│           └── input.txt
│           └── solution_part1.txt
│           └── solution_part2.txt
├── solutions
│  └── aockt
│     └── y2015
│        └── Y2015D01.kt
└── tests
   └── aockt
      └── y2015
         └── Y2015D01Test.kt
```

There are a few simple key points here:
- Solution implementations go into `solutions`.
  While it is not a requirement to split them into packages by year or have a specific naming convention for the class
  names, doing so helps with organising.
- Unit tests go into `tests` and should have the same structure as the `solutions` source set.
- Input data goes into `inputs`.
  The format for these ***is enforced*** and needs to be followed:
    - The directory for each day has the base path: `/aockt/y[year]/d[zeroPaddedDay]`
    - In that directory, the input and the solutions go in the respective text files: 
      `input.txt`, `solution_part1.txt`, and `solution_part2.txt`.
    - If a solution is not yet known, it can be skipped.

**NOTE: Please do NOT commit your puzzle inputs!**

The `inputs` files are git ignored by default to prevent accidental commits.
However, this means that you would have to re-download your inputs and solutions every time you clone from a 
different machine.
To avoid this, consider using something like [`git-crypt`](https://github.com/AGWA/git-crypt) to make your inputs 
available to you alone, while still allowing them to be checked in on a public repository.

## 📝 Workflow Example

This section will demonstrate how you would use this project when solving new puzzles.
For example, to implement the first ever AoC puzzle, you would do the following.

**Step 1: Create your Solution Stub**

Create a `class` or `object` for your solution that implements the `Solution` type.
There is no requirement as to how to name it or where to put it, but a good convention is to split them by year as a 
package, and name them in the `YxxxxDxx` format.

Create the following class in your `solutions` directory:

```kotlin
package aockt.y2015

import io.github.jadarma.aockt.core.Solution

object Y2015D01 : Solution
```

Following the same logic, create the following class in your `tests` directory:

```kotlin
package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 1, "Not Quite Lisp")
class Y2015D01Test : AdventSpec<Y2015D01>({})
```

Note that the `@AdventDay` annotation is required so that the test knows where to look for puzzle inputs in step 4.

**Step 2: Define your Test Cases**

Read the puzzle text from the AoC website.
If it gives you example inputs and outputs (or if you come up with others as you develop), you can define them as test
cases using the `shouldOutput` and `shouldAllOutput` helper functions.

```kotlin
package aockt.y2015

import io.github.jadarma.aockt.test.AdventDay
import io.github.jadarma.aockt.test.AdventSpec

@AdventDay(2015, 1, "Not Quite Lisp")
class Y2015D01Test : AdventSpec<Y2015D01>({
    partOne {
        listOf("(())", "()()") shouldAllOutput 0
        listOf("(((", "(()(()(", "))(((((") shouldAllOutput 3
        listOf("())", "))(") shouldAllOutput -1
        listOf(")))", ")())())") shouldAllOutput -3
    }
})
```

**Step 3: Implement your solution**

Implement the `partOne` function.
When you [run](#-running-your-code) your test, your solution will be validated against the examples you defined.

```kotlin
package aockt.y2015

import io.github.jadarma.aockt.core.Solution

object Y2015D01 : Solution {
    override fun partOne(input: String) = 42 /* No Spoilers! */
}
```

**Step 4: Test against real input**

Download your puzzle input from the AoC website and place it in your `inputs` directory.
This is the only place where structure is enforced! Following the [project structure](#-project-structure), it should be
added in `inputs/aockt/y2015/d01/input.txt`.

If you run the test again, you will see a new test group called _"The solution"_, with two tests inside it: _"Is 
correct"_ and _"Is reasonably efficient"_.
Since we don't know the correct solution yet, the correctness test will be greyed out, with your unverified answer 
in parens.

If the examples pass, it's a good chance that is the correct answer!
Submit it to the AoC website and earn your star!

**Step 5: Record the solution in your inputs and refactor!**

Once you have the correct solution, add it in `inputs/aockt/y2015/d01/solution_part1.txt`.
Now the tests will know what the correct answer for your `input.txt` is and allow you to refactor your code while 
constantly checking that your code still works as intended.

**Step 6: Repeat steps 2-5 for the second part**

Move on to the second part.
Since you are focusing on `partTwo` now, you can temporarily ignore all tests for `partOne`, just to speed up the 
feedback loop.

```kotlin
partOne(enable = false) { /* ... */ }
```

**BONUS: Isolated Debug Run**

For more complex puzzles, you might find yourself unable to determine why your solution behaves differently, and you 
want to employ the debugger.
Since the `AdventSpec` is just a fancy `FunSpec`, you can simply define a standalone test, which you can 
[run](#from-intellij-recommended) on its own in debug mode using the gutter icon.

```kotlin
class Y2015D01Test : AdventSpec<Y2015D01>({
    test("debug") {
        solution.partOne("some input")
    }
})
```

## ▶️ Running your Code

### From IntelliJ (Recommended)

If you have the [Kotest Plugin](https://plugins.jetbrains.com/plugin/14080-kotest) installed, the IDE will show a 
'Run' gutter icon near any `AdventSpec` in your code.
After that, you can re-run the same test using `Ctrl+F5`, or pressing the _Rerun_ button in the test tool window.
This provides the most comfortable feedback loop when solving new problems.

### From Gradle / CLI

You can also run your tests from Gradle.

To run a specific test, pass it via its FQN:

```shell
./gradlew test --tests 'aockt.y9999.Y9999D01Test'
```

You may also filter and run tests in bulk.
For example, running all tests within the same year package:

```shell
./gradlew test --test 'aockt.y9999'
```

If for whatever reason you want to run tests in bulk but skip the parts that have been marked as expensive, you can 
filter them out as well:

```shell
./gradlew test -Dkotest.tags='!Expensive'
```
