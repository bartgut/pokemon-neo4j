package pokemon.conversion

import cats.implicits._

object OpaqueConversions {

  given [A, B](using c: Conversion[A,B], fv: FromValue[A]): FromValue[B] =
    fv.map(c.apply)

}
