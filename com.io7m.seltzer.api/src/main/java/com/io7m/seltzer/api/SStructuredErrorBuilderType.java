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

import org.osgi.annotation.versioning.ConsumerType;

import java.util.Map;

/**
 * The type of mutable builders that can construct structured errors.
 *
 * @param <C> The type of error codes
 */

@ConsumerType
public interface SStructuredErrorBuilderType<C>
{
  /**
   * Update the message.
   *
   * @param message The new message
   *
   * @return this
   */

  SStructuredErrorBuilderType<C> withMessage(
    String message);

  /**
   * Add an attribute.
   *
   * @param name  The attribute name
   * @param value The attribute value
   *
   * @return this
   */

  SStructuredErrorBuilderType<C> withAttribute(
    String name,
    String value);

  /**
   * Add all the given attributes.
   *
   * @param attributes  The attributes
   *
   * @return this
   */

  default SStructuredErrorBuilderType<C> withAttributes(
    final Map<String, String> attributes)
  {
    for (final var e : attributes.entrySet()) {
      this.withAttribute(e.getKey(), e.getValue());
    }
    return this;
  }

  /**
   * Update the remediating action.
   *
   * @param action The new action
   *
   * @return this
   */

  SStructuredErrorBuilderType<C> withRemediatingAction(
    String action);

  /**
   * Update the exception.
   *
   * @param exception The new exception
   *
   * @return this
   */

  SStructuredErrorBuilderType<C> withException(
    Throwable exception);

  /**
   * @param <T> The type of returned values
   * @param c   The constructor function
   *
   * @return A structured error based on the information so far
   */

  <T> T build(SStructuredErrorConstructorType<C, T> c);

  /**
   * @return An immutable structured error based on the information so far
   */

  default SStructuredError<C> build()
  {
    return this.build(SStructuredError::new);
  }
}
