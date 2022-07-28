package pokemon.conversion

import scala.util.control.NoStackTrace

final case class ConversionError(error: String) extends NoStackTrace