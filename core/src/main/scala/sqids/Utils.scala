/*
 * Copyright 2023 Sqids
 *
 * SPDX-License-Identifier: MIT
 */

package sqids

private object Utils {

  implicit class StringOps(private val s: String) extends AnyVal {
    def tailOrEmpty: String = s.slice(1, s.length)
  }

}
