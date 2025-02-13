//> using scala 3.6.2
//> using dep "com.lihaoyi::os-lib:0.11.3"
//> using dep "com.monovore::decline:2.4.1"
//> using dep "org.kohsuke:github-api:1.327"

import scala.jdk.CollectionConverters._
import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import org.kohsuke.github.GitHubBuilder
import com.monovore.decline.*
import cats.syntax.all.given

import java.util.Date
import java.text.SimpleDateFormat

object ReleaseNotes {
  val Organization = "testcontainers"
  val Repo = "testcontainers-scala"

  val firstTagOpt =
    Opts.option[String](long = "first-tag", help = "start git tag", short = "s")

  val lastTagOpt =
    Opts.option[String](long = "last-tag", help = "end git tag", short = "e")

  val githubTokenOpt =
    Opts.option[String](long = "token", help = "github token", short = "t")
  val debugOpt = Opts
    .flag(long = "debug", help = "prints debug information", short = "d")
    .orFalse

  final case class Arguments(
      firstTag: String,
      lastTag: String,
      githubToken: String,
      debug: Boolean
  )

  val opts: Opts[Arguments] =
    Opts.subcommand("release-notes", "generates release notes") {
      (firstTagOpt, lastTagOpt, githubTokenOpt, debugOpt).mapN(Arguments.apply)
    }

  def run(args: Arguments): Unit = {
    def debug(msg: String) = if (args.debug)
      println(msg)

    val commits = os
      .proc(List("git", "rev-list", s"${args.firstTag}..${args.lastTag}"))
      .call()
      .out
      .trim()
      .linesIterator
      .size

    debug(
      s"Number of commits between ${args.firstTag}..${args.lastTag}: $commits"
    )

    val contributors = os
      .proc(
        List(
          "git",
          "shortlog",
          "-sn",
          "--no-merges",
          s"${args.firstTag}..${args.lastTag}"
        )
      )
      .call()
      .out
      .trim()
      .linesIterator
      .toList

    val command = List(
      "git",
      "log",
      s"${args.firstTag}..${args.lastTag}",
      "--first-parent",
      "master",
      "--pretty=format:%H"
    )

    val output = os.proc(command).call().out.trim()

    val gh = new GitHubBuilder()
      .withOAuthToken(args.githubToken)
      .withEndpoint("https://api.github.com")
      .build()

    val foundPRs = mutable.Set.empty[Int]
    val mergedPRs = ListBuffer[String]()

    for {
      // group in order to optimize API
      searchSha <-
        output.split('\n').grouped(5).map(_.mkString("SHA ", " SHA ", ""))
      allMatching =
        gh.searchIssues()
          .q(s"repo:$Organization/$Repo type:pr $searchSha")
          .list()
      pr <- allMatching.toList().asScala.sortBy(_.getClosedAt()).reverse
      prNumber = pr.getNumber()
      if !foundPRs(prNumber)
    } {
      foundPRs += prNumber
      val login = pr.getUser().getLogin()
      val formattedPR =
        s"- ${pr.getTitle()} [\\#${pr.getNumber()}](${pr.getHtmlUrl()}) ([$login](https://github.com/$login))"
      mergedPRs += formattedPR
    }

    val releaseNotes =
      template(
        firstTag = args.firstTag,
        lastTag = args.lastTag,
        mergedPrs = mergedPRs.toList,
        commits = commits,
        contributors = contributors
      )

    debug(releaseNotes)

    gh.getRepository(s"$Organization/$Repo")
      .createRelease(args.lastTag)
      .name(s"${args.lastTag}")
      .body(releaseNotes)
      .create()
  }

  def today: String = {
    val formatter = new SimpleDateFormat("yyyy-MM-dd");
    formatter.format(new Date());
  }

  def template(
      firstTag: String,
      lastTag: String,
      mergedPrs: List[String],
      commits: Int,
      contributors: List[String]
  ): String =
    s"""
      |## testcontainers-scala $lastTag
      |
      |We're happy to announce the release of test-containers $lastTag, which
      |
      |<table>
      |<tbody>
      |  <tr>
      |    <td>Commits since last release</td>
      |    <td align="center">$commits</td>
      |  </tr>
      |  <tr>
      |    <td>Merged PRs</td>
      |    <td align="center">${mergedPrs.size}</td>
      |  </tr>
      |    <tr>
      |    <td>Contributors</td>
      |    <td align="center">${contributors.size}</td>
      |  </tr>
      |</tbody>
      |</table>
      |
      |## Contributors
      |
      |Big thanks to everybody who contributed to this release or reported an issue!
      |
      |```
      |$$ git shortlog -sn --no-merges $firstTag..$lastTag
      |${contributors.mkString("\n")}
      |```
      |
      |## Merged PRs
      |
      |## [$lastTag](https://github.com/$Organization/$Repo/tree/$lastTag) (${today})
      |
      |[Full Changelog](https://github.com/$Organization/$Repo/compare/$firstTag...$lastTag)
      |
      |**Merged pull requests:**
      |
      |${mergedPrs.mkString("\n")}
      |""".stripMargin
}

object GenerateRelease
    extends CommandApp(
      name = "generate-release",
      header = "helpers to automate releases",
      version = "0.0.0",
      main = (ReleaseNotes.opts).map { case args: ReleaseNotes.Arguments =>
        ReleaseNotes.run(args)
      }
    )
