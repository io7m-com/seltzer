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
import org.slf4j.MDC;
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
   * <p>The method calls {@link MDC#clear()} afterward.</p>
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
   * included as named MDC values.</p>
   *
   * <p>The method calls {@link MDC#clear()} afterward.</p>
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

    final var attributes = error.attributes();
    for (final var entry : attributes.entrySet()) {
      MDC.put(entry.getKey(), entry.getValue());
    }
    MDC.put(codeName, error.errorCode().toString());

    error.remediatingAction()
      .ifPresent(s -> MDC.put(remediatingName, s));

    final var exceptionOpt =
      error.exception();

    switch (level) {
      case ERROR -> {
        if (exceptionOpt.isPresent()) {
          log.error("{}: ", error.message(), exceptionOpt.get());
        } else {
          log.error("{}", error.message());
        }
      }
      case WARN -> {
        if (exceptionOpt.isPresent()) {
          log.warn("{}: ", error.message(), exceptionOpt.get());
        } else {
          log.warn("{}", error.message());
        }
      }
      case INFO -> {
        if (exceptionOpt.isPresent()) {
          log.info("{}: ", error.message(), exceptionOpt.get());
        } else {
          log.info("{}", error.message());
        }
      }
      case DEBUG -> {
        if (exceptionOpt.isPresent()) {
          log.debug("{}: ", error.message(), exceptionOpt.get());
        } else {
          log.debug("{}", error.message());
        }
      }
      case TRACE -> {
        if (exceptionOpt.isPresent()) {
          log.trace("{}: ", error.message(), exceptionOpt.get());
        } else {
          log.trace("{}", error.message());
        }
      }
    }

    MDC.clear();
  }
}
