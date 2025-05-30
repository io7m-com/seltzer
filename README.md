seltzer
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.seltzer/com.io7m.seltzer.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.seltzer%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.seltzer/com.io7m.seltzer?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/seltzer/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/seltzer.svg?style=flat-square)](https://codecov.io/gh/io7m-com/seltzer)
![Java Version](https://img.shields.io/badge/17-java?label=java&color=e65cc3)

![com.io7m.seltzer](./src/site/resources/seltzer.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/seltzer/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/seltzer/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/seltzer/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/seltzer/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/seltzer/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/seltzer/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/seltzer/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/seltzer/actions?query=workflow%3Amain.windows.temurin.lts)|

## seltzer

A specification for structured, user-facing error message values.

### Features

  * Simple interfaces for exposing error information in a structured manner.
  * Convenient builders for structured error values.
  * Written in pure Java 17.
  * [OSGi](https://www.osgi.org/) ready.
  * [JPMS](https://en.wikipedia.org/wiki/Java_Platform_Module_System) ready.
  * ISC license.
  * High-coverage automated test suite.

### Motivation

Many applications and command-line tools need to produce error messages for
humans to read. Command-line tools will produce an error message as plain
text on the terminal, whilst graphical applications will probably produce
some sort of more complex error message dialog.

If a command-line application wants to include useful values along with the
error message such as the names of missing files, request IDs, and so on, the
only option it really has is to encode this information directly into the
error message. A GUI application, on the other hand, could display a basic
error message along with a graphical table of useful values. Library code wanting
to support both use-cases will typically bake some kind of structured error
type into the API. The `seltzer` package is an attempt to provide a set of
simple, structured API types so that [io7m](https://www.io7m.com/) packages
do not have to endlessly specify these kinds of structured error types over
and over.

Note: This is _not_ a machine-readable structured error logging API. For that,
use [OpenTelemetry](https://opentelemetry.io/).

### Building

```
$ mvn clean verify
```

### Usage

Application-specific structured error types can implement the
`SStructuredErrorType` interface. Application-specific exception types can
implement the `SStructuredErrorExceptionType` which contains some useful
default methods.

A structured error consists of the following:

|Item               |Type                  | Description |
|-------------------|----------------------|-------------|
|Message            |`String`              | A basic error message, such as "File not found".|
|Attributes         |`Map<String,String>`  | A key/value map containing attributes such as `("File", "file.txt")`.|
|Remediating Action |`Optional<String>`    | An optional, suggested remediating action such as "Specify a file that actually exists."|
|Error Code         |`T`                   | An error code that uniquely identifies the _type_ of error, such as `error-file-not-found`|
|Exception          |`Optional<Throwable>` | An optional exception. |

Structured error values are indexed by an error code type `T` to allow for
applications to strictly control error codes (and not have random error codes
proliferate through codebases).

The API contains an immutable `SStructuredError` type that applications can
use if they don't want to implement the interfaces themselves.

```
var error =
  SStructuredError.builder("error-file-not-found", "File not found.")
    .withAttribute("File", file.toString())
    .withRemediatingAction("Use a file that exists.")
    .withException(ex)
    .build();
```

Applications catching exceptions can check if an `Exception` is a structured
error and display a more detailed error message if so:

```
try {
  run();
} catch (Exception e) {
  if (e instanceof SStructuredErrorType s) {
    showDetailedError(s);
  } else {
    showBasicError(e);
  }
}
```

