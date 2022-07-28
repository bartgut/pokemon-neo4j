package pokemon
import pokemon.TestNeo4j.BType
import pokemon.TestNeo4j.AType
import pokemon.TestNeo4j.CType
import pokemon.TestNeo4j.DType
import pokemon.conversion.FromValue
import pokemon.opaquetype.OpaqueType
import pokemon.conversion.ConversionError
import cats.implicits._

case class TestNeo4j(a: AType, b: BType, c: CType, d: TestNeo4j2)
case class TestNeo4j2(d: DType, e: Option[BType], f: List[CType])

object TestNeo4j {
    val AType = OpaqueType.ofType[String]
    type AType = AType.Type

    val BType = OpaqueType.ofType[Int]
    type BType = BType.Type

    val CType = OpaqueType.ofType[Boolean]
    type CType = CType.Type

    val DType = OpaqueType.ofType[Double]
    type DType = DType.Type
}