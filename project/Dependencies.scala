import sbt._

object Dependencies {
  def COMPILE(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(None, modules: _*)

  def PROVIDED(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(Some("provided"), modules: _*)

  def TEST(modules: sbt.ModuleID*): Seq[sbt.ModuleID] = deps(Some("test"), modules: _*)

  def deps(scope: Option[String], modules: sbt.ModuleID*): Seq[sbt.ModuleID] = {
    scope.map(s => modules.map(_ % s)).getOrElse(modules)
  }
}