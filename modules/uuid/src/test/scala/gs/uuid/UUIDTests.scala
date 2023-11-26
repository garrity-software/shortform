package gs.uuid

class UUIDTests extends munit.FunSuite:
  private val v4                                 = UUID.Generator.version4
  private val v7                                 = UUID.Generator.version7
  given CanEqual[java.util.UUID, java.util.UUID] = CanEqual.derived

  test(
    "should instantiate a type 4 UUID, serialize it, and parse the result"
  ) {
    val base   = v4.next()
    val str    = base.str()
    val parsed = UUID.parse(str)
    assert(parsed == Some(base))
  }

  test(
    "should instantiate a type 7 UUID, serialize it, and parse the result"
  ) {
    val base   = v7.next()
    val str    = base.str()
    val parsed = UUID.parse(str)
    assert(parsed == Some(base))
  }

  test("should instantiate from any java.util.UUID") {
    val raw    = java.util.UUID.randomUUID()
    val base   = UUID(raw)
    val str    = base.str()
    val parsed = UUID.fromString(str)
    assert(parsed == Some(base))
    assert(parsed.map(_.toUUID()) == Some(raw))
  }

  test("should successfully parse a UUID with dashes") {
    val base = java.util.UUID.randomUUID()
    assert(UUID.parse(base.toString()) == Some(UUID(base)))
  }

  test("should fail to parse a non-hex string") {
    val input = "ghijklmnoped45189efa6f40ed9518ed"
    assert(UUID.parse(input) == None)
  }

  test("should generate using an available type class instance") {
    given UUID.Generator = v7
    val base             = doGen
    val str              = base.str()
    val parsed           = UUID.parse(str)
    assert(parsed == Some(base))
  }

  private def doGen(
    using
    UUID.Generator
  ): UUID =
    UUID.generate()
