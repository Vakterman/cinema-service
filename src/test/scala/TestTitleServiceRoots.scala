import TitleADT.Title
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.DB
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import spray.json.DefaultJsonProtocol._
import web.{SearchTitleService, TitleRoutes}

class TestRoots extends AnyWordSpec with Matchers with ScalatestRouteTest {
  import TestEnvInitializer._

  val testTitle = Title("testTitle", "testTitle", 1984, 1985, isAdult = true, 90, Set("Music"), 9)

  val testServiceWithRoutes = new SearchTitleService with TitleRoutes {
    override val db: DB = TestEnvInitializer.dbInit(List(testTitle))
  }

  // Just to check endpoints work. For real implementation tests should be more specific
  "The case" should {
      "return test result in  searchByPrimaryTitle endpoint" in {
        Get("/api/v1/primaryTitle?title=something") ~> testServiceWithRoutes.topLevelRoute ~> check {
          responseAs[List[Title]] shouldEqual List(testTitle)
        }
      }

    "return test result in  searchByOriginalTitle endpoint" in {
      Get("/api/v1/originalTitle?title=something") ~> testServiceWithRoutes.topLevelRoute ~> check {
        responseAs[List[Title]] shouldEqual List(testTitle)
      }
    }

    "return test result in  byGenre endpoint" in {
      Get("/api/v1/byGenre?genre=Music&limit=10") ~> testServiceWithRoutes.topLevelRoute ~> check {
        responseAs[List[Title]] shouldEqual List(testTitle)
      }
    }
  }
}