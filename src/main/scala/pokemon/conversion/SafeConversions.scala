package pokemon.conversion

import scala.util.{Try, Success, Failure}

object SafeConversions {
    def longToIntSave(long: Long): Either[ConversionError, Int] =
        Try { long.toInt } match {
          case Success(value) => Right(value)
          case Failure(_) => Left(ConversionError(s"Cannot create an int out of $long"))
        }

    def stringToBoolean(string: String): Either[ConversionError, Boolean] =
        Try { string.toBoolean } match {
          case Success(value) => Right(value)
          case Failure(_) => Left(ConversionError(s"Cannot create a boolean out of $string"))
        }
}
