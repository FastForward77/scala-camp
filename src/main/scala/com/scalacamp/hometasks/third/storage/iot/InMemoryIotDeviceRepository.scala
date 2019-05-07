package com.scalacamp.hometasks.third.storage.iot

import java.util.concurrent.atomic.AtomicInteger

import cats.Id
import com.scalacamp.hometasks.third.domain.IotDevice

import scala.collection.mutable

class InMemoryIotDeviceRepository extends IotDeviceRepository[Id] {
  val currentDeviceId = new AtomicInteger(10000)
  val storage: mutable.Map[Long, IotDevice] = mutable.Map.empty

  override def registerDevice(userId: Long, serialNumber: String): Id[IotDevice] = {
    val device = IotDevice(currentDeviceId.getAndIncrement(), userId, serialNumber)
    storage.put(device.id, device)
    device
  }

  override def getById(id: Long): Id[Option[IotDevice]] = storage.get(id)

  override def getBySn(sn: String): Id[Option[IotDevice]] = storage.values.find(_.sn == sn)

  override def getByUser(userId: Long): Id[Seq[IotDevice]] = storage.values.filter(_.userId == userId).toSeq
}


