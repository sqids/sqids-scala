package sqids

import munit.ScalaCheckSuite
import sqids.options.SqidsOptions

final class SqidsSuite_minLength extends ScalaCheckSuite {

  val sqids = SqidsOptions.default.withMinLength(SqidsOptions.default.alphabet.length).map(Sqids.apply)
  sqids.foreach { sqids =>
    test("simple") {
      val numbers = List(1, 2, 3);
      val id = "75JILToVsGerOADWmHlY38xvbaNZKQ9wdFS0B6kcMEtnRpgizhjU42qT1cd0dL"
      assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
      assertEquals(sqids.decode(id), numbers)
    }

    test("incremental numbers") {
      val ids = Map(
        "jf26PLNeO5WbJDUV7FmMtlGXps3CoqkHnZ8cYd19yIiTAQuvKSExzhrRghBlwf" -> List(
          0,
          0
        ),
        "vQLUq7zWXC6k9cNOtgJ2ZK8rbxuipBFAS10yTdYeRa3ojHwGnmMV4PDhESI2jL" -> List(
          0,
          1
        ),
        "YhcpVK3COXbifmnZoLuxWgBQwtjsSaDGAdr0ReTHM16yI9vU8JNzlFq5Eu2oPp" -> List(
          0,
          2
        ),
        "OTkn9daFgDZX6LbmfxI83RSKetJu0APihlsrYoz5pvQw7GyWHEUcN2jBqd4kJ9" -> List(
          0,
          3
        ),
        "h2cV5eLNYj1x4ToZpfM90UlgHBOKikQFvnW36AC8zrmuJ7XdRytIGPawqYEbBe" -> List(
          0,
          4
        ),
        "7Mf0HeUNkpsZOTvmcj836P9EWKaACBubInFJtwXR2DSzgYGhQV5i4lLxoT1qdU" -> List(
          0,
          5
        ),
        "APVSD1ZIY4WGBK75xktMfTev8qsCJw6oyH2j3OnLcXRlhziUmpbuNEar05QCsI" -> List(
          0,
          6
        ),
        "P0LUhnlT76rsWSofOeyRGQZv1cC5qu3dtaJYNEXwk8Vpx92bKiHIz4MgmiDOF7" -> List(
          0,
          7
        ),
        "xAhypZMXYIGCL4uW0te6lsFHaPc3SiD1TBgw5O7bvodzjqUn89JQRfk2Nvm4JI" -> List(
          0,
          8
        ),
        "94dRPIZ6irlXWvTbKywFuAhBoECQOVMjDJp53s2xeqaSzHY8nc17tmkLGwfGNl" -> List(
          0,
          9
        )
      )

      ids.foreach { case (id, numbers) =>
        assertEquals(sqids.encodeUnsafeString(numbers: _*), id)
        assertEquals(sqids.decode(id), numbers)
      }

    }

    test("min lengths") {
      List(0, 1, 5, 10, SqidsOptions.default.alphabet.value.length).foreach(minLength =>
        List(
          List(sqids.minValue),
          List(0, 0, 0, 0, 0),
          List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          List(100, 200, 300),
          List(1000, 2000, 3000),
          List(1000000),
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
