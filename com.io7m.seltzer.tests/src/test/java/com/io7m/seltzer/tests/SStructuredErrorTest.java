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


package com.io7m.seltzer.tests;

import com.io7m.seltzer.api.SStructuredError;
import com.io7m.seltzer.api.SStructuredErrorExceptionType;
import com.io7m.seltzer.api.SStructuredErrorType;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Structured error tests.
 */

public final class SStructuredErrorTest
{
  @Provide
  public Arbitrary<Throwable> throwables()
  {
    return Arbitraries.strings()
      .map(IOException::new);
  }

  record OtherError<C>(
    C errorCode,
    String message,
    Map<String, String> attributes,
    Optional<String> remediatingAction,
    Optional<Throwable> exception)
    implements SStructuredErrorType<C>
  {

  }

  final class Ex extends Exception
    implements SStructuredErrorExceptionType<String>
  {
    private final String errorCode;
    private final Map<String, String> attributes;
    private final Optional<String> remediatingAction;
    private final Optional<Throwable> exception;

    Ex(
      final String errorCode,
      final String message,
      final Map<String, String> attributes,
      final Optional<String> remediatingAction,
      final Optional<Throwable> exception)
    {
      super(message);
      this.errorCode = errorCode;
      this.attributes = attributes;
      this.remediatingAction = remediatingAction;
      this.exception = exception;
    }

    @Override
    public String errorCode()
    {
      return this.errorCode;
    }

    @Override
    public Map<String, String> attributes()
    {
      return this.attributes;
    }

    @Override
    public Optional<String> remediatingAction()
    {
      return this.remediatingAction;
    }

    @Override
    public Optional<Throwable> exception()
    {
      return this.exception;
    }
  }

  /**
   * Mutable builders work as expected.
   *
   * @param errorCode  The error code
   * @param message1   A message
   * @param message2   A message
   * @param action     An action
   * @param attributes1 A set of attributes
   * @param exception  An exception
   */

  @Property
  public void testBuilder(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll String message2,
    final @ForAll String action,
    final @ForAll Map<String, String> attributes1,
    final @ForAll Map<String, String> attributes2,
    final @ForAll("throwables") Throwable exception)
  {
    var builder =
      SStructuredError.builder(errorCode, message1);

    for (final var entry : attributes1.entrySet()) {
      builder = builder.withAttribute(entry.getKey(), entry.getValue());
    }

    builder = builder.withAttributes(attributes2);
    builder = builder.withMessage(message2);
    builder = builder.withException(exception);
    builder = builder.withRemediatingAction(action);

    final var all =
      Stream.concat(
        attributes1.entrySet().stream(),
        attributes2.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (s0, s1) -> s1));

    final var error = builder.build();
    assertEquals(message2, error.message());
    assertEquals(errorCode, error.errorCode());
    assertEquals(all, error.attributes());
    assertEquals(exception, error.exception().orElseThrow());
    assertEquals(action, error.remediatingAction().orElseThrow());
  }

  /**
   * Mutable builders work as expected.
   *
   * @param errorCode  The error code
   * @param message1   A message
   * @param message2   A message
   * @param action     An action
   * @param attributes A set of attributes
   * @param exception  An exception
   */

  @Property
  public void testBuilderOther(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll String message2,
    final @ForAll String action,
    final @ForAll Map<String, String> attributes,
    final @ForAll("throwables") Throwable exception)
  {
    var builder =
      SStructuredError.builder(errorCode, message1);

    for (final var entry : attributes.entrySet()) {
      builder = builder.withAttribute(entry.getKey(), entry.getValue());
    }

    builder = builder.withMessage(message2);
    builder = builder.withException(exception);
    builder = builder.withRemediatingAction(action);

    final var error = builder.build(OtherError::new);
    assertEquals(message2, error.message());
    assertEquals(errorCode, error.errorCode());
    assertEquals(attributes, error.attributes());
    assertEquals(exception, error.exception().orElseThrow());
    assertEquals(action, error.remediatingAction().orElseThrow());
  }

  /**
   * Mutable builders work as expected.
   *
   * @param errorCode  The error code
   * @param message1   A message
   * @param message2   A message
   * @param action     An action
   * @param attributes A set of attributes
   * @param exception  An exception
   */

  @Property
  public void testBuilderException(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll String message2,
    final @ForAll String action,
    final @ForAll Map<String, String> attributes,
    final @ForAll("throwables") Throwable exception)
  {
    var builder =
      SStructuredError.builder(errorCode, message1);

    for (final var entry : attributes.entrySet()) {
      builder = builder.withAttribute(entry.getKey(), entry.getValue());
    }

    builder = builder.withMessage(message2);
    builder = builder.withException(exception);
    builder = builder.withRemediatingAction(action);

    final var error = builder.build(Ex::new);
    assertEquals(message2, error.message());
    assertEquals(errorCode, error.errorCode());
    assertEquals(attributes, error.attributes());
    assertEquals(exception, error.exception().orElseThrow());
    assertEquals(action, error.remediatingAction().orElseThrow());
  }

  /**
   * An error with only a code and message works.
   *
   * @param errorCode The error code
   * @param message1  A message
   */

  @Property
  public void testWithOnlyMessage(
    final @ForAll String errorCode,
    final @ForAll String message1)
  {
    final var error =
      SStructuredError.withMessageOnly(errorCode, message1);

    assertEquals(message1, error.message());
    assertEquals(errorCode, error.errorCode());
    assertEquals(Map.of(), error.attributes());
    assertEquals(Optional.empty(), error.exception());
    assertEquals(Optional.empty(), error.remediatingAction());
  }
}
