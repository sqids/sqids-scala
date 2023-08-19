package sqids

import scala.util.control.NoStackTrace

sealed trait SqidsError extends RuntimeException with NoStackTrace

object SqidsError {
  final case class OutOfRange(override val getMessage: String) extends SqidsError

  final case class AlphabetTooSmall(override val getMessage: String) extends SqidsError

  final case class AlphabetNotUnique(override val getMessage: String) extends SqidsError

  final case class EncodeError(override val getMessage: String) extends SqidsError
}
