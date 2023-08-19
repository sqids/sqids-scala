package sqids

import scala.annotation.tailrec
import sqids.options.Alphabet
import sqids.options.Blocklist

final case class Sqid(
  value: String,
  alphabet: Alphabet,
  numbers: List[Int],
  partitioned: Boolean,
  originalAlphabet: Alphabet
) {
  override def toString = value
  def withNextnr(nr: Int) = append(alphabet.removeSeparator.toId(nr))
  def addSeparator = append(alphabet.separator.toString)
  def addPartitionOrSeparator(partition: String, shouldAddPartition: Boolean) =
    if (shouldAddPartition) append(partition)
    else addSeparator
  def append(s: String) = copy(value = value + s)
  def length = value.length
  def fillToMinLength(minLength: Int): Sqid =
    copy(value =
      value.head.toString +
        alphabet.value.take(minLength - length) +
        value.drop(1).take(length)
    )
  def shuffle = copy(alphabet = alphabet.shuffle)

  def handleBlocked(blocklist: Blocklist, maxValue: Int): Either[SqidsError, Sqid] =
    if (blocklist.isBlocked(value)) {
      val newNumbers: Either[SqidsError, List[Int]] =
        if (partitioned)
          // Here we have a true cornercase, we have to find
          // 2 147 483 647 sequential iterations of a blocked id
          // before this happens 😅
          if (numbers.head + 1 > maxValue)
            Left(SqidsError.OutOfRange("Ran out of range checking against the blocklist"))
          else
            Right(numbers.head + 1 :: numbers.tail)
        else
          Right(0 :: numbers)

      newNumbers.flatMap(numbers =>
        Sqid
          .fromNumbers(numbers, originalAlphabet, true)
          .handleBlocked(blocklist, maxValue)
      )
    } else Right(this)

  def handleMinLength(minLength: Int): Sqid =
    if (length < minLength)
      if (!partitioned)
        Sqid
          .fromNumbers(0 :: numbers, originalAlphabet, true)
          .handleMinLength(minLength)
      else
        fillToMinLength(minLength)
    else this
}

object Sqid {
  def fromNumbers(
    numbers: List[Int],
    a: Alphabet,
    partitioned: Boolean
  ): Sqid = {
    val alphabet = a.rearrange(numbers)

    @tailrec
    def go(
      numbers: List[Int],
      sqid: Sqid,
      first: Boolean
    ): Sqid =
      numbers match {
        case Nil => sqid.copy(value = "")
        case List(nr) => sqid.withNextnr(nr)
        case nr :: next =>
          go(
            numbers = next,
            sqid = sqid
              .withNextnr(nr)
              .addPartitionOrSeparator(
                alphabet.partition.toString,
                first && partitioned
              )
              .shuffle,
            first = false
          )
      }

    go(
      numbers = numbers,
      sqid = Sqid(
        value = alphabet.prefix.toString,
        alphabet = alphabet.removePrefixAndPartition,
        numbers = numbers,
        partitioned = partitioned,
        originalAlphabet = a
      ),
      first = true
    )
  }
}
