package com.scalacamp.hometask.second

import com.scalacamp.hometasks.second.Validator.{lessThan, positiveInt}
import org.scalatest.{EitherValues, FunSuite}
import org.scalatest.Matchers._

class ValidatorTest extends FunSuite with EitherValues {
  test("test and both success") {
    val positiveNumberLessThanTen = 9
    val result = (positiveInt and lessThan(10)).validate(positiveNumberLessThanTen)
    result.right.value should be (positiveNumberLessThanTen)
  }

  test("test and one success one failure") {
    val negativeNumber = -1
    val result = (positiveInt and lessThan(10)).validate(negativeNumber)
    result.isLeft should be (true)
  }

  test("test and both failure") {
    val negativeNumber = -1
    val result = (positiveInt and lessThan(-5)).validate(negativeNumber)
    result.isLeft should be (true)
  }

  test("test or both success") {
    val positiveNumberLessThanTen = 9
    val result = (positiveInt or lessThan(10)).validate(positiveNumberLessThanTen)
    result.right.value should be (positiveNumberLessThanTen)
  }

  test("test or one success one failure") {
    val negativeNumber = -1
    val result = (positiveInt or lessThan(10)).validate(negativeNumber)
    result.right.value should be (negativeNumber)
  }

  test("test or both failure") {
    val negativeNumber = -1
    val result = (positiveInt or lessThan(-5)).validate(negativeNumber)
    result.isLeft should be (true)
  }
}
