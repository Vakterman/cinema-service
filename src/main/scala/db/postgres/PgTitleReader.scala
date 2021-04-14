package db.postgres

import TitleADT.Title
import db.TitleReader

import java.sql.ResultSet

trait PgTitleReader extends TitleReader {
  override type A = ResultSet
  override def apply(source: ResultSet): Title =
    Title(source.getString("primary_title"),
      source.getString("original_title"),
      source.getShort("start_year"),
      source.getShort("end_year"),
      source.getBoolean("is_adult"),
      source.getShort("runtime_minutes"),
      source.getString("genres").split(",").toSet,
      source.getFloat("rating"))
}

object PgTitleReader extends PgTitleReader