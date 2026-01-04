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


package com.io7m.seltzer.slf4j;

import com.io7m.seltzer.api.SStructuredErrorType;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * Functions to log structured errors to SLF4J.
 */

public final class SSLogging
{
  private SSLogging()
  {

  }

  /**
   * <p>Log a structured error to the given logger at the given level. Attributes
   * are included as MDC values, and the error code and remediating action are
   * included as {@code ErrorCode} and {@code RemediatingAction} MDC values,
   * respectively.</p>
   *
   * @param log   The logger
   * @param level The level
   * @param error The error
   */

  public static void logMDC(
    final Logger log,
    final Level level,
    final SStructuredErrorType<?> error)
  {
    logMDCCode(log, level, "ErrorCode", "RemediatingAction", error);
  }

  /**
   * <p>Log a structured error to the given logger at the given level. Attributes
   * are included as MDC values, and the error code and remediating action are
   * included as {@code ErrorCode} and {@code RemediatingAction} MDC values,
   * respectively.</p>
   *
   * @param log   The logger
   * @param level The level
   * @param error The error
   * @param style The message style
   *
   * @since 1.3.0
   */

  public static void logMDCWithStyle(
    final Logger log,
    final Level level,
    final MessageStyle style,
    final SStructuredErrorType<?> error)
  {
    Objects.requireNonNull(log, "log");
    Objects.requireNonNull(level, "level");
    Objects.requireNonNull(style, "style");
    Objects.requireNonNull(error, "error");

    logMDCCodeWithStyle(
      log,
      level,
      "ErrorCode",
      "RemediatingAction",
      style,
      error
    );
  }

  /**
   * <p>Log a structured error to the given logger at the given level. Attributes
   * are included as MDC values, and the error code and remediating action are
   * included as named MDC values.</p>
   *
   * @param log             The logger
   * @param codeName        The name for the error code MDC key
   * @param remediatingName The name of the remediating action MDC key
   * @param level           The level
   * @param error           The error
   */

  public static void logMDCCode(
    final Logger log,
    final Level level,
    final String codeName,
    final String remediatingName,
    final SStructuredErrorType<?> error)
  {
    Objects.requireNonNull(log, "log");
    Objects.requireNonNull(level, "level");
    Objects.requireNonNull(codeName, "codeName");
    Objects.requireNonNull(remediatingName, "remediatingName");
    Objects.requireNonNull(error, "error");

    logMDCCodeEvent(
      log,
      level,
      codeName,
      remediatingName,
      error,
      MessageStyle.STYLE_MESSAGE_COLON_EXCEPTION
    );
  }

  /**
   * <p>Log a structured error to the given logger at the given level. Attributes
   * are included as MDC values, and the error code and remediating action are
   * included as named MDC values.</p>
   *
   * @param log             The logger
   * @param codeName        The name for the error code MDC key
   * @param remediatingName The name of the remediating action MDC key
   * @param level           The level
   * @param style           The message style
   * @param error           The error
   *
   * @since 1.3.0
   */

  public static void logMDCCodeWithStyle(
    final Logger log,
    final Level level,
    final String codeName,
    final String remediatingName,
    final MessageStyle style,
    final SStructuredErrorType<?> error)
  {
    Objects.requireNonNull(log, "log");
    Objects.requireNonNull(level, "level");
    Objects.requireNonNull(codeName, "codeName");
    Objects.requireNonNull(remediatingName, "remediatingName");
    Objects.requireNonNull(style, "style");
    Objects.requireNonNull(error, "error");

    logMDCCodeEvent(
      log,
      level,
      codeName,
      remediatingName,
      error,
      style
    );
  }

  private static void logMDCCodeEvent(
    final Logger log,
    final Level level,
    final String codeName,
    final String remediatingName,
    final SStructuredErrorType<?> error,
    final MessageStyle style)
  {
    final var exceptionOpt =
      error.exception();

    final var eventBuilder =
      switch (level) {
        case ERROR -> log.atError();
        case WARN -> log.atWarn();
        case INFO -> log.atInfo();
        case DEBUG -> log.atDebug();
        case TRACE -> log.atTrace();
      };

    final var attributes = error.attributes();
    for (final var entry : attributes.entrySet()) {
      eventBuilder.addKeyValue(entry.getKey(), entry.getValue());
    }
    eventBuilder.addKeyValue(codeName, error.errorCode().toString());

    error.remediatingAction()
      .ifPresent(s -> eventBuilder.addKeyValue(remediatingName, s));

    switch (style) {
      case STYLE_MESSAGE_COLON_EXCEPTION -> {
        if (exceptionOpt.isPresent()) {
          eventBuilder.log("{}: ", error.message(), exceptionOpt.get());
        } else {
          eventBuilder.log("{}", error.message());
        }
      }
      case STYLE_MESSAGE_ONLY -> {
        if (exceptionOpt.isPresent()) {
          eventBuilder.log("{}", error.message(), exceptionOpt.get());
        } else {
          eventBuilder.log("{}", error.message());
        }
      }
    }
  }

  /**
   * The message style used when exceptions are included in events.
   *
   * @since 1.3.0
   */

  public enum MessageStyle
  {
    /**
     * Use a logback format such as '{}: '. This is the default.
     */

    STYLE_MESSAGE_COLON_EXCEPTION,

    /**
     * Use a logback format such as '{}'. In other words, assume that
     * the underlying log backend will print the message and exception
     * stacktrace separately, so don't format the message in a manner
     * that expects the stacktrace to immediately follow it.
     */

    STYLE_MESSAGE_ONLY
  }
}
