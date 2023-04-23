/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.seltzer.api;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A structured error value.
 *
 * @param errorCode         The error code
 * @param message           The error message
 * @param attributes        The error attributes
 * @param remediatingAction The remediating action, if any
 * @param exception         The exception, if any
 * @param <C>               The type of error codes
 */

public record SStructuredError<C>(
  C errorCode,
  String message,
  Map<String, String> attributes,
  Optional<String> remediatingAction,
  Optional<Throwable> exception)
  implements SStructuredErrorType<C>
{
  /**
   * A structured error value.
   *
   * @param errorCode         The error code
   * @param message           The error message
   * @param attributes        The error attributes
   * @param remediatingAction The remediating action, if any
   * @param exception         The exception, if any
   */

  public SStructuredError
  {
    Objects.requireNonNull(errorCode, "errorCode");
    Objects.requireNonNull(message, "message");
    Objects.requireNonNull(attributes, "attributes");
    Objects.requireNonNull(remediatingAction, "remediatingAction");
    Objects.requireNonNull(exception, "exception");
  }
}
