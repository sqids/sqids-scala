package sqids
import munit.ScalaCheckSuite
import sqids.options.Blocklist

final class SqidsSuite_blockList extends ScalaCheckSuite {
  val sqids = Sqids.default

  test("simple") {
    val numbers = sqids.decode("sexy")
    val encoded = sqids.encodeUnsafeString(numbers: _*)
    assertEquals(encoded, "d171vI")
  }

  test("if an empty blocklist param passed, don't use any blocklist") {
    val sqids = Sqids.withBlocklist(Blocklist.empty)
    val numbers = sqids.decode("sexy")
    val encoded = sqids.encodeUnsafeString(numbers: _*)
    assertEquals(encoded, "sexy")
  }
  test("if a non-empty blocklist param passed, use only that") {
    val sqids = Sqids.withBlocklist(Blocklist(Set("AvTg")))
    assertEquals(sqids.decode("sexy"), List(200044))
    assertEquals(sqids.encodeUnsafeString(200044), "sexy")
    assertEquals(sqids.decode("AvTg"), List(100000))
    assertEquals(sqids.encodeUnsafeString(100000), "7T1X8k")
    assertEquals(sqids.decode("7T1X8k"), List(100000))

  }
  test("only block functionality") {
    val blocklist = Blocklist(
      Set(
        "8QRLaD", // normal result of 1st encoding, let's block that word on purpose
        "7T1cd0dL", // result of 2nd encoding
        "UeIe", // result of 3rd encoding is `RA8UeIe7`, let's block a substring
        "imhw", // result of 4th encoding is `WM3Limhw`, let's block the postfix
        "LfUQ" // result of 4th encoding is `LfUQh4HN`, let's block the prefix
      )
    )
    List(
      "8QRLaD",
      "7T1cd0dL", // result of 2nd encoding
      "RA8UeIe7", // result of 3rd encoding is `RA8UeIe7`, let's block a substring
      "WM3Limhw", // result of 4th encoding is `WM3Limhw`, let's block the postfix
      "LfUQh4HN"
    ).foreach(id => assume(blocklist.isBlocked(id)))
  }
  test("blocklist") {
    val sqids = Sqids.withBlocklist(
      Blocklist(
        Set(
          "8QRLaD", // normal result of 1st encoding, let's block that word on purpose
          "7T1cd0dL", // result of 2nd encoding
          "UeIe", // result of 3rd encoding is `RA8UeIe7`, let's block a substring
          "imhw", // result of 4th encoding is `WM3Limhw`, let's block the postfix
          "LfUQ" // result of 4th encoding is `LfUQh4HN`, let's block the prefix
        )
      )
    )

    assertEquals(sqids.encodeUnsafeString(1, 2, 3), "TM0x1Mxz")
    assertEquals(sqids.decode("TM0x1Mxz"), List(1, 2, 3));
  }

  test("decoding blocklist words should still work") {
    val blocklist = Set(
      "8QRLaD",
      "7T1cd0dL",
      "RA8UeIe7",
      "WM3Limhw",
      "LfUQh4HN"
    )
    val sqids = Sqids.withBlocklist(
      Blocklist(
        blocklist
      )
    )
    blocklist.foreach(id => assertEquals(sqids.decode(id), List(1, 2, 3)))
  }
  test("match against a short blocklist word") {
    val sqids = Sqids.withBlocklist(Blocklist(Set("pPQ")))
    assertEquals(sqids.decode(sqids.encodeUnsafeString(1, 2, 3)), List(1, 2, 3))
  }
}
