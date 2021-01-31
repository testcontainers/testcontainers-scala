package com.dimafeng.testcontainers.lifecycle

import org.scalatest.freespec.AnyFreeSpec

import scala.collection.mutable

class AndableTest extends AnyFreeSpec {

  case class Cont1(i: Int, buffer: mutable.Buffer[Int] = mutable.Buffer.empty[Int]) extends Stoppable {
    override def stop(): Unit = buffer.append(i)
  }

  case class Cont2(i: Int, buffer: mutable.Buffer[Int] = mutable.Buffer.empty[Int]) extends Stoppable {
    override def stop(): Unit = buffer.append(i)
  }

  "Andable" - {
    "foreach" - {
      "should iterate Andable in a correct order" in {
        val andable = Cont1(1) and Cont1(2) and Cont2(3) and Cont2(4)

        val ixs = mutable.Buffer.empty[Int]
        andable.foreach {
          case Cont1(i, _) => ixs.append(i)
          case Cont2(i, _) => ixs.append(i)
        }

        assert(ixs.toSeq === Seq(1,2,3,4))
      }
    }

    "stop" - {
      "should stop all Andable in a reverse order" in {
        val ixs = mutable.Buffer.empty[Int]

        val andable = Cont1(1, ixs) and Cont1(2, ixs) and Cont2(3, ixs) and Cont2(4, ixs)
        andable.stop()

        assert(ixs.toSeq === Seq(4,3,2,1))
      }
    }
  }

}
