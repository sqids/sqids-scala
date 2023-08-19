package sqids.options

import scala.util.control.NoStackTrace

final case class InvalidSqidsOptions(override val getMessage: String)
  extends RuntimeException
  with NoStackTrace
sealed abstract case class SqidsOptions(
  alphabet: Alphabet,
  minLength: Int,
  blocklist: Blocklist
) {
  def withBlocklist(blocklist: Blocklist): SqidsOptions = new SqidsOptions(
    alphabet,
    minLength,
    blocklist.filter(alphabet)
  ) {}

  def withAlphabet(alphabet: Alphabet): Either[InvalidSqidsOptions, SqidsOptions] = SqidsOptions.apply(
    alphabet,
    minLength,
    blocklist
  )

  def withMinLength(minLength: Int): Either[InvalidSqidsOptions, SqidsOptions] = SqidsOptions.apply(
    alphabet,
    minLength,
    blocklist
  )
}

object SqidsOptions {
  def apply(
    alphabet: Alphabet,
    minLength: Int,
    blocklist: Blocklist
  ): Either[InvalidSqidsOptions, SqidsOptions] =
    if (minLength < 0)
      Left(InvalidSqidsOptions("minLength need to be > 0"))
    else if (minLength > alphabet.length)
      Left(InvalidSqidsOptions("minLength cant be larger than alphabet length"))
    else
      Right(
        new SqidsOptions(
          alphabet,
          minLength,
          blocklist.filter(alphabet)
        ) {}
      )

  def default: SqidsOptions = new SqidsOptions(
    alphabet = Alphabet.default,
    minLength = 0,
    blocklist = Blocklist.default
  ) {}
}
