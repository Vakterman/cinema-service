package db

trait DbSettings {
    val host: String
    val port: Int
    val dbName: String
    val userName: String
    val password: String
}
