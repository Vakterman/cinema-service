package object TitleADT {
    case class Title(primaryTitle: String,
                     originalTitle: String,
                     startYear: Short,
                     endYear: Short,
                     isAdult: Boolean,
                     runtimeMinutes: Short,
                     genres: Set[String],
                     rating: Double)
}
