package sqids

import munit.ScalaCheckSuite
import sqids.options.Alphabet

class AlphabetSuite extends ScalaCheckSuite {
  test("simple") {
    val numbers = List(1, 2, 3)
    val id = "4d9fd2"
    Alphabet("0123456789abcdef")
      .flatMap(Sqids.forAlphabet)
      .foreach { sqids =>
        assertEquals(sqids.encode(numbers).map(_.value), Right(id))
        assertEquals(sqids.decode(id), numbers)
      }
  }

  test("short alphabet") {
    val numbers = List(1, 2, 3)
    Alphabet("abcde")
      .flatMap(Sqids.forAlphabet)
      .foreach(sqids => assertEquals(sqids.decode(sqids.encodeUnsafeString(numbers: _*)), numbers))
  }

  test("long alphabet") {
    val numbers = List(1, 2, 3)
    Alphabet(
      "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_+|{}[];:'\"/?.>,<`~"
    )
      .flatMap(Sqids.forAlphabet)
      .foreach(sqids => assertEquals(sqids.decode(sqids.encodeUnsafeString(numbers: _*)), numbers))
  }

  test("repeating alphabet characters") {
    assert(Alphabet("aabcdefg").isLeft)
  }

  test("too short of an alphabet") {
    assert(Alphabet("abcd").isLeft)
  }
}
