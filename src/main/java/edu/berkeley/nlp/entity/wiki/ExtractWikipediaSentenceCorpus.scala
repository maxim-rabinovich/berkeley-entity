package edu.berkeley.nlp.entity.wiki

import java.io._

/**
 * Created by mrabinovich on 2/16/2015.
 */
object ExtractWikipediaSentenceCorpus extends App {

  val wikiPath : String =
    "/project/eecs/nlp/gdurrett/zdisco-clean-copy/data/wikipedia/enwiki-latest-pages-articles.xml"
  val querySet : Set[String] = Array[String]("Anarchism").toSet

  var outDir : String = ""

  parseArgs(args.toList)

  def parseArgs(args : List[String]) : Unit = {
    args match {
      case Nil => {}
      case "-outDir" :: x :: rest => { outDir = if (x.endsWith("/")) {x} else { x + "/" } ; parseArgs(rest) }
      case _ :: rest => { parseArgs(rest) }
    }

    return
  }

  def extractSentences(text : String) : Array[String] = {
      Array[String](text)
  }

  def main() : Unit = {

    printf("Running ExtractWikipediaSentenceCorpus with [wikiPath = %s, outDir = %s]...\n",
           wikiPath, outDir)

    val titleDB : WikipediaTitleGivenSurfaceDB = WikipediaTitleGivenSurfaceDB.processWikipedia(wikiPath, querySet)
    val  textDB : WikipediaTextDB = WikipediaTextDB.processWikipedia(wikiPath, titleDB.allPossibleTitlesLowercase.toSet)

    val outFile : File = new File(outDir + "enwiki-latest-pages-queries.txt")
    val pw      : PrintWriter = new PrintWriter(outFile)
    val titer   : java.util.Iterator[String] = textDB.titleToText.keySet().iterator()
    while (titer.hasNext()) {
      val title      : String = titer.next()
      val text       : String = textDB.titleToText.get(title)
      val sentences  : Array[String] = extractSentences(text)

      for (s <- sentences) { pw.println(s) }
    }

    pw.close()

  }

  main()
}
