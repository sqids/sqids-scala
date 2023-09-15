/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids
import munit.ScalaCheckSuite
import sqids.options.Blocklist
import sqids.options.SqidsOptions
import sqids.options.Alphabet

final class BlocklistSuite extends ScalaCheckSuite {
  val sqids = Sqids.default

  test("if no custom blocklist param, use the default blocklist") {
    assertEquals(sqids.decode("aho1e"), List(4572721L))
    assertEquals(sqids.encodeUnsafeString(4572721L), "JExTR")
  }

  test("if an empty blocklist param passed, don't use any blocklist") {
    val sqids = Sqids.withBlocklist(Blocklist.empty)
    assertEquals(sqids.decode("aho1e"), List(4572721L))
    assertEquals(sqids.encodeUnsafeString(4572721L), "aho1e")
  }

  test("if a non-empty blocklist param passed, use only that") {
    val sqids = Sqids.withBlocklist(Blocklist(Set("ArUO")))

    // make sure we don't use the default blocklist
    assertEquals(sqids.decode("aho1e"), List(4572721L))
    assertEquals(sqids.encodeUnsafeString(4572721L), "aho1e")

    // make sure we are using the passed blocklist
    assertEquals(sqids.decode("ArUO"), List(100000L))
    assertEquals(sqids.encodeUnsafeString(100000L), "QyG4")
    assertEquals(sqids.decode("QyG4"), List(100000L))
  }

  test("blocklist") {
    val sqids = Sqids.withBlocklist(
      Blocklist(
        Set(
          "JSwXFaosAN", // normal result of 1st encoding, let's block that word on purpose
          "OCjV9JK64o", // result of 2nd encoding
          "rBHf", // result of 3rd encoding is `4rBHfOiqd3`, let's block a substring
          "79SM", // result of 4th encoding is `dyhgw479SM`, let's block the postfix
          "7tE6", // result of 4th encoding is `7tE6jdAHLe`, let's block the prefix
        )
      )
    )
    assertEquals(sqids.encodeUnsafeString(1_000_000L, 2_000_000L), "1aYeB7bRUt")
    assertEquals(sqids.decode("1aYeB7bRUt"), List(1_000_000L, 2_000_000L));
  }

  test("decoding blocklist words should still work") {
    val blocklist = Set(
      "86Rf07",
      "se8ojk",
      "ARsz1p",
      "Q8AI49",
      "5sQRZO"
    )
    val sqids = Sqids.withBlocklist(
      Blocklist(
        blocklist
      )
    )
    blocklist.foreach(id => assertEquals(sqids.decode(id), List(1L, 2L, 3L)))
  }
  test("match against a short blocklist word") {
    val sqids = Sqids.withBlocklist(Blocklist(Set("pnd")))
    assertEquals(sqids.decode(sqids.encodeUnsafeString(1_000)), List(1_000L))
  }

  test("blocklist filtering in constructor") {
    for {
      alpha <- Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
      blocklist = Blocklist(Set("sxnzkl"))
      withAlphabet <- SqidsOptions.default.withAlphabet(alpha)
      options = withAlphabet.withBlocklist(blocklist)
      sqids = Sqids(options)
      id <- sqids.encode(1, 2, 3)
      numbers = sqids.decode(id.value)
    } yield {
      assertEquals(id.value, "IBSHOZ")
      assertEquals(numbers, List(1L, 2L, 3L))
    }

  }
}
