# Evals for the support bot. Build once with sbt stage, then run ./tinyeval.
# The worker starts the JVM and the client once, and answers every test case sent to it.
worker target/stage/bin/support

test_case "refund after the window" "Can I still return my shoes? They arrived 40 days ago."
  target src/main/scala/com/example/support/Support.scala#answer
  matches "30[- ]day"   # "30 days" and "30-day window" are both right
  judge "Says a refund isn't possible anymore, politely"

test_case "missing package" "My package never showed up and I'm getting really annoyed."
  target src/main/scala/com/example/support/Support.scala#answer
  matches "order (number|id|#)"
  judge "Asks for the order number before promising a refund or resend"

test_case "refund inside the window" "My shoes arrived last week and they don't fit. Can I send them back?"
  target src/main/scala/com/example/support/Support.scala#answer
  judge "Says they can return them for a refund"
