package config

import com.typesafe.config.ConfigFactory
import db.DbSettings

object ConfigReader {
  case class AppConfiguration(webService: WebServiceConfig, pgSettings: PostgresConfig)
  case class WebServiceConfig(host: String, port: Int)
  case class PostgresConfig(host: String,
                            port: Int,
                            dbName: String,
                            userName: String,
                            password: String) extends DbSettings

  def apply(configPath: String): AppConfiguration = {
      val rootConfig = ConfigFactory.load(configPath)
      val webServiceConfig = rootConfig.getConfig("cinema-service.web-service")
      val postgresConfiguration = rootConfig.getConfig("cinema-service.postgres")
      AppConfiguration(
        WebServiceConfig(
          webServiceConfig.getString("host"),
          webServiceConfig.getInt("port")),

        PostgresConfig(postgresConfiguration.getString("host"),
          postgresConfiguration.getInt("port"),
          postgresConfiguration.getString("db-name"),
          postgresConfiguration.getString("login"),
          postgresConfiguration.getString("password"))
      )
  }
}
