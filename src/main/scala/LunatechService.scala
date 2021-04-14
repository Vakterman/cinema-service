import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.event.slf4j.Logger
import config.ConfigurableService
import db.DB
import db.postgres.PgDatabase
import web.{SearchTitleService, TitleRoutes}

import scala.io.StdIn
import scala.util.{Failure, Success}

object CinemaService extends App with ConfigurableService {
    val logging = Logger("LunatechService")
    val configuration = configurationInit("conf/cinema-service.conf") match {
        case Success(config) => config
        case Failure(ex) => {
            logging.error(ex.getMessage, ex)
            defaultInit
        }
    }

    implicit val pgSettings = configuration.pgSettings
    val pgDB = PgDatabase()

    implicit val system = ActorSystem(Behaviors.empty, "Cinema")
    implicit val executionContext = system.executionContext

    val searchTitleService : TitleRoutes = new SearchTitleService with TitleRoutes {
        override val db: DB = pgDB
    }

    import web.WebConfigHelper._
    val httpFuture = configuration
                        .webService
                        .launchWebServer()
                        .bind(searchTitleService.topLevelRoute)

    StdIn.readLine("Press to finish...")

    httpFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate())
}
