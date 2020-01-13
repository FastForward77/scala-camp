package com.scalacamp.hometask.third.storage.device

import com.scalacamp.hometasks.third.storage.device.InMemoryIotDeviceRepository
import com.scalacamp.hometasks.web.storage.repo.InMemoryUserRepository
import org.scalatest.FunSuite
import org.scalatest.Matchers._


class InMemoryDeviceRepositoryTest extends FunSuite {
  test("should register device and get it by by id, sn and user") {
    val deviceRepository = new InMemoryIotDeviceRepository
    val serial = "Serial"
    val userId = 100001
    val device = deviceRepository.registerDevice(userId, serial)
    val deviceById = deviceRepository.getById(device.id)
    val deviceBySn = deviceRepository.getBySn(serial)
    val deviceByUser = deviceRepository.getByUser(userId)
    deviceById.get shouldBe device
    deviceBySn.get shouldBe device
    deviceByUser.head shouldBe device
  }

  test("should return empty option if device is absent") {
    val deviceRepository = new InMemoryIotDeviceRepository
    val deviceById = deviceRepository.getById(100500)
    val deviceBySn = deviceRepository.getBySn("unknown")
    val deviceByUser = deviceRepository.getByUser(10001)
    deviceById.isEmpty shouldBe true
    deviceBySn.isEmpty shouldBe true
    deviceByUser.isEmpty shouldBe true
  }
}
