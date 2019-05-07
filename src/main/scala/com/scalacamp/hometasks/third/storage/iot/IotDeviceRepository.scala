package com.scalacamp.hometasks.third.storage.iot

import com.scalacamp.hometasks.third.domain.IotDevice

trait IotDeviceRepository[F[_]] {
  def registerDevice(userId: Long, serialNumber: String): F[IotDevice]
  def getById(id: Long): F[Option[IotDevice]]
  def getBySn(sn: String): F[Option[IotDevice]]
  def getByUser(userId: Long): F[Seq[IotDevice]]
}
