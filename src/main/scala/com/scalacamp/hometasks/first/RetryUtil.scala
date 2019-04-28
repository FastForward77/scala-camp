package com.scalacamp.hometasks.first

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration


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
        case head::tail => {
          Thread.sleep(head.toMillis)
          retry(block, acceptResult, tail)
        }
        case _ => result
      }
    }
  }
}
