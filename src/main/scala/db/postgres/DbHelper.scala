package db.postgres

import java.sql.ResultSet
import scala.annotation.tailrec

object DbHelper {
  // With power of implicit classes we turn ResultSet mapping into functional style
  implicit class MakeMonad(rs: ResultSet) {
    def map[T](f: ResultSet => T): List[T] = {
      @tailrec
      def collect(rs: ResultSet, acc: List[T]): List[T] = {
        if (!rs.next()) acc
        else collect(rs, f(rs) :: acc)
      }

      collect(rs, List())
    }
  }
}
