package sqids

import scala.util.control.NoStackTrace

sealed trait SqidsError extends RuntimeException with NoStackTrace

object SqidsError {
  final case class OutOfRange(override val getMessage: String) extends SqidsError

  case object AlphabetTooSmall extends SqidsError {
    override def getMessage(): String = "Alphabet must contain more than 5 characters"
  }

  case object AlphabetNotUnique extends SqidsError {
    override def getMessage(): String = "Alphabet must contain unique characters"
  }

  final case class EncodeError(override val getMessage: String) extends SqidsError
}
