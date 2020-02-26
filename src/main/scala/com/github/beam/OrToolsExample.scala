package com.github.beam

import java.util

import com.google.ortools.algorithms.KnapsackSolver
import com.google.ortools.algorithms.KnapsackSolver.SolverType

private object OrToolsExample {
  private def runAssignmentOn4x4Matrix(): Unit = {
    val var0 = new KnapsackSolver(SolverType.KNAPSACK_MULTIDIMENSION_BRANCH_AND_BOUND_SOLVER, "test")
    val var1 = Array[Long](360L, 83L, 59L, 130L, 431L, 67L, 230L, 52L, 93L, 125L, 670L, 892L, 600L, 38L, 48L, 147L, 78L,
      256L, 63L, 17L, 120L, 164L, 432L, 35L, 92L, 110L, 22L, 42L, 50L, 323L, 514L, 28L, 87L, 73L, 78L, 15L, 26L, 78L,
      210L, 36L, 85L, 189L, 274L, 43L, 33L, 10L, 19L, 389L, 276L, 312L)
    val var2 = Array[Array[Long]](
      Array(7L, 0L, 30L, 22L, 80L, 94L, 11L, 81L, 70L, 64L, 59L, 18L, 0L, 36L, 3L, 8L, 15L, 42L, 9L, 0L, 42L, 47L, 52L,
        32L, 26L, 48L, 55L, 6L, 29L, 84L, 2L, 4L, 18L, 56L, 7L, 29L, 93L, 44L, 71L, 3L, 86L, 66L, 31L, 65L, 0L, 79L,
        20L, 65L, 52L, 13L)
    )
    val var3 = Array[Long](850L)
    var0.init(var1, var2, var3)
    val var4 = var0.solve
    val var6 = new util.ArrayList[Long]()
    val var7 = new util.ArrayList[Long]()
    var var8 = 0
    System.out.println("Total value = " + var4)
    var var9 = 0
    while ({ var9 < var1.length }) {
      if (var0.bestSolutionContains(var9)) {
        var6.add(var9)
        var7.add(var2(0)(var9))
        var8 = (var8.toLong + var2(0)(var9)).toInt
      }
      { var9 += 1; var9 }
    }
    System.out.println("Total weight: " + var8)
    System.out.println("Packed items: " + var6)
    System.out.println("Packed weights: " + var7)
  }

  def main(args: Array[String]): Unit = {
    OrToolsLoader.load()
    OrToolsExample.runAssignmentOn4x4Matrix()
  }
}
