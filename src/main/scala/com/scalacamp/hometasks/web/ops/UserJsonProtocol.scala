package com.scalacamp.hometasks.web.ops

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.scalacamp.hometasks.web.domain.User
import spray.json._

trait UserJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object UserJsonFormat extends JsonFormat[User] {

    override def read(json: JsValue): User = {
      json.asJsObject.getFields("username", "address", "email") match {
        case Seq(JsString(username), JsString(address), JsString(email)) => User(0, username, Some(address), email)
        case _ => throw new DeserializationException("User expected")
      }
    }

    override def write(obj: User): JsValue = {
      JsObject(
        "id" -> obj.id.toJson,
        "username" -> obj.username.toJson,
        "email" -> obj.email.toJson,
        "address" -> obj.address.toJson
      )
    }
  }
}
