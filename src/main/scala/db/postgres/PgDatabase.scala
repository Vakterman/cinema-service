package db.postgres

import TitleADT.Title
import config.ConfigReader.PostgresConfig
import db.{DB, DbWithTimeLog}
import db.postgres.DbHelper.MakeMonad
import db.postgres.StatementBuilder._

import java.sql.{Connection, DriverManager}

// Postgres database implementations - no any external libraries
// because it executes just simple select
// but more complicated code mandates to use something like scala quill
class PgDatabase(implicit val settings: PostgresConfig) extends DB  {
  private val driver = "org.postgresql.Driver"
  private val postgresUrl = (host: String, port: Int, dbName: String) =>
    s"jdbc:postgresql://$host:$port/$dbName"

  type Reader = PgTitleReader

  private lazy val connection: Connection = {
    Class.forName(driver)
    DriverManager
      .getConnection(
        postgresUrl(
          settings.host,
          settings.port,
          settings.dbName),
          settings.userName,
          settings.password)
  }

  private val pgStatement = connection.createStatement
  private val reader: PgTitleReader = PgTitleReader

  override def searchByPrimaryTitle(primaryTitle: String): List[Title] = {
    pgStatement
      .executeQuery(SearchByPrimaryTitleRequest(primaryTitle))
      .map(rs => reader(rs))
  }

  override def searchByOriginalTitle(originalTitle: String): List[Title] =
    pgStatement
      .executeQuery(SearchByOriginalTitleRequest(originalTitle))
      .map(rs => reader(rs))

  override def searchTopNByGenre(genre: String, limit: Int): List[Title] = {

    pgStatement
      .executeQuery(GetGenreIdByName(genre))
      .map(rs => rs.getShort("genre_id"))
      .flatMap(genreId =>
        pgStatement
          .executeQuery(SearchTopNByGenresId(genreId, limit))
          .map(rs => reader(rs)))
  }
}

object PgDatabase {
  def apply()(implicit pgSettings: PostgresConfig): PgDatabase = new PgDatabase()
}

object PgDatabaseWithLog {
  def apply()(implicit pgSettings: PostgresConfig): PgDatabase = new PgDatabase() with DbWithTimeLog
}
