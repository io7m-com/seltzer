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

import java.util.HashMap;
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

  /**
   * An error value with only the given error code and message.
   *
   * @param errorCode The error code
   * @param message   The error message
   * @param <C>       The type of error codes
   *
   * @return An error value
   */

  public static <C> SStructuredError<C> withMessageOnly(
    final C errorCode,
    final String message)
  {
    return new SStructuredError<>(
      errorCode,
      message,
      Map.of(),
      Optional.empty(),
      Optional.empty()
    );
  }

  /**
   * Create a new mutable builder for structured error values.
   *
   * @param errorCode The error code
   * @param message   The message
   * @param <C>       The type of error codes
   *
   * @return A new builder
   */

  public static <C> SStructuredErrorBuilderType<C> builder(
    final C errorCode,
    final String message)
  {
    return new Builder<C>(errorCode, message);
  }

  private static final class Builder<C> implements SStructuredErrorBuilderType<C>
  {
    private final C errorCode;
    private final HashMap<String, String> attributes;
    private String message;
    private Optional<String> remediatingAction;
    private Optional<Throwable> exception;

    private Builder(
      final C inErrorCode,
      final String inMessage)
    {
      this.errorCode =
        Objects.requireNonNull(inErrorCode, "errorCode");
      this.message =
        Objects.requireNonNull(inMessage, "message");
      this.attributes =
        new HashMap<>(4);
      this.remediatingAction =
        Optional.empty();
      this.exception =
        Optional.empty();
    }

    @Override
    public SStructuredErrorBuilderType<C> withMessage(
      final String newMessage)
    {
      this.message = Objects.requireNonNull(newMessage, "message");
      return this;
    }

    @Override
    public SStructuredErrorBuilderType<C> withAttribute(
      final String name,
      final String value)
    {
      this.attributes.put(
        Objects.requireNonNull(name, "name"),
        Objects.requireNonNull(value, "value")
      );
      return this;
    }

    @Override
    public SStructuredErrorBuilderType<C> withRemediatingAction(
      final String newAction)
    {
      this.remediatingAction = Optional.of(newAction);
      return this;
    }

    @Override
    public SStructuredErrorBuilderType<C> withException(
      final Throwable newException)
    {
      this.exception = Optional.of(newException);
      return this;
    }

    @Override
    public <T> T build(
      final SStructuredErrorConstructorType<C, T> c)
    {
      return c.construct(
        this.errorCode,
        this.message,
        Map.copyOf(this.attributes),
        this.remediatingAction,
        this.exception
      );
    }
  }
}
