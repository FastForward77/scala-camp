package com.scalacamp.hometask.first

import akka.actor.{ActorSystem, Scheduler}
import com.scalacamp.hometasks.first.RetryUtil._
import org.scalatest.FunSuite
import org.scalatest.Matchers._
import org.scalatest.concurrent._
import org.scalatest.time.{Seconds, Span}

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future
import scala.concurrent.duration._

class RetryUtilTest extends FunSuite with ScalaFutures {

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

  test("should be invoked twice") {
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val result = retry[Int](
      decorate(invocationState, () => 1),
      successOn(2),
      List(1.seconds)
    )
    result shouldBe 1
    invocationState.getInvocationCount shouldBe 2
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 1)
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
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 0)
  }

  test("should return last result even if not acceptable") {
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val result = retry[Int](
      decorate(invocationState, () => 1),
      successOn(5),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    result shouldBe 1
    invocationState.getInvocationCount shouldBe 4
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 0, 1, 2)
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

  test("async retry should stop after the first acceptable result (1st invocation)") {
    import scala.concurrent.ExecutionContext.Implicits.global
    val actorSystem = ActorSystem()
    implicit val scheduler = actorSystem.scheduler
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val resultFuture = retry[Int](
      decorate(invocationState, () => Future{1}),
      successOn(1),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    whenReady(resultFuture, timeout(Span(5, Seconds))) {result =>
      result shouldBe 1
    }
    invocationState.getInvocationCount shouldBe 1
    invocationState.getPauseDurationsInSeconds() shouldBe List(0)
  }

  test("async retry should be invoked twice") {
    import scala.concurrent.ExecutionContext.Implicits.global
    val actorSystem = ActorSystem()
    implicit val scheduler = actorSystem.scheduler
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val resultFuture = retry[Int](
      decorate(invocationState, () => Future{1}),
      successOn(2),
      List(1.seconds)
    )
    whenReady(resultFuture, timeout(Span(5, Seconds))) {result =>
      result shouldBe 1
    }
    invocationState.getInvocationCount shouldBe 2
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 1)
  }

  test("async retry should stop after the first acceptable result (2nd invocation)") {
    import scala.concurrent.ExecutionContext.Implicits.global
    val actorSystem = ActorSystem()
    implicit val scheduler = actorSystem.scheduler
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val resultFuture = retry[Int](
      decorate(invocationState, () => Future(1)),
      successOn(2),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    whenReady(resultFuture, timeout(Span(5, Seconds))) {result =>
      result shouldBe 1
    }
    invocationState.getInvocationCount shouldBe 2
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 0)
  }

  test("async retry should return last result even if not acceptable") {
    import scala.concurrent.ExecutionContext.Implicits.global
    val actorSystem = ActorSystem()
    implicit val scheduler = actorSystem.scheduler
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val resultFuture = retry[Int](
      decorate(invocationState, () => Future(1)),
      successOn(5),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    whenReady(resultFuture, timeout(Span(5, Seconds))) {result =>
      result shouldBe 1
    }

    invocationState.getInvocationCount shouldBe 4
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 0, 1, 2)
  }

  test("async retry should invoke once immediately if durations are not specified") {
    import scala.concurrent.ExecutionContext.Implicits.global
    val actorSystem = ActorSystem()
    implicit val scheduler = actorSystem.scheduler
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val resultFuture = retry[Int](
      decorate(invocationState, () => Future(1)),
      successOn(5),
      List()
    )
    whenReady(resultFuture, timeout(Span(5, Seconds))) {result =>
      result shouldBe 1
    }
    invocationState.getInvocationCount shouldBe 1
    invocationState.getPauseDurationsInSeconds() shouldBe List(0)
  }

  test("async retry on failed future") {
    import scala.concurrent.ExecutionContext.Implicits.global
    val actorSystem = ActorSystem()
    implicit val scheduler: Scheduler = actorSystem.scheduler
    val invocationState = InvocationState(ListBuffer[Long](System.currentTimeMillis()))
    val resultFuture = retry[Int](
      decorate(invocationState, () => {
        Future {throw new IllegalStateException()}
      }),
      successOn(5),
      List(0.seconds, 1.seconds, 2.seconds)
    )
    whenReady(resultFuture.failed, timeout(Span(5, Seconds))) {e =>
      e shouldBe a [IllegalStateException]
    }
    invocationState.getInvocationCount shouldBe 4
    invocationState.getPauseDurationsInSeconds() shouldBe List(0, 0, 1, 2)
  }

}
