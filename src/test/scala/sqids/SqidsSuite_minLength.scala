/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import munit.ScalaCheckSuite
import sqids.options.SqidsOptions

final class SqidsSuite_minLength extends ScalaCheckSuite {

  val sqids = SqidsOptions.default.withMinLength(SqidsOptions.default.alphabet.length).map(Sqids.apply)
  sqids.foreach { sqids =>
    test("simple") {
      val numbers: List[Long] = List(1, 2, 3)
      val id = "86Rf07xd4zBmiJXQG6otHEbew02c3PWsUOLZxADhCpKj7aVFv9I8RquYrNlSTM"
      assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
      assertEquals(sqids.decode(id), numbers)
    }

    test("incremental numbers") {
      val ids = Map(
        "SvIzsqYMyQwI3GWgJAe17URxX8V924Co0DaTZLtFjHriEn5bPhcSkfmvOslpBu" -> List(0, 0),
        "n3qafPOLKdfHpuNw3M61r95svbeJGk7aAEgYn4WlSjXURmF8IDqZBy0CT2VxQc" -> List(0, 1),
        "tryFJbWcFMiYPg8sASm51uIV93GXTnvRzyfLleh06CpodJD42B7OraKtkQNxUZ" -> List(0, 2),
        "eg6ql0A3XmvPoCzMlB6DraNGcWSIy5VR8iYup2Qk4tjZFKe1hbwfgHdUTsnLqE" -> List(0, 3),
        "rSCFlp0rB2inEljaRdxKt7FkIbODSf8wYgTsZM1HL9JzN35cyoqueUvVWCm4hX" -> List(0, 4),
        "sR8xjC8WQkOwo74PnglH1YFdTI0eaf56RGVSitzbjuZ3shNUXBrqLxEJyAmKv2" -> List(0, 5),
        "uY2MYFqCLpgx5XQcjdtZK286AwWV7IBGEfuS9yTmbJvkzoUPeYRHr4iDs3naN0" -> List(0, 6),
        "74dID7X28VLQhBlnGmjZrec5wTA1fqpWtK4YkaoEIM9SRNiC3gUJH0OFvsPDdy" -> List(0, 7),
        "30WXpesPhgKiEI5RHTY7xbB1GnytJvXOl2p0AcUjdF6waZDo9Qk8VLzMuWrqCS" -> List(0, 8),
        "moxr3HqLAK0GsTND6jowfZz3SUx7cQ8aC54Pl1RbIvFXmEJuBMYVeW9yrdOtin" -> List(0, 9)
      ).view.mapValues(_.map(_.toLong))

      ids.foreach { case (id, numbers) =>
        assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
        assertEquals(sqids.decode(id), numbers)
      }

    }

    test("min lengths") {
      List(0, 1, 5, 10, SqidsOptions.default.alphabet.value.length).foreach(minLength =>
        List(
          List(sqids.minValue),
          List(0L, 0L, 0L, 0L, 0L),
          List(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L),
          List(100L, 200L, 300L),
          List(1000L, 2000L, 3000L),
          List(1000000L),
          List(sqids.maxValue)
        ).foreach { numbers =>
          SqidsOptions.default.withMinLength(minLength).map(Sqids.apply).foreach { sqids =>
            val id = sqids.encodeUnsafeString(numbers: _*)
            assert(id.length >= minLength)
            assertEquals(sqids.decode(id), numbers)
          }
        }
      )
    }

    test("out-of-range invalid min length") {
      assert(SqidsOptions.default.withMinLength(minLength = -1).isLeft)
      assert(
        SqidsOptions.default.withMinLength(minLength = SqidsOptions.default.alphabet.value.length + 1).isLeft
      )
    }
  }
}
