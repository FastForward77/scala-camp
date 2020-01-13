package com.scalacamp.hometasks.web.ops

import com.scalacamp.hometasks.web.domain.User
import spray.json._

object UserJsonProtocol extends DefaultJsonProtocol {

  implicit object UserJsonFormat extends RootJsonFormat[User] {

    override def read(json: JsValue): User = {
      json.asJsObject.getFields("username", "address", "email") match {
        case Seq(JsString(username), JsString(address), JsString(email)) => User(0, username, Some(address), email)
        case Seq(JsString(username), JsString(email)) => User(0, username, None, email)
        case _ => throw DeserializationException("User expected")
      }
    }

    override def write(obj: User): JsValue = {
      obj.address match {
        case Some(address) => JsObject(
          "id" -> obj.id.toJson,
          "username" -> obj.username.toJson,
          "address" -> address.toJson,
          "email" -> obj.email.toJson
        )
        case None => JsObject(
          "id" -> obj.id.toJson,
          "username" -> obj.username.toJson,
          "email" -> obj.email.toJson
        )
      }

    }
  }
}
