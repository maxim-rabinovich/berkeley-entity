package edu.berkeley.nlp.entity.wiki

import java.util.HashMap

import edu.berkeley.nlp.futile.fig.basic.{Indexer, IOUtils}

import scala.collection.mutable.HashSet

/**
 * Created by mrabinovich on 2/16/2015.
 */
class WikipediaTextDB(titleToTextMap : HashMap[String, String]) {
    val titleToText : HashMap[String, String] = titleToTextMap
}

object WikipediaTextDB {

  def processWikipedia(wikipediaPath: String, pageTitleSetLc: Set[String]) : WikipediaTextDB = {
    val pageNamesIndex = new Indexer[String];
    val lines = IOUtils.lineIterator(IOUtils.openInHard(wikipediaPath));
    var currentPageTitle = "";
    var doneWithThisPage = false;
    var numPagesSeen = 0;
    var lineIdx = 0;

    val titleToTextMap : HashMap[String, String] = new HashMap[String, String]()
    while (lines.hasNext) {
      val line: String = lines.next;
      if (lineIdx % 100000 == 0) {
        println("Line: " + lineIdx + ", processed " + numPagesSeen + " pages");
      }
      lineIdx += 1;
      // 8 because all page lines look like "  <page>" so we just need to catch the next one and skip
      // longer lines
      if (line.size > 8 && doneWithThisPage) {
        // Do nothing
      } else {
        if (line.contains("<page>")) {
          doneWithThisPage = false;
          numPagesSeen += 1;
        } else if (line.contains("<title>")) {
          // 7 = "<title>".length()
          currentPageTitle = line.substring(line.indexOf("<title>") + 7, line.indexOf("</title>"));
          if (!pageTitleSetLc.contains(currentPageTitle.toLowerCase)) {
            doneWithThisPage = true;
          }
        } else if (line.contains("<redirect title")) {
          doneWithThisPage = true;
        } else if (line.contains("<text")) {
          // process article text

          val rawText: StringBuilder = new StringBuilder()
          do {

            // identify and replace all links in the text
            var startIdx = line.indexOf("[[");
            var startIdxOld = startIdx
            while (startIdx >= 0) {
              val endIdx = line.indexOf("]]", startIdx);
              val pipeIdx = line.indexOf("|", startIdx);
              val replaceString: String = if (pipeIdx >= 0 && pipeIdx < endIdx) {
                line.substring(startIdx + 2, pipeIdx);
              } else if (endIdx >= startIdx + 2) {
                line.substring(startIdx + 2, endIdx);
              } else {
                ""
              }

              startIdxOld = startIdx
              startIdx = line.indexOf("[[", startIdx + 2);

              // replace special link character with
              line.substring(startIdxOld).replaceFirst(line.substring(startIdxOld, endIdx + 2), replaceString)
            }

            rawText.append(" " + line)
          } while (!(line.contains("/text") || line.contains("/page")))

          titleToTextMap.put(currentPageTitle, refineRawText(rawText.toString()))

          doneWithThisPage = true
        }
      }
    }

    new WikipediaTextDB(titleToTextMap)
  }

  def refineRawText(rawText: String): String = {
    // val rawTokens : Array[String] = rawText.split("\\s|")

    rawText
  }
}
