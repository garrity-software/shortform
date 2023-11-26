package gs.uuid

import com.fasterxml.uuid.Generators

/** Alias for the `java.util.UUID` type, which represents a 128-bit value.
  *
  * ## ID Generation
  *
  * This library provides generator implementations for the following types of
  * UUID:
  *
  *   - Type 4
  *   - Type 7
  *
  * These implementations are provided by JUG.
  *
  * ## Serialization
  *
  * This library uses a custom variant of the JDK 17 implementation that removes
  * dashes from the output and is likewise capable of parsing those values.
  *
  * {{{
  * val example: UUID = UUID(java.util.UUID.randomUUID())
  * val serialized = example.str() // or example.withoutDashes()
  * // example value = 899efa6f40ed45189efa6f40ed9518ed
  * }}}
  */
opaque type UUID = java.util.UUID

object UUID:
  /** Express any `java.util.UUID` as a Meager UUID.
    *
    * @param uuid
    *   The input UUID.
    * @return
    *   The aliased value.
    */
  def apply(uuid: java.util.UUID): UUID = uuid

  given CanEqual[UUID, UUID] = CanEqual.derived

  /** Generate a new UUID.
    *
    * @param G
    *   The [[Generator]] type class instance.
    * @return
    *   The new UUID.
    */
  def generate(
  )(
    using
    G: Generator
  ): UUID = G.next()

  /** Parse the given string as a UUID.
    *
    * @param str
    *   The UUID, which is expected to be in a hexadecimal format with no
    *   dashes.
    * @return
    *   The parsed UUID value, or `None` if the value does not represent a UUID.
    */
  def parse(str: String): Option[UUID] = fromString(str)

  def fromString(str: String): Option[UUID] =
    scala.util
      .Try(UUIDFormat.fromHex(str))
      .map(uuid => Some(apply(uuid)))
      .getOrElse(None)

  extension (uid: UUID)
    def toUUID(): java.util.UUID = uid

    def str(): String = withoutDashes()

    def withoutDashes(): String = UUIDFormat.toHex(uid)

    def lsb(): Long = uid.getLeastSignificantBits()

    def msb(): Long = uid.getMostSignificantBits()

    def isZero(): Boolean = lsb() == 0L && msb() == 0L

  /** Type class for UUID generation.
    */
  trait Generator:
    /** Generate a new UUID.
      */
    def next(): UUID

  object Generator:
    /** Instantiate a new Type 4 generator.
      */
    def version4: Generator = new Version4

    /** Instantiate a new Type 7 generator.
      */
    def version7: Generator = new Version7

    /** Type 4 (Random) implementation of a UUID generator.
      */
    final class Version4 extends Generator:
      private val gen           = Generators.randomBasedGenerator()
      override def next(): UUID = gen.generate()

    /** Type 7 (Unix Epoch Time + Random) implementation of a UUID generator.
      * Consider using this rather than Type 1 or Type 6.
      *
      * This type is defined in [IETF New UUID Formats
      * Draft](https://www.ietf.org/archive/id/draft-peabody-dispatch-new-uuid-format-04.html#name-uuid-version-7)
      */
    final class Version7 extends Generator:
      private val gen           = Generators.timeBasedEpochGenerator()
      override def next(): UUID = gen.generate()
