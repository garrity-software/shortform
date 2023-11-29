package gs.shortform.crypto.argon2

import gs.shortform.crypto.EncodedCredential
import java.util.UUID
import munit.*
import org.bouncycastle.crypto.params.Argon2Parameters

class Argon2Tests extends FunSuite:
  import Argon2Tests.TestData

  val argon2 = Argon2.defaultInstance()

  test("should hash some value, encode the hash, and decode the encoded form") {
    val data    = UUID.randomUUID().toString()
    val hash    = argon2.hash(data)
    val encoded = argon2.encode(hash)
    val decoded = argon2.decode(encoded)

    // Ensure the decoded hash has the same bytes as the original hash.
    assertEquals(decoded.isDefined, true)
    decoded.foreach { d =>
      assertEquals(d.hash.sameElements(hash.hash), true)
      assertEquals(
        d.parameters.getSalt().sameElements(hash.parameters.getSalt()),
        true
      )
      assertEquals(d.parameters.getVersion(), hash.parameters.getVersion())
      assertEquals(d.parameters.getMemory(), hash.parameters.getMemory())
      assertEquals(
        d.parameters.getIterations(),
        hash.parameters.getIterations()
      )
      assertEquals(d.parameters.getLanes(), hash.parameters.getLanes())
    }
  }

  test("should decode a valid hash") {
    val hash    = EncodedCredential(TestData.Valid)
    val decoded = argon2.decode(hash)
    assertEquals(decoded.isDefined, true)
    decoded.foreach { d =>
      assertEquals(
        d.parameters.getVersion(),
        Argon2Parameters.ARGON2_VERSION_13
      )
      assertEquals(d.parameters.getMemory(), Argon2.Defaults.Memory)
      assertEquals(d.parameters.getIterations(), Argon2.Defaults.Iterations)
      assertEquals(d.parameters.getLanes(), Argon2.Defaults.Parallelism)
    }
  }

  test("should refuse to decode an encoded hash with a bad algorithm") {
    val credential = EncodedCredential(TestData.BadAlgorithm)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test("should refuse to decode an encoded hash with a bad version") {
    val credential = EncodedCredential(TestData.BadVersion)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test("should refuse to decode an encoded hash with bad memory") {
    val credential = EncodedCredential(TestData.BadMemory)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test("should refuse to decode an encoded hash with bad iterations") {
    val credential = EncodedCredential(TestData.BadIterations)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test("should refuse to decode an encoded hash with bad parallelism") {
    val credential = EncodedCredential(TestData.BadParallelism)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test(
    "should refuse to decode an encoded hash with an invalid number of parts"
  ) {
    val credential = EncodedCredential(TestData.WrongNumberOfParts)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test(
    "should refuse to decode an encoded hash with an invalid number of performance parts"
  ) {
    val credential = EncodedCredential(TestData.WrongNumberOfPerformanceParts)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test(
    "should refuse to decode an encoded hash with an invalid version prefix"
  ) {
    val credential = EncodedCredential(TestData.BadVersionPrefix)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test(
    "should refuse to decode an encoded hash with an invalid memory prefix"
  ) {
    val credential = EncodedCredential(TestData.BadMemoryPrefix)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test(
    "should refuse to decode an encoded hash with an invalid iterations prefix"
  ) {
    val credential = EncodedCredential(TestData.BadIterationsPrefix)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test(
    "should refuse to decode an encoded hash with an invalid parallelism prefix"
  ) {
    val credential = EncodedCredential(TestData.BadParallelismPrefix)
    assertEquals(argon2.decode(credential).isEmpty, true)
  }

  test(
    "should determine that equal inputs have matching hashes"
  ) {
    val data    = UUID.randomUUID().toString()
    val output  = argon2.hash(data)
    val encoded = argon2.encode(output)
    assertEquals(argon2.matches(data, encoded), true)
  }

  test(
    "should determine that non-equal inputs do not have matching hashes"
  ) {
    val data    = UUID.randomUUID().toString()
    val output  = argon2.hash(data)
    val encoded = argon2.encode(output)
    assertEquals(argon2.matches("foo", encoded), false)
  }

object Argon2Tests:

  object TestData:

    val Valid: String =
      "$argon2id$v=19$m=19456,t=2,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadAlgorithm: String =
      "$argon2$v=19$m=19456,t=2,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadVersion: String =
      "$argon2id$v=XYZ$m=19456,t=2,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadMemory: String =
      "$argon2id$v=19$m=XYZ,t=2,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadIterations: String =
      "$argon2id$v=19$m=19456,t=XYZ,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadParallelism: String =
      "$argon2id$v=19$m=19456,t=2,p=XYZ$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val WrongNumberOfParts: String = "$argon2id$v=19$m=19456,t=2,p=1"

    val WrongNumberOfPerformanceParts: String =
      "$argon2id$v=19$m=19456$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadVersionPrefix: String =
      "$argon2id$Z=19$m=19456,t=2,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadMemoryPrefix: String =
      "$argon2id$v=19$Z=19456,t=2,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadIterationsPrefix: String =
      "$argon2id$v=19$m=19456,Z=2,p=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

    val BadParallelismPrefix: String =
      "$argon2id$v=19$m=19456,t=2,Z=1$/Uz9Rqt/b6SN53LfdNmfYA$v1Nscv0zqsMSvBnh6DlhubjCrmcx5dZTrOOnImPiOZ4"

  end TestData

end Argon2Tests
