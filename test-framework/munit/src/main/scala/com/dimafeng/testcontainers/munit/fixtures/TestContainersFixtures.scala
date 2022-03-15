package com.dimafeng.testcontainers.munit.fixtures

import com.dimafeng.testcontainers.lifecycle.Stoppable
import org.testcontainers.lifecycle.Startable
import munit.Suite
import munit.FunFixtures
import munit.FunSuite
import com.dimafeng.testcontainers.munit.TestContainersSuite
import com.dimafeng.testcontainers.lifecycle.TestLifecycleAware

trait TestContainersFixtures { self: FunSuite =>

    /**
     * Creates a fixture that starts a single container before each test and stops
     * it after each test.
     *
     * @example
     * {{{
     * class MysqlSpec extends FunSuite with TestContainersFixtures {
     *
     *   val mysql = ForEachContainerFixture(MySQLContainer())
     * 
     *   // You need to override `munitFixtures` and pass in your container fixture
     *   override def munitFixtures = List(mysql)
     *
     *   test("test case name") {
     *     // Inside your test body you can do with your container whatever you want to
     *     assert(mysql().jdbcUrl.nonEmpty)
     *   }
     * }
     * }}}
     */
    class ForEachContainerFixture[T <: Startable with Stoppable](val container: T)
        extends Fixture[T]("ForEachTestContainers") {

        def apply(): T = container

        def afterContainerStart(container: T, context: BeforeEach): Unit = ()

        def beforeContainerStop(container: T, context: AfterEach): Unit = ()

        override def beforeEach(context: BeforeEach): Unit = {
            container.start()
            afterContainerStart(container, context)
            container match {
                case container: TestLifecycleAware => container.beforeTest(suiteDescription)
                case _ => // do nothing
            }
        }

        override def afterEach(context: AfterEach): Unit = {
            container match {
                case container: TestLifecycleAware => container.afterTest(suiteDescription, None)
                case _ => // do nothing
            }
            beforeContainerStop(container, context)
            container.stop()
        }

    }

    object ForEachContainerFixture {

        /**
         * Creates a fixture that starts a single container before each test and stops
         * it after each test.
         *
         * @example
         * {{{
         * class MysqlSpec extends FunSuite with TestContainersFixtures {
         *
         *   val mysql = ForEachContainerFixture(MySQLContainer())
         * 
         *   // You need to override `munitFixtures` and pass in your container fixture
         *   override def munitFixtures = List(mysql)
         *
         *   test("test case name") {
         *     // Inside your test body you can do with your container whatever you want to
         *     assert(mysql().jdbcUrl.nonEmpty)
         *   }
         * }
         * }}}
         */
        def apply[T <: Startable with Stoppable](container: T) = new ForEachContainerFixture[T](container)

    }

    /**
     * Creates a fixture that starts a single container before all test and stops
     * it after all test.
     *
     * @example
     * {{{
     * class MysqlSpec extends FunSuite with TestContainersFixtures {
     *
     *   val mysql = ForAllContainerFixture(MySQLContainer())
     * 
     *   // You need to override `munitFixtures` and pass in your container fixture
     *   override def munitFixtures = List(mysql)
     *
     *   test("test case name") {
     *     // Inside your test body you can do with your container whatever you want to
     *     assert(mysql().jdbcUrl.nonEmpty)
     *   }
     * }
     * }}}
     */
    class ForAllContainerFixture[T <: Startable with Stoppable](val container: T)
        extends Fixture[T]("ForAllTestContainers") {

        def apply(): T = container

        def afterContainerStart(container: T): Unit = ()

        def beforeContainerStop(container: T): Unit = ()

        override def beforeAll(): Unit = {
            container.start()
            afterContainerStart(container)
        }

        override def beforeEach(context: BeforeEach): Unit = container match {
            case container: TestLifecycleAware => container.beforeTest(suiteDescription)
            case _ => // do nothing
        }

        override def afterAll(): Unit = {
            beforeContainerStop(container)
            container.stop()
        }

        override def afterEach(context: AfterEach): Unit = container match {
            case container: TestLifecycleAware => container.afterTest(suiteDescription, None)
            case _ => // do nothing
        }

    }

    object ForAllContainerFixture {

        /**
         * Creates a fixture that starts a single container before all test and stops
         * it after all test.
         *
         * @example
         * {{{
         * class MysqlSpec extends FunSuite with TestContainersFixtures {
         *
         *   val mysql = ForAllContainerFixture(MySQLContainer())
         * 
         *   // You need to override `munitFixtures` and pass in your container fixture
         *   override def munitFixtures = List(mysql)
         *
         *   test("test case name") {
         *     // Inside your test body you can do with your container whatever you want to
         *     assert(mysql().jdbcUrl.nonEmpty)
         *   }
         * }
         * }}}
         */
        def apply[T <: Startable with Stoppable](container: T) = new ForAllContainerFixture[T](container)

    }

    object ContainerFunFixture {

        /**
         * Creates a fun-fixture that starts a single container before each test and stops
         * it after each test.
         *
         * @example
         * {{{
         * class MysqlSpec extends FunSuite with TestContainersFixtures {
         *
         *   val mysql = ContainerFunFixture(MySQLContainer())
         *
         *   mysql.test("test case name") { container =>
         *     // Inside your test body you can do with your container whatever you want to
         *     assert(container.jdbcUrl.nonEmpty)
         *   }
         * }
         * }}}
         */
        def apply[T <: Startable with Stoppable](container: T) = FunFixture[T](
            setup = { _ => 
                container.start();
                container match {
                    case tla: TestLifecycleAware => tla.beforeTest(suiteDescription)
                    case _ => // do nothing
                }
                container
            }, teardown = { c =>
                c match {
                    case tla: TestLifecycleAware => tla.afterTest(suiteDescription, None)
                    case _ => // do nothing
                }
                c.stop()
            }
        )

    }

    private val suiteDescription = TestContainersSuite.createDescription(self)
  
}
