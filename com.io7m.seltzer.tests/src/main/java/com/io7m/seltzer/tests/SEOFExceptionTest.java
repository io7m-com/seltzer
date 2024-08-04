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

import com.io7m.seltzer.io.SEOFException;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Structured error tests.
 */

public final class SEOFExceptionTest
{
  @Provide
  public Arbitrary<Throwable> throwables()
  {
    return Arbitraries.strings()
      .map(IOException::new);
  }

  /**
   * Exception fields work.
   *
   * @param errorCode   The error code
   * @param message1    A message
   * @param action      An action
   * @param attributes1 A set of attributes
   * @param exception   An exception
   */

  @Property
  public void testException0(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll String action,
    final @ForAll Map<String, String> attributes1,
    final @ForAll("throwables") Throwable exception)
  {
    final var sioException =
      new SEOFException(
        message1,
        exception,
        errorCode,
        attributes1,
        Optional.of(action)
      );

    assertEquals(message1, sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(attributes1, sioException.attributes());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(action, sioException.remediatingAction().orElseThrow());
  }

  /**
   * Exception fields work.
   *
   * @param errorCode The error code
   * @param message1  A message
   * @param exception An exception
   */

  @Property
  public void testException0(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll("throwables") Throwable exception)
  {
    final var sioException =
      new SEOFException(
        message1,
        exception,
        errorCode
      );

    assertEquals(message1, sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(Optional.empty(), sioException.remediatingAction());
  }

  /**
   * Exception fields work.
   *
   * @param errorCode   The error code
   * @param message1    A message
   * @param attributes1 A set of attributes
   * @param exception   An exception
   */

  @Property
  public void testException2(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll Map<String, String> attributes1,
    final @ForAll("throwables") Throwable exception)
  {
    final var sioException =
      new SEOFException(
        message1,
        exception,
        errorCode,
        attributes1
      );

    assertEquals(message1, sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(attributes1, sioException.attributes());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(Optional.empty(), sioException.remediatingAction());
  }

  /**
   * Exception fields work.
   *
   * @param errorCode   The error code
   * @param message1    A message
   * @param attributes1 A set of attributes
   */

  @Property
  public void testException3(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll String action,
    final @ForAll Map<String, String> attributes1)
  {
    final var sioException =
      new SEOFException(
        message1,
        errorCode,
        attributes1,
        Optional.of(action)
      );

    assertEquals(message1, sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(attributes1, sioException.attributes());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(Optional.of(action), sioException.remediatingAction());
  }

  /**
   * Exception fields work.
   *
   * @param errorCode   The error code
   * @param action      An action
   * @param attributes1 A set of attributes
   * @param exception   An exception
   */

  @Property
  public void testException0(
    final @ForAll String errorCode,
    final @ForAll String action,
    final @ForAll Map<String, String> attributes1,
    final @ForAll("throwables") Throwable exception)
  {
    final var sioException =
      new SEOFException(
        exception,
        errorCode,
        attributes1,
        Optional.of(action)
      );

    assertEquals(exception.getMessage(), sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(attributes1, sioException.attributes());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(action, sioException.remediatingAction().orElseThrow());
  }

  /**
   * Exception fields work.
   *
   * @param errorCode The error code
   * @param action    An action
   * @param exception An exception
   */

  @Property
  public void testException4(
    final @ForAll String errorCode,
    final @ForAll String action,
    final @ForAll("throwables") Throwable exception)
  {
    final var sioException =
      new SEOFException(
        exception,
        errorCode,
        Optional.of(action)
      );

    assertEquals(exception.getMessage(), sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(Map.of(), sioException.attributes());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(action, sioException.remediatingAction().orElseThrow());
  }

  /**
   * Exception fields work.
   *
   * @param message1  The message
   * @param errorCode The error code
   * @param action    An action
   */

  @Property
  public void testException5(
    final @ForAll String message1,
    final @ForAll String errorCode,
    final @ForAll String action)
  {
    final var sioException =
      new SEOFException(
        message1,
        errorCode,
        Optional.of(action)
      );

    assertEquals(message1, sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(action, sioException.remediatingAction().orElseThrow());
  }

  /**
   * Exception fields work.
   *
   * @param errorCode The error code
   * @param message1  A message
   * @param action    An action
   * @param exception An exception
   */

  @Property
  public void testException6(
    final @ForAll String errorCode,
    final @ForAll String message1,
    final @ForAll String action,
    final @ForAll("throwables") Throwable exception)
  {
    final var sioException =
      new SEOFException(
        message1,
        exception,
        errorCode,
        Optional.of(action)
      );

    assertEquals(message1, sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(Map.of(), sioException.attributes());
    assertEquals(sioException, sioException.exception().orElseThrow());
    assertEquals(action, sioException.remediatingAction().orElseThrow());
  }

  /**
   * Exception fields work.
   *
   * @param message     The message
   * @param errorCode   The error code
   * @param attributes1 A set of attributes
   */

  @Property
  public void testException7(
    final @ForAll String message,
    final @ForAll String errorCode,
    final @ForAll Map<String, String> attributes1)
  {
    final var sioException =
      new SEOFException(
        message,
        errorCode,
        attributes1
      );

    assertEquals(message, sioException.message());
    assertEquals(errorCode, sioException.errorCode());
    assertEquals(attributes1, sioException.attributes());
    assertEquals(sioException, sioException.exception().orElseThrow());
  }
}
