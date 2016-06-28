[![Build Status](https://travis-ci.org/dimafeng/testcontainers-scala.svg?branch=master)](https://travis-ci.org/dimafeng/testcontainers-scala)

Testcontainers-scala
====================
Scala wrapper for [testcontainers-java](https://github.com/testcontainers/testcontainers-java) that
allows to start and work with docker containers during testing.

Setup
-----

*Maven*

```
<dependency>
    <groupId>com.dimafeng</groupId>
    <artifactId>testcontainers-scala</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
</dependency>

```

*Gradle*

```
testCompile("com.dimafeng:testcontainers-scala:0.1.0")
```

*SBT*

```
libraryDependencies += "com.dimafeng" % "testcontainers-scala" % "0.1.0" % "test"
```

Requirements
------------

* JDK > 1.8

Quick Start
-----------

There are two modes of container lunching: `ForEachTestContainer` and `ForAllTestContainer`.
The first one starts a new container before each test case and then stops and removes it. The second one
 starts and stops a container only once.

 To start using it, you just need to extend one of those traits and override a `container` val.

 ```
 import org.testcontainers.containers.MySQLContainer

 class MysqlSpec extends FlatSpec with ForEachTestContainer {

   val mysqlContainer = new MySQLContainer()
   override val container: Container = Container(mysqlContainer)

   "Mysql container" should "be started" in {
     Class.forName(mysqlContainer.getDriverClassName)
     val connection = DriverManager.getConnection(mysqlContainer.getJdbcUrl, mysqlContainer.getUsername, mysqlContainer.getPassword)

     ...

     connection.close()
   }
 }
 ```
 This spec has a clean mysql database instance for each of its test cases.

  ```
  import org.testcontainers.containers.MySQLContainer

  class MysqlSpec extends FlatSpec with ForAllTestContainer {

    val mysqlContainer = new MySQLContainer()
    override val container: Container = Container(mysqlContainer)

    it should "do something" in {
      ...
    }

    it should "do something 2" in {
      ...
    }
  }
  ```

  This spec starts one container and both tests share the container's state.


Container types
---------------

You can use multiple containers:

```
class MysqlSpec extends FlatSpec with ForAllTestContainer {
    override val container: Container = Container(new GenericContainer("redis:3.0.2"), new MySQLContainer())

    ....
}
```
All containers passed to `Container` will be launched before test start.

There are several predefined containers in the *testcontainers-java* library:

* [Temporary database containers](http://testcontainers.viewdocs.io/testcontainers-java/usage/database_containers/)
* [Generic containers](http://testcontainers.viewdocs.io/testcontainers-java/usage/generic_containers/)
* [Docker compose](http://testcontainers.viewdocs.io/testcontainers-java/usage/docker_compose/)
* [Dockerfile containers](http://testcontainers.viewdocs.io/testcontainers-java/usage/dockerfile/)

Selenium
--------

First of all, you need to add [this dependency](http://mvnrepository.com/artifact/org.testcontainers/selenium/1.0.5) to your build script.


```
class SeleniumSpec extends FlatSpec with SeleniumTestContainer with WebBrowser {
  withDesiredCapabilities(DesiredCapabilities.chrome())

  "Browser" should "show google" in {
      go to "http://google.com"
  }
}

```

In this case, you'll obtain a clean instance of browser (firefox/chrome) within container to which
a test will connect via remote-driver. See [Webdriver Containers](http://testcontainers.viewdocs.io/testcontainers-java/usage/webdriver_containers/)
for more details.


License
-------
The MIT License (MIT)

Copyright (c) 2016 Dmitry Fedosov

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.