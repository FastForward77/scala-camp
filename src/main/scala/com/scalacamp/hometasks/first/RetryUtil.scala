package com.scalacamp.hometasks.first

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration


object RetryUtil {
  @tailrec
  final def retry[A](block: () => A,
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): A = {
    retries match {
      case Nil => block()
      case head :: tail => {
        Thread.sleep(head.toMillis)
        val result = block()
        if (acceptResult(result)) {
          result
        } else {
          tail match {
            case Nil => result
            case _ => retry(block, acceptResult, tail)
          }
        }
      }
    }
  }
}
