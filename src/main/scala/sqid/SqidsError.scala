/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

import scala.util.control.NoStackTrace

sealed trait SqidsError extends RuntimeException with NoStackTrace

object SqidsError {
  final case class OutOfRange(override val getMessage: String) extends SqidsError

  case object AlphabetMultibyteChars extends SqidsError {
    override def getMessage(): String = "Alphabet cannot contain multibyte characters"
  }
  case object AlphabetTooSmall extends SqidsError {
    override def getMessage(): String = "Alphabet must contain more than 5 characters"
  }

  case object AlphabetNotUnique extends SqidsError {
    override def getMessage(): String = "Alphabet must contain unique characters"
  }

  final case class EncodeError(override val getMessage: String) extends SqidsError

  case object RegenerationMaxAttempts extends SqidsError {
    override def getMessage(): String = "Reached max attempts to re-generate the ID"
  }
}
