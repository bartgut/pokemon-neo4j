package pokemon.conversion

import pokemon.conversion.FromValue

trait FromMap[A] {
   def fromMap(value: Map[String, Any]): Either[ConversionError, A]
}

object FromMap {
   given [A](using FromValue[A]): FromMap[A] with
      def fromMap(value: Map[String, Any]): Either[ConversionError, A] = 
         summon[FromValue[A]].fromValue(Some(value))
}
