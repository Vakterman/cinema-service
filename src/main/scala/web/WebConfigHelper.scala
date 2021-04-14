package web

import akka.actor.ClassicActorSystemProvider
import akka.http.scaladsl.Http
import config.ConfigReader.WebServiceConfig

object WebConfigHelper {
    implicit class EnrichWebConfig(webConfig: WebServiceConfig) {
      def launchWebServer()(implicit system: ClassicActorSystemProvider)
                    = Http().newServerAt(webConfig.host,
                                            webConfig.port)
    }
}
