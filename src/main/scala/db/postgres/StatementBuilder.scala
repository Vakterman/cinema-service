package db.postgres

object StatementBuilder {

  object SearchByPrimaryTitleRequest {

    def apply(partOfPrimaryTitle: String): String  =
      s"""SELECT
         |primary_title,
         |original_title,
         |start_year,
         |end_year,
         |is_adult,
         |runtime_minutes,
         |genres,
         |rating
         |FROM title_info
         |WHERE primary_title @@ to_tsquery('*$partOfPrimaryTitle:*')
         |""".stripMargin.replaceAll("\n", " ")
  }

  object SearchByOriginalTitleRequest {
    def apply(partOfOriginalTitle: String): String =
      s"""SELECT
        |primary_title,
        |original_title,
        |start_year,
        |end_year,
        |is_adult,
        |runtime_minutes,
        |genres,
        |rating
        |FROM title_info.titles
        |WHERE original_title @@ to_tsquery('*$partOfOriginalTitle:*')"""
        .stripMargin
        .replace("\\n", " ")
  }

  object SearchTopNByGenresId {
    def apply(genreId: Int, topN: Int): String =
      s"""SELECT
         |primary_title,
         |original_title,
         |start_year,
         |end_year,
         |is_adult,
         |runtime_minutes,
         |genres,
         |rating
         |FROM title_info.titles
         |AS ts INNER JOIN title_info.title_genres
         |AS tg ON ts.title_id = tg.title_id
         |WHERE genre_id  = $genreId ORDER BY ts.rating DESC LIMIT $topN"""
        .stripMargin
        .replace("\\n", " ")
  }

  object GetGenreIdByName {
    def apply(genreName: String) =
      s"SELECT genre_id FROM title_info.genres WHERE genre_name='$genreName'"
  }
}
