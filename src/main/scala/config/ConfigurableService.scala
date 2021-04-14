package config

import config.ConfigReader.{AppConfiguration, PostgresConfig, WebServiceConfig}

import scala.util.Try

trait ConfigurableService {
  def configurationInit(pathToConfig: String): Try[AppConfiguration]
  = Try(ConfigReader(pathToConfig))

  def defaultInit =
    AppConfiguration(
      WebServiceConfig("127.0.0.1", 8443),
      PostgresConfig("127.0.0.1", 5432, "test", "postgres", "postgres"))
}
