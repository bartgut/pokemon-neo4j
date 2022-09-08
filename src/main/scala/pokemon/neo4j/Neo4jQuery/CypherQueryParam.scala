package pokemon.neo4j.Neo4jQuery

import scala.CanEqual.derived
import scala.deriving.Mirror

trait CypherQueryParam[A] {
  def value: A
  def asParam: String
}

trait ToCypherQueryParam[A] {
  def from(value: A): CypherQueryParam[A]
}

object ToCypherQueryParam {
  given ToCypherQueryParam[String] with
    def from(v: String): CypherQueryParam[String] =
      new CypherQueryParam[String] {
        def value = v
        def asParam = v 
      }

  given ToCypherQueryParam[Int] with
    def from(v: Int): CypherQueryParam[Int] =
      new CypherQueryParam[Int] {
        def value = v
        def asParam = v.toString() 
      }

  given ToCypherQueryParam[Boolean] with
    def from(v: Boolean): CypherQueryParam[Boolean] =
      new CypherQueryParam[Boolean] {
        def value = v
        def asParam = v.toString() 
      }

  given mapInstance: ToCypherQueryParam[Map[String, String]] with
    def from(v: Map[String, String]): CypherQueryParam[Map[String, String]] =
      new CypherQueryParam[Map[String, String]] {
        def value = v
        def asParam = v.toString() 
      }

  import scala.compiletime.*
  import scala.deriving.Mirror

  inline given derived[A](using m: Mirror.Of[A]): ToCypherQueryParam[A] = new ToCypherQueryParam[A] {
    override def from(value: A): CypherQueryParam[A] = 
      new CypherQueryParam[A] {
        def value = value
        def asParam: String = value.toString()
      }
  } /*new ToCypherQueryParam[A] {
    def from(value: A): CypherQueryParam[A] = 
      inline m match
        case s: Mirror.SumOf[A] => sum(s)
        case p: Mirror.ProductOf[A] => product(p, value) 
  }*/

  private inline def product[A](p: Mirror.ProductOf[A], value: A) = 
    val fieldNames = labelsAndInstances[p.MirroredElemLabels, p.MirroredElemTypes]
    val values = value.asInstanceOf[Product].productIterator.toList
    val resultMap = fieldNames.zip(values).map { case ((fieldName, typeclass), value) =>
      fieldName -> typeclass.asInstanceOf[ToCypherQueryParam[Any]].from(value)
    }.map { case (name, param) => (name, param.asParam) }
    .toMap
    new CypherQueryParam[A] {
      def value = value
      def asParam = resultMap.toString()
    }

  private inline def sum[A](s: Mirror.SumOf[A]) = ???

  private inline def labelsAndInstances[Labels <: Tuple, Elems <: Tuple]: List[(String, ToCypherQueryParam[?])] =
    inline erasedValue[Labels] match
      case _ : (l *: ls) =>
        inline erasedValue[Elems] match {
          case _: (e *: es) =>
            val propertyLabel = constValue[l].asInstanceOf[String]
            val propertyFromValueInstance = summonInline[ToCypherQueryParam[e]]
            (propertyLabel, propertyFromValueInstance) :: labelsAndInstances[ls, es]
        }
      case _ => Nil

}

object CypherQueryUtil {
  given [A](using TC: ToCypherQueryParam[A]): Conversion[A, CypherQueryParam[A]] with
    def apply(value: A) = TC.from(value)
}
