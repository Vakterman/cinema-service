package web

import TitleADT.Title
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, StandardRoute}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

import scala.language.implicitConversions

// I put all routes in this trait. But we have more complicated structure
// we should split it
trait TitleRoutes {
  this: SearchTitleService =>
  implicit val itemFormat: RootJsonFormat[Title] = jsonFormat8(Title)

  lazy val topLevelRoute: Route =
    pathPrefix("api") {
      pathPrefix("v1") {
        concat(
          path("primaryTitle") {
            get {
              parameters("title".as[String]) {
                title => completeTitleList(searchByOriginalTitle(title))
              }
            }
          },
          path("originalTitle") {
            get {
              parameters("title".as[String]) {
                title => completeTitleList(searchByOriginalTitle(title))
              }
            }
          },
          path("byGenre") {
            get {
              parameters("genre".as[String], "limit".as[Int]) {
                (genre, limit) => {
                  complete(searchTopNByGenre(genre, limit))
                }
              }
            }
          }
        )
      }
    }

  def completeTitleList(titleList: List[Title]): StandardRoute =
    if(titleList.isEmpty)
      complete(StatusCodes.NotFound)
    else
      complete(titleList)
}

object TitleRoutes {
  implicit val toRoot: TitleRoutes => Route = (route: TitleRoutes) => route.topLevelRoute
}
