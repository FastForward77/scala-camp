package com.scalacamp.hometask.third

import com.scalacamp.hometasks.third.service.IotDeviceService
import com.scalacamp.hometasks.third.storage.device.InMemoryIotDeviceRepository
import com.scalacamp.hometasks.web.service.UserService
import com.scalacamp.hometasks.web.storage.repo.InMemoryUserRepository
import org.scalatest.{EitherValues, FunSuite}
import org.scalatest.Matchers._


class IotDeviceServiceTest extends FunSuite with EitherValues {
  test("should successful add device for existing user") {
    val userRepository = new InMemoryUserRepository
    val deviceService = new IotDeviceService(new InMemoryIotDeviceRepository, userRepository)
    val userService = new UserService(userRepository)

    val username = "Bob"
    val address = Some("address")
    val email = "123@email.com"
    val deviceSerial = "123456"
    val resultUser = userService.registerUser(username, address, email).right.value

    val device = deviceService.registerDevice(resultUser.id, deviceSerial).right.value
    device.userId shouldBe resultUser.id
    device.sn shouldBe deviceSerial
    device.id shouldBe 10000L
  }

  test("should fail on adding device for non existing user") {
    val userRepository = new InMemoryUserRepository
    val deviceService = new IotDeviceService(new InMemoryIotDeviceRepository, userRepository)
    val userService = new UserService(userRepository)

    val deviceSerial = "123456"

    deviceService.registerDevice(100010L, deviceSerial).isLeft shouldBe true
  }

  test("should fail on adding device for existing serial number") {
    val userRepository = new InMemoryUserRepository
    val deviceService = new IotDeviceService(new InMemoryIotDeviceRepository, userRepository)
    val userService = new UserService(userRepository)

    val username = "Bob"
    val address = Some("address")
    val email = "123@email.com"
    val deviceSerial = "123456"
    val resultUser = userService.registerUser(username, address, email).right.value

    deviceService.registerDevice(resultUser.id, deviceSerial)
    deviceService.registerDevice(resultUser.id, deviceSerial).isLeft shouldBe true
  }
}
