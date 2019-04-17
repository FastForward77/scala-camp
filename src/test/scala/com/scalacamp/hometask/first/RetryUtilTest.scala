package com.scalacamp.hometask.first

import com.scalacamp.hometasks.first.RetryUtil.retry

import scala.concurrent.duration._
import org.scalatest.FunSuite
import org.scalatest.Matchers._

import scala.collection.mutable.ListBuffer

class RetryUtilTest extends FunSuite {

  case class InvocationState(invocationInstants: ListBuffer[Long]) {
    def getPauseDurationsInSeconds()
    = invocationInstants.sliding(2).map { case Seq(x, y, _*) => (y - x) / 1000 }.toList
    def getInvocationCount = invocationInstants.size - 1
  }

  def successOn[T]: Int => T => Boolean = {
    var count = 0

    successTime: Int =>
      T => {
        count = count + 1
        if (count == successTime) true
        else false
      }
  }

  def decorate[T](invocationState: InvocationState, function: () => T): () => T = {
    () => {
      invocationState.invocationInstants += System.currentTimeMillis()
      function()
    }
  }

  test("should stop after the first acceptable result (1st invocation)") {
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val result = retry[Int](
      decorate(invocationState, () => 1),
      successOn(1),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    result shouldBe 1
    invocationState.getInvocationCount shouldBe 1
    invocationState.getPauseDurationsInSeconds() shouldBe List(0)
  }

  test("should be invoked once") {
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val result = retry[Int](
      decorate(invocationState, () => 1),
      successOn(1),
      List(1.seconds)
    )
    result shouldBe 1
    invocationState.getInvocationCount shouldBe 1
    invocationState.getPauseDurationsInSeconds() shouldBe List(1)
  }

  test("should stop after the first acceptable result (2nd invocation)") {
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val result = retry[Int](
      decorate(invocationState, () => 1),
      successOn(2),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    result shouldBe 1
    invocationState.getInvocationCount shouldBe 2
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 1)
  }

  test("should return last result even if not acceptable") {
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val result = retry[Int](
      decorate(invocationState, () => 1),
      successOn(5),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    result shouldBe 1
    invocationState.getInvocationCount shouldBe 3
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 1, 2)
  }

  test("should invoke once immediately if durations are not specified") {
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val result = retry[Int](
      decorate(invocationState, () => 1),
      successOn(5),
      List()
    )
    result shouldBe 1
    invocationState.getInvocationCount shouldBe 1
    invocationState.getPauseDurationsInSeconds() shouldBe List(0)
  }
}
