/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
import com.io7m.seltzer.slf4j.SSLogging;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SSLoggingTest
{
  private record LogEvent(
    Level level,
    Marker marker,
    String messagePattern,
    Object[] arguments,
    Throwable throwable)
  {

  }

  private static final class CapturingLogger
    extends AbstractLogger
  {
    LinkedList<LogEvent> events = new LinkedList<>();

    @Override
    protected String getFullyQualifiedCallerName()
    {
      return "x";
    }

    @Override
    protected void handleNormalizedLoggingCall(
      final Level level,
      final Marker marker,
      final String messagePattern,
      final Object[] arguments,
      final Throwable throwable)
    {
      this.events.add(
        new LogEvent(
          level,
          marker,
          messagePattern,
          arguments,
          throwable
        )
      );
    }

    @Override
    public boolean isTraceEnabled()
    {
      return true;
    }

    @Override
    public boolean isTraceEnabled(
      final Marker marker)
    {
      return true;
    }

    @Override
    public boolean isDebugEnabled()
    {
      return true;
    }

    @Override
    public boolean isDebugEnabled(
      final Marker marker)
    {
      return true;
    }

    @Override
    public boolean isInfoEnabled()
    {
      return true;
    }

    @Override
    public boolean isInfoEnabled(
      final Marker marker)
    {
      return true;
    }

    @Override
    public boolean isWarnEnabled()
    {
      return true;
    }

    @Override
    public boolean isWarnEnabled(
      final Marker marker)
    {
      return true;
    }

    @Override
    public boolean isErrorEnabled()
    {
      return true;
    }

    @Override
    public boolean isErrorEnabled(
      final Marker marker)
    {
      return true;
    }
  }

  @TestFactory
  public Stream<DynamicTest> testLoggingWithException()
  {
    final var logger =
      new CapturingLogger();

    return Stream.of(Level.values())
      .map(level -> {
        return DynamicTest.dynamicTest(
          "testLogging_%s".formatted(level),
          () -> {
            SSLogging.logMDCCode(
              logger,
              level,
              "CODE",
              "REMEDIATING",
              new SStructuredError<>(
                "ErrorCodeName",
                "A message.",
                Map.ofEntries(
                  Map.entry("a", "x"),
                  Map.entry("b", "y"),
                  Map.entry("c", "z")
                ),
                Optional.of("Some action."),
                Optional.of(new IOException("Printer on fire."))
              )
            );

            final var event = logger.events.remove();
            assertEquals(level, event.level);
            System.out.println(event);
          });
      });
  }

  @TestFactory
  public Stream<DynamicTest> testLoggingWithoutException()
  {
    final var logger =
      new CapturingLogger();

    return Stream.of(Level.values())
      .map(level -> {
        return DynamicTest.dynamicTest(
          "testLogging_%s".formatted(level),
          () -> {
            SSLogging.logMDC(
              logger,
              level,
              new SStructuredError<>(
                "ErrorCodeName",
                "A message.",
                Map.ofEntries(
                  Map.entry("a", "x"),
                  Map.entry("b", "y"),
                  Map.entry("c", "z")
                ),
                Optional.of("Some action."),
                Optional.empty()
              )
            );

            final var event = logger.events.remove();
            assertEquals(level, event.level);
            System.out.println(event);
          });
      });
  }
}
