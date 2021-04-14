package web

import TitleADT.Title
import db.DB

trait SearchTitleService {
  val db: DB

  def searchByPrimaryTitle(primaryTitle: String): List[Title] =
                              db.searchByPrimaryTitle(primaryTitle)

  def searchByOriginalTitle(originalTitle: String): List[Title] =
                              db.searchByOriginalTitle(originalTitle)

  def searchTopNByGenre(genre: String, limit: Int): List[Title] =
                          db.searchTopNByGenre(genre, limit)
}
