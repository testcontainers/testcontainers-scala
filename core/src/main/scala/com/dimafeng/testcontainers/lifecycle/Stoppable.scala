package com.dimafeng.testcontainers.lifecycle

trait Stoppable extends AutoCloseable with Andable {

  def stop(): Unit

  override def close(): Unit = stop()
}

/**
  * Gives you a possibility to write `container1 and container2`.
  *
  * Used for tests DSL.
  */
sealed trait Andable {

  def stop(): Unit

  def foreach(f: Stoppable => Unit): Unit = {
    this match {
      case and(head, tail) =>
        head.foreach(f)
        tail.foreach(f)

      case stoppable: Stoppable =>
        f(stoppable)
    }
  }

}
final case class and[A1 <: Andable, A2 <: Andable](head: A1, tail: A2) extends Andable {

  /**
    * Stopping all Andable elements in the reverse order
    */
  override def stop(): Unit = {
    tail.stop()
    head.stop()
  }
}

object Andable {
  implicit class AndableOps[A <: Andable](val self: A) extends AnyVal {
    def and[A2 <: Andable](that: A2): A and A2 = com.dimafeng.testcontainers.lifecycle.and(self, that)
  }
}
