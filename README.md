# [Sqids Scala](https://sqids.org/scala)

[![Release](https://index.scala-lang.org/sqids/sqids-scala/latest.svg?color=orange)](https://index.scala-lang.org/sqids/sqids-scala)
[![Tests](https://github.com/sqids/sqids-scala/actions/workflows/ci.yml/badge.svg)](https://github.com/sqids/sqids-scala/actions/workflows/ci.yml) 
[![License](https://img.shields.io/badge/license-MIT-blue)](https://github.com/sqids/sqids-scala/blob/main/LICENSE)

[Sqids](https://sqids.org/scala) (*pronounced "squids"*) is a small library that lets you **generate unique IDs from numbers**. It's good for link shortening, fast & URL-safe ID generation and decoding back into numbers for quicker database lookups.

Features:

- **Encode multiple numbers** - generate short IDs from one or several non-negative numbers
- **Quick decoding** - easily decode IDs back into numbers
- **Unique IDs** - generate unique IDs by shuffling the alphabet once
- **ID padding** - provide minimum length to make IDs more uniform
- **URL safe** - auto-generated IDs do not contain common profanity
- **Randomized output** - Sequential input provides nonconsecutive IDs
- **Many implementations** - Support for [40+ programming languages](https://sqids.org/)

## 🧰 Use-cases

Good for:

- Generating IDs for public URLs (eg: link shortening)
- Generating IDs for internal systems (eg: event tracking)
- Decoding for quicker database lookups (eg: by primary keys)

Not good for:

- Sensitive data (this is not an encryption library)
- User IDs (can be decoded revealing user count)

## 🚀 Getting started

Include in build.sbt:

```scala
libraryDependencies ++= "org.sqids" %% "sqids" % "0.6.0"
```

## 👩‍💻 Examples

Simple encode & decode:

```scala
import sqids.Sqids
val sqids = Sqids.default
val id = sqids.encodeUnsafeString(1, 2, 3)
// id: String = "86Rf07"
val numbers = sqids.decode(id) 
// numbers: List[Int] = List(1, 2, 3)
```

> **Note**
> 🚧 Because of the algorithm's design, **multiple IDs can decode back into the same sequence of numbers**. If it's important to your design that IDs are canonical, you have to manually re-encode decoded numbers and check that the generated ID matches.

Randomize IDs by providing a custom alphabet:

```scala
import sqids.options.Alphabet
import sqids.Sqids

Alphabet("FxnXM1kBN6cuhsAvjW3Co7l2RePyY8DwaU04Tzt9fHQrqSVKdpimLGIJOgb5ZE")
  .flatMap(Sqids.forAlphabet)
  .foreach { sqids =>
    val id = sqids.encodeUnsafeString(1, 2, 3)
    println(id) 
    // B4aajs
    println(sqids.decode(id)) 
    // List(1, 2, 3)
  }
```

Enforce a *minimum* length for IDs:

```scala
import sqids.Sqids

Sqids
  .withMinLength(10)
  .foreach { sqids =>
    val id = sqids.encodeUnsafeString(1, 2, 3)
    println(id) 
    // 86Rf07xd4z
    println(sqids.decode(id)) 
    // List(1, 2, 3)
  }
```

Prevent specific words from appearing anywhere in the auto-generated IDs:

(Blocked ids/words does still decode to correct numbers)
```scala
import sqids.options.Blocklist
import sqids.Sqids

val sqids = Sqids.withBlocklist(Blocklist(Set("86Rf07", "se8ojk")))
val id = sqids.encodeUnsafeString(1, 2, 3) 
// id: String = "ARsz1p"

sqids.decode(id)
// List(1, 2, 3)
sqids.decode("86Rf07")
// List(1, 2, 3)
sqids.decode("se8ojk")
// List(1, 2, 3)

```

## 📝 License

[MIT](LICENSE)
