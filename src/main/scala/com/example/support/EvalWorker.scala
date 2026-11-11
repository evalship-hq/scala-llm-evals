package com.example.support

import com.evalship.tinyeval.Tinyeval

/** tinyeval's worker: the JVM and the client start once per worker, not once per test case.
  * When the cause isn't the app (no key, a rate limit, a server error, a timeout or the network),
  * tinyeval counts the test case as couldn't run. Any other error, like a bad model name or a bug in the app,
  * is the reply, so the eval fails.
  */
@main def evalWorker(): Unit =
  lazy val support = Support() // built on the first input, once Tinyeval.worker has moved System.out to stderr
  Tinyeval.worker(input => support.answer(input))
