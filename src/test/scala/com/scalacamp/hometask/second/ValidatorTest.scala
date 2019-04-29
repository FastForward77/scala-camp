package com.scalacamp.hometask.second

import com.scalacamp.hometasks.second.Validator
import com.scalacamp.hometasks.second.Validator.{lessThan, positiveInt}
import org.scalatest.FunSuite

class ValidatorTest extends FunSuite {
  test("validator exp") {

    val res = (positiveInt and lessThan(10))
    print(res)

//    val l: Either[String, Int] = Left("")
//    print(Right(1).right.flatMap(v1Res =>
//      l.right.map(v2Res => v1Res + v2Res)))
  }
}
