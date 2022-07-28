package pokemon.conversion

import scala.collection.JavaConverters.*
import scala.deriving.Mirror
import scala.util.{Try, Success, Failure}
import scala.util.control.NoStackTrace
import pokemon.conversion.SafeConversions._
import cats.implicits._
import cats.Functor

trait FromValue[A] {
  def fromValue(value: Option[Any]): Either[ConversionError, A]
}

object FromValue {

  def mandatoryField(value: Option[Any]) = 
    value.toRight(ConversionError("Missing obligatory value"))    

  def castToMapSafe(value: Any) : Option[Map[String, Any]] =
    Try { value.asInstanceOf[Map[String, Any]] }.toOption

  given Functor[FromValue] with
    def map[A, B](fa: FromValue[A])(f: A => B): FromValue[B] =
      (value: Option[Any]) => fa.fromValue(value).map(f)

  import scala.compiletime.*
  import scala.deriving.Mirror

  given FromValue[String] with
    def fromValue(value: Option[Any]): Either[ConversionError, String] =
      mandatoryField(value).map(_.toString())

  given FromValue[Int] with
    def fromValue(value: Option[Any]): Either[ConversionError, Int] =
      mandatoryField(value).flatMap {
        case i: Int => Right(i)
        case l: Long => longToIntSave(l)
        case x => Left(ConversionError(s"Wrong type encountered for expected Int: $x"))
      }

  given FromValue[Boolean] with
    def fromValue(value: Option[Any]): Either[ConversionError, Boolean] =
      mandatoryField(value).flatMap {
        case b: Boolean => Right(b)
        case s: String => stringToBoolean(s)
        case x => Left(ConversionError(s"Wrong type encountered for expected Boolean: $x"))
      }

  given FromValue[Double] with
    def fromValue(value: Option[Any]): Either[ConversionError, Double] =
      mandatoryField(value).flatMap {
        case d: Double => Right(d)
        case x => Left(ConversionError(s"Wrong type encountered for expected Double: $x"))
      }    

  given [A](using FromValue[A]): FromValue[Option[A]] with
    def fromValue(value: Option[Any]): Either[ConversionError, Option[A]] = 
      value.flatMap(s => summon[FromValue[A]].fromValue(Some(s)).toOption).asRight[ConversionError]

  given [A](using FromValue[A]): FromValue[List[A]] with
    def fromValue(value: Option[Any]): Either[ConversionError, List[A]] =
      mandatoryField(value).flatMap {
        case l: List[?] => l.traverse(s => summon[FromValue[A]].fromValue(Some(s)))
        case s => Left(ConversionError(s"Expected List, received ${s.getClass}"))
      }    

  /* Derivation for more complex cases */

  inline given derived[A](using m: Mirror.Of[A]): FromValue[A] = new FromValue[A] {
    override def fromValue(value: Option[Any]): Either[ConversionError, A] =
      inline m match {
        case s: Mirror.SumOf[A] => fromMapSum(s, value)
        case p: Mirror.ProductOf[A] => fromMapProduct(p, value)
      }
  }

  private inline def labelsAndInstances[Labels <: Tuple, Elems <: Tuple]: List[(String, FromValue[?])] =
    inline erasedValue[Labels] match
      case _ : (l *: ls) =>
        inline erasedValue[Elems] match {
          case _: (e *: es) =>
            val propertyLabel = constValue[l].asInstanceOf[String]
            val propertyFromValueInstance = summonInline[FromValue[e]]
            (propertyLabel, propertyFromValueInstance) :: labelsAndInstances[ls, es]
        }
      case _ => Nil

  private inline def fromMapProduct[A](p: Mirror.ProductOf[A], value: Option[Any]): Either[ConversionError, A] = {
    val fieldNames: List[(String, FromValue[?])] = labelsAndInstances[p.MirroredElemLabels, p.MirroredElemTypes]
    value.flatMap(castToMapSafe)
      .toRight(ConversionError("Wrong type encountered for expected java.util.map"))
      .flatMap { map => 
        val result = fieldNames.traverse {  
          case (fieldName, fromValueMapper) => fromValueMapper.fromValue(map.get(fieldName))
        }
        result.map(res => p.fromProduct(Tuple.fromArray(res.toArray))) 
      }
  }

  private inline def fromMapSum[A](s: Mirror.SumOf[A], value: Any): Either[ConversionError, A] = ???
}
