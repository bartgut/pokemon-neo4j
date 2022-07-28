package pokemon.opaquetype
import scala.compiletime.erasedValue
import pokemon.conversion.FromValue
import cats.implicits._


object OpaqueType {
    class GenericOpaqueType[A] {
        opaque type Type = A
        def from(v: A): Type = v
        extension (x: Type) def value = x
        given Conversion[A, Type] with
            def apply(x: A): Type = x
    }

    class OpaqueIntType extends GenericOpaqueType[Int]
    class OpaqueStringType extends GenericOpaqueType[String]
    class OpaqueBooleanType extends GenericOpaqueType[Boolean]
    class OpaqueDoubleType extends GenericOpaqueType[Double]

    transparent inline def ofType[A] = inline erasedValue[A] match
        case _ : Int => new OpaqueIntType()
        case _ : String => new OpaqueStringType()
        case _ : Boolean => new OpaqueBooleanType()
        case _ : Double => new OpaqueDoubleType()
        case _ => new GenericOpaqueType[A]()
}