# Pair Adjacent Violators

## Overview

![Illustration of PAV in action](https://trystacks.github.io/pairAdjacentViolators/pav-example.png)

An implementation of the [Pair Adjacent Violators](http://gifi.stat.ucla.edu/janspubs/2009/reports/deleeuw_hornik_mair_R_09.pdf) algorithm for [isotonic regression](https://en.wikipedia.org/wiki/Isotonic_regression).  Written in [Kotlin](http://kotlinlang.org/) (an exciting new programming language that you should know about if you don't already).  Note this algorithm is also known as "Pool Adjacent Violators".

While not widely known, I've found this algorithm useful in a variety of circumstances, particularly when it comes to [calibration of predictive model outputs](http://scikit-learn.org/stable/modules/calibration.html).

## Features

* Tries to do one thing and do it well with minimal bloat, no external dependencies (other than Kotlin's stdlib)
* Fairly comprehensive [unit tests](https://github.com/trystacks/pairAdjacentViolators/tree/master/src/test/kotlin/com/trystacks/pav) (using [Kotlintest](https://github.com/kotlintest/kotlintest))
* Employs an isotonic spline algorithm for smooth interpolation
* Fairly efficient implementation without compromizing code readability
* While implemented in Kotlin, should be usable from Java, Clojure, Scala, and other JVM languages
* Supports reverse-interpolation

## Limitations

* Very little documentation for the moment, however your IDE should be able to assist for now, and you can always read the source!

## Usage

### Adding library dependency

You can use this library by adding a dependency for Gradle, Maven, SBT, Leiningen or another Maven-compatible dependency management system thanks to Jitpack:

[![](https://jitpack.io/v/trystacks/pairAdjacentViolators.svg)](https://jitpack.io/#trystacks/pairAdjacentViolators)

### Basic usage

```kotlin
import com.trystacks.pav.PairAdjacentViolators
import com.trystacks.pav.PairAdjacentViolators.*
// ...
val inputPoints = listOf(Point(3.0, 1.0), Point(4.0, 2.0), Point(5.0, 3.0), Point(8.0, 4.0))
val pav = PairAdjacentViolators(inputPoints)
val interpolator = pav.interpolator()
println("Interpolated: ${interpolator(6.0)}")
```
### License
Released under the [LGPL](https://en.wikipedia.org/wiki/GNU_Lesser_General_Public_License) version 3 by [Ian Clarke](http://blog.locut.us/) of [Stacks](http://trystacks.com/).
