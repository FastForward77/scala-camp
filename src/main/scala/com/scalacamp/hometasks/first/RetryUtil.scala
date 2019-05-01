package com.scalacamp.hometasks.first

import akka.actor.Scheduler

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import akka.pattern.after

object RetryUtil {
  @tailrec
  final def retry[A](block: () => A,
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): A = {
    val result = block()
    if (acceptResult(result)) {
      result
    } else {
      retries match {
        case head :: tail =>
          Thread.sleep(head.toMillis)
          retry(block, acceptResult, tail)
        case _ => result
      }
    }
  }


  final def retry[A](block: () => Future[A],
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration])
                    (implicit ec : ExecutionContext, scheduler: Scheduler): Future[A] = {
    block().flatMap(result => {
      if (acceptResult(result)) {
        Future.successful(result)
      } else {
        retries match {
          case head :: tail =>
            after(retries.head, scheduler)(retry(block, acceptResult, retries.tail))
          case _ => Future.successful(result)
        }
      }
    }).recoverWith { case e if retries.nonEmpty =>
      after(retries.head, scheduler)(retry(block, acceptResult, retries.tail))
    }
  }
}