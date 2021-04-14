import TitleADT.Title
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.{DB, TitleReader}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import web.{SearchTitleService, TitleRoutes}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object TestEnvInitializer {

 class TestDB(titleList: List[Title]) extends DB {

    override def searchByPrimaryTitle(primaryTitle: String): List[TitleADT.Title] = titleList

    override def searchByOriginalTitle(originalTitle: String): List[TitleADT.Title] = titleList

    override def searchTopNByGenre(genre: String, limit: Int): List[TitleADT.Title] = titleList
  }


  implicit val itemFormat: RootJsonFormat[Title] = jsonFormat8(Title)

  def dbInit(titleList: List[Title]) = new TestDB(titleList)
}
