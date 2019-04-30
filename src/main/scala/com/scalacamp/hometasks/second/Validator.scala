package com.scalacamp.hometasks.second

/**
  * Implement validator typeclass that should validate arbitrary value [T].
  * @tparam T the type of the value to be validated.
  */
trait Validator[T] {
  /**
    * Validates the value.
    * @param value value to be validated.
    * @return Right(value) in case the value is valid, Left(message) on invalid value
    */
  def validate(value: T): Either[String, T]

  /**
    * And combinator.
    * @param other validator to be combined with 'and' with this validator.
    * @return the Right(value) only in case this validator and <code>other</code> validator returns valid value,
    *         otherwise Left with error messages from the validator that failed.
    */
  def and(other: Validator[T]): Validator[T] =
    new Validator[T] {
      override def validate(value: T): Either[String, T] =
        Validator.this.validate(value).right.flatMap(_ =>
          other.validate(value).right.map(v2Res => v2Res))
    }

  /**
    * Or combinator.
    * @param other validator to be combined with 'or' with this validator.
    * @return the Right(value) only in case either this validator or <code>other</code> validator returns valid value,
    *         otherwise Left with error messages from both validators.
    */
  def or(other: Validator[T]): Validator[T] =
    new Validator[T] {
      override def validate(value: T): Either[String, T] =
        Validator.this.validate(value).left.flatMap(v1Res =>
          other.validate(value).left.map(v2Res => s"$v1Res and $v2Res"))
    }
}


object Validator {

  implicit def string2PersonValidator(stringValidator: Validator[String]): Validator[Person] =
    new Validator[Person] {
      override def validate(person: Person): Either[String, Person] = {
        stringValidator.validate(person.name).right.map(_ => person)
      }
    }

  implicit def int2PersonValidator(intValidator: Validator[Int]): Validator[Person] =
    new Validator[Person] {
      override def validate(person: Person): Either[String, Person] = {
        intValidator.validate(person.age).right.map(_ => person)
      }
    }


//  def validate[A](value: A)(implicit v: Validator[A]): Either[String, A] = v.validate(value)

  implicit val positiveInt : Validator[Int] = new Validator[Int] {
    override def validate(t: Int): Either[String, Int] = {
      if (t > 0) Right(t) else Left(s"$t isn't a positive number")
    }
  }

  implicit def lessThan(n: Int): Validator[Int] = new Validator[Int] {
    override def validate(t: Int): Either[String, Int] = {
      if (t < n) Right(t) else Left(s"$t isn't less than $n")
    }
  }

  implicit val nonEmpty : Validator[String] = new Validator[String] {
    override def validate(t: String): Either[String, String] = {
      if (t == null || t.isEmpty) Left("String is empty") else Right(t)
    }
  }

  implicit val isPersonValid: Validator[Person] = new Validator[Person] {
    // Returns valid only when the name is not empty and age is in range [1-99].
    override def validate(person: Person): Either[String, Person] = {
      string2PersonValidator(nonEmpty) and lessThan(100) and positiveInt validate person
    }
  }

  implicit class ValidatorOps[A](value: A) {
    def validate(implicit validatorInstance: Validator[A]): Either[String, A] = {
      validatorInstance.validate(value)
    }
  }

//  implicit class ValidatorOps[A: Validator](value: A) {
//    def validate: Either[String, A] = implicitly[Validator[A]].validate(value)
//  }
}


object ValidApp extends App {
  import Validator._

  // uncomment make possible next code to compile
//  2 validate (positiveInt and lessThan(10))

  // uncomment make possible next code to compile
  "" validate Validator.nonEmpty

  // uncomment make possible next code to compile
  print(Person(name = "John", age = 25) validate isPersonValid)
}

object ImplicitValidApp extends App {
  import Validator.ValidatorOps
  import Validator.isPersonValid
  // uncomment next code and make it compilable and workable
  Person(name = "John", age = 25) validate
  import Validator.nonEmpty
  "asdasd" validate
  import Validator.positiveInt
    234.validate
}


case class Person(name: String, age: Int)
