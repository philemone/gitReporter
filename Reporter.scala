#!/usr/bin/env -S scala-cli shebang

import scala.io.Source
import sys.process.*

object Reporter {
  private final val RepositoryFile = "repositories.txt"

  def main(args: Array[String]): Unit = {
    if (args.contains("-h") || args.contains("--help")) println(help)
    else {
      val mapWithArgs = args.grouped(2).toList.map { v => v.head -> v.tail.headOption.getOrElse("") }.toMap
      failOnMissingArguments(mapWithArgs)

      val fromDate     = argSince.extractFromMap(mapWithArgs)
      val toDate       = argBefore.extractFromMap(mapWithArgs)
      val user         = argUser.extractFromMap(mapWithArgs)
      val prettyFormat = mapWithArgs.getOrElse("-p", "%H - %s")
      val extraArgs    = if (mapWithArgs.contains("-nm")) "--no-merges" else ""
      val repositories = fetchOrCreateRepositoryFile()

      val request: String = s"git log --author=$user --pretty=\"$prettyFormat\" --since=\"$fromDate\" --before=\"$toDate\" $extraArgs"
      fetchCommits(repositories, request)
    }
  }

  private def fetchOrCreateRepositoryFile() = {
    val source = Source.fromFile(s"$RepositoryFile")
    val paths  = source.getLines().toList.filterNot(l => l.startsWith("#") || l.isBlank)
    source.close()
    if (paths.isEmpty) throw IllegalArgumentException(s"Please define repositories paths in $RepositoryFile file")
    paths
  }

  private def fetchCommits(repositories: List[String], request: String): Unit = {
    repositories.foreach { repo =>
      println(s"---- [$repo] ----")
      println(s"sh -c 'cd $repo; $request'".!!)
    }
  }

  private def failOnMissingArguments(args: Map[String, String]): Unit =
    RequiredArguments.filterNot(arg => args.exists { case (name, _) => arg.equalsName(name) }).map(_.printHint) match {
      case missing if missing.nonEmpty =>
        throw IllegalArgumentException(s"Required arguments are missing (--command example): [${missing.mkString(", ")}]")
      case _ =>
    }

  private val argUser     = Arguments("u", "user", "user@domain.com", "User email address")
  private val argSince    = Arguments("s", "since", "2024-01-14", "Date to start gathering commits from (--since in git). This date is inclusive")
  private val argBefore   = Arguments("b", "before", "2024-01-30", "Date to end gathering commits to (--before in git). This date is inclusive")
  private val argHelp     = Arguments("h", "help", "", "Help!")
  private val argNoMerges = Arguments("nm", "no-merges", "'--no-merges'", "Don't count merges")
  private val argPretty   = Arguments("p", "pretty", "'%H - %cd - %s'", "How should be commits displayed (check out git docs for symbols meaning)")

  private val RequiredArguments = List(argUser, argSince, argBefore)
  private val OptionalArguments = List(argHelp, argNoMerges, argPretty)

  private def help: String = {
    def title(value: String) = s"\n\n ---- $value ---- \n\n"
    " Hello good fellow!" +
      title("Required arguments") +
      RequiredArguments.mkString("\n") +
      title("Optional arguments") +
      OptionalArguments.mkString("\n")
  }

  private case class Arguments(shortName: String, longName: String, example: String, description: String) {
    def equalsName(name: String): Boolean = name.equals("-" + shortName) || name.equals("--" + longName)
    def printHint: String                 = s"--$longName $example"
    def extractFromMap(m: Map[String, String]): String = m
      .find { case (key, _) => equalsName(key) }
      .map(_._2)
      .getOrElse(
        throw IllegalArgumentException(s"Required argument $longName is missing (--command example): $toString")
      )

    def extractFromMap(m: Map[String, String], default: String): String = {
      m.getOrElse(s"-$shortName", m(s"--$longName"))
    }

    override def toString: String = s" -$shortName, \t --$longName \t $description (e.g. --$longName $example)"
  }

}
