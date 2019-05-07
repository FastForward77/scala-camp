package com.scalacamp.hometasks.third.service

import cats.Monad
import com.scalacamp.hometasks.third.domain.{IotDevice}
import com.scalacamp.hometasks.third.storage.iot.IotDeviceRepository
import com.scalacamp.hometasks.third.storage.user.UserRepository
import cats.implicits._


class IotDeviceService[F[_]](repository: IotDeviceRepository[F],
                             userRepository: UserRepository[F])
                            (implicit monad: Monad[F]) {

  // the register should fail with Left if the user doesn't exist or the sn already exists.
  def registerDevice(userId: Long, sn: String): F[Either[String, IotDevice]] = {
    userRepository.getById(userId).flatMap({
      case Some(_) =>
        repository.getBySn(sn).flatMap({
          case Some(_) => monad.pure(Left(s"Device with serial number: '$sn' already exists"))
          case None => repository.registerDevice(userId, sn).map(Right(_))
        })
      case None =>
        monad.pure(Left(s"User '$userId' doesn't exist"))
    })

  }
}
