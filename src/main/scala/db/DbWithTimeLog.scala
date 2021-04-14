package db

import TitleADT.Title
import akka.event.slf4j.Logger

// stackable pattern - scala version of decorator pattern
trait DbWithTimeLog extends DB {
  val logger = Logger("Database")
  def logTime[R](actionName: String )(action: R) = {
    val startTime = System.currentTimeMillis()
    val result = action
    logger.info(s"$actionName completed in ${System.currentTimeMillis()} milliseconds")
    result
  }

  abstract override def searchByPrimaryTitle(primaryTitle: String): List[Title] = {
    logTime("byPrimaryTitleRequest") {
      super.searchByPrimaryTitle(primaryTitle)
    }
  }
  abstract override def searchByOriginalTitle(originalTitle: String): List[Title] = {
    logTime("byOriginalTitleRequest") {
      super.searchByOriginalTitle(originalTitle)
    }
  }
  abstract override def searchTopNByGenre(genre: String, limit: Int): List[Title] = {
    logTime("topNByGenreRequest") {
      super.searchTopNByGenre(genre, limit)
    }
  }
}
