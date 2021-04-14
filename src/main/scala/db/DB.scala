package db

/**
 * This is common interface - we can use Elastic search instead of Postgres
 */
trait DB {
  import TitleADT.Title

  def searchByPrimaryTitle(primaryTitle: String): List[Title]
  def searchByOriginalTitle(originalTitle: String): List[Title]
  def searchTopNByGenre(genre: String, limit: Int): List[Title]
}
