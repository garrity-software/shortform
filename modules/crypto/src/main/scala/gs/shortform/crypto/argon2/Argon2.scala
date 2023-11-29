package gs.shortform.crypto.argon2

import gs.shortform.crypto.EncodedCredential
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import org.bouncycastle.crypto.generators.Argon2BytesGenerator
import org.bouncycastle.crypto.params.Argon2Parameters
import scala.util.Try

/** Argon2id support based on BouncyCastle. For typical use cases please refer
  * to `Argon2.defaultInstance()`.
  *
  * This class does not support other Argon2 flavors.
  *
  * @param saltLength
  *   The salt length for hashing.
  * @param hashLength
  *   The overall hash length
  * @param memoryInKb
  *   Memory in KB to use for Argon2.
  * @param iterations
  *   Number of iterations.
  * @param parallelism
  *   Allowed parallelism (lanes).
  * @param rng
  *   Secure random number generator.
  */
final class Argon2(
  val saltLength: Int,
  val hashLength: Int,
  val memoryInKb: Int,
  val iterations: Int,
  val parallelism: Int,
  val rng: SecureRandom
):

  def hashCredential(input: String): EncodedCredential = encode(hash(input))

  /** Hash the given input using Argon2id.
    *
    * @param input
    *   The input to hash.
    * @return
    *   The output, containing the argon2id hash and argon2 parameters.
    */
  def hash(input: String): Argon2Output =
    val params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
      .withMemoryAsKB(memoryInKb)
      .withIterations(iterations)
      .withParallelism(parallelism)
      .withSalt(generateSalt(saltLength))
      .build()

    val generator = new Argon2BytesGenerator()
    val _         = generator.init(params)
    val out       = new Array[Byte](hashLength)
    val _ = generator.generateBytes(input.getBytes(StandardCharsets.UTF_8), out)
    Argon2Output(out, params)

  private def generateSalt(length: Int): Array[Byte] =
    val bytes = new Array[Byte](length)
    val _     = rng.nextBytes(bytes)
    bytes

  /** Test whether some input matches some target encoded credential. The target
    * must be encoded using the standard Argon2 encoding (produced by the
    * `encode` function of this class).
    *
    * @param candidate
    *   The candidate to test.
    * @param target
    *   The target encoded credential to match against.
    * @return
    *   True if the candidate matches the credential, false otherwise.
    */
  def matches(
    candidate: String,
    target: EncodedCredential
  ): Boolean =
    decode(target)
      .map { output =>
        val bytes     = new Array[Byte](output.hash.length)
        val generator = new Argon2BytesGenerator
        val _         = generator.init(output.parameters)
        val _ = generator.generateBytes(
          candidate.getBytes(StandardCharsets.UTF_8),
          bytes
        )
        output.hash.sameElements(bytes)
      }
      .getOrElse(false)

  /** Encode an Argon2id hash according to the standard format defined in the
    * Argon2 reference implementation:
    *
    * $argon2<T>[$v=<num>]$m=<num>,t=<num>,p=<num>$<bin>$<bin>
    *
    *   - `v` = version
    *   - `m` = memory
    *   - `t` = iterations
    *   - `p` = lanes (parallelism)
    *
    * Quoted from the reference:
    *
    * "The last two binary chunks (encoded in Base64) are, in that order, the
    * salt and the output. Both are required. The binary salt length and the
    * output length must be in the allowed ranges defined in argon2."
    *
    * The reference explicitly disallows padding characters in the Base64
    * encoding.
    *
    * ### Implementation Notes
    *
    * This implementation is specific to Argon2id and requires a version.
    *
    * @param argon2
    *   The hash and parameters.
    * @return
    *   The string encoding of the Argon2id hash.
    */
  def encode(output: Argon2Output): EncodedCredential =
    val builder = new java.lang.StringBuilder
    builder
      .append("$")
      .append(Argon2.Algorithm)
      .append("$v=")
      .append(output.parameters.getVersion())
      .append("$m=")
      .append(output.parameters.getMemory())
      .append(",t=")
      .append(output.parameters.getIterations())
      .append(",p=")
      .append(output.parameters.getLanes())

    Option(output.parameters.getSalt())
      .foreach(salt =>
        builder.append("$").append(Argon2.b64e.encodeToString(salt))
      )

    builder.append("$").append(Argon2.b64e.encodeToString(output.hash))

    EncodedCredential(builder.toString())

  /** Decode an encoded Argon2 credential. If successful, unpacks the encoded
    * form as [[Argon2Output]].
    *
    * @param credential
    *   The encoded credential to decode.
    * @return
    *   The decoded [[Argon2Output]], or `None` if the input is invalid.
    */
  def decode(credential: EncodedCredential): Option[Argon2Output] =
    val parts = credential.str().split("\\$")
    if parts.length != 6 then None
    else decodeParts(parts(1), parts(2), parts(3), parts(4), parts(5))

  private def decodeParts(
    algorithmPart: String,
    versionPart: String,
    performancePart: String,
    saltPart: String,
    hashPart: String
  ): Option[Argon2Output] =
    decodeAlgorithm(algorithmPart).flatMap { builder =>
      for
        version            <- decodeVersion(versionPart)
        (mem, iter, lanes) <- decodePerformance(performancePart)
      yield Argon2Output(
        hash = Argon2.b64d.decode(hashPart),
        parameters = builder
          .withVersion(version)
          .withMemoryAsKB(mem)
          .withIterations(iter)
          .withParallelism(lanes)
          .withSalt(Argon2.b64d.decode(saltPart))
          .build()
      )
    }

  private def decodeAlgorithm(
    candidate: String
  ): Option[Argon2Parameters.Builder] =
    if candidate.equalsIgnoreCase(Argon2.Algorithm) then
      Some(new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id))
    else None

  private def decodeVersion(
    candidate: String
  ): Option[Int] =
    if candidate.startsWith("v=") then
      Try(candidate.substring(2).toInt).toOption
    else None

  private def decodePerformance(performancePart: String)
    : Option[(Int, Int, Int)] =
    val parts = performancePart.split(",")
    if parts.length == 3 then
      for
        mem   <- decodeMemory(parts(0))
        iter  <- decodeIterations(parts(1))
        lanes <- decodeLanes(parts(2))
      yield (mem, iter, lanes)
    else None

  private def decodeMemory(
    candidate: String
  ): Option[Int] =
    if candidate.startsWith("m=") then
      Try(candidate.substring(2).toInt).toOption
    else None

  private def decodeIterations(
    candidate: String
  ): Option[Int] =
    if candidate.startsWith("t=") then
      Try(candidate.substring(2).toInt).toOption
    else None

  private def decodeLanes(
    candidate: String
  ): Option[Int] =
    if candidate.startsWith("p=") then
      Try(candidate.substring(2).toInt).toOption
    else None

object Argon2:

  /** The formal algorithm name: `argon2id`
    */
  val Algorithm: String = "argon2id"

  /** Instantiate an instance of the Argon2 algorithm with default parameters.
    *
    * @param rng
    *   The secure random number generator to use for salts.
    * @return
    *   The new [[Argon2]] instance.
    */
  def defaultInstance(rng: SecureRandom = new SecureRandom()): Argon2 =
    new Argon2(
      saltLength = Defaults.SaltLength,
      hashLength = Defaults.HashLength,
      memoryInKb = Defaults.Memory,
      iterations = Defaults.Iterations,
      parallelism = Defaults.Parallelism,
      rng = rng
    )

  /** According to the OWASP Cheat Sheet: Use Argon2id with a minimum
    * configuration of 19 MiB of memory, an iteration count of 2, and 1 degree
    * of parallelism.
    */
  object Defaults:
    val SaltLength: Int  = 16
    val HashLength: Int  = 32
    val Memory: Int      = 19456
    val Iterations: Int  = 2
    val Parallelism: Int = 1
  end Defaults

  private val b64e = Base64.getEncoder().withoutPadding()
  private val b64d = Base64.getDecoder()
end Argon2
