package db
import TitleADT.Title

trait TitleReader {
  type A
  def apply(source: A): Title
}
