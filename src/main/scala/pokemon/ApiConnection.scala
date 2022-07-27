package pokemon

import cats.Foldable
import cats.effect.{IO, IOApp}
import com.typesafe.scalalogging.LazyLogging
import io.circe
import sttp.client3.*
import sttp.client3.circe.*
import io.circe.generic.auto.*
import scala.collection.parallel.CollectionConverters.*
import dataModel.TypeData
import scala.collection.parallel.ParSeq
import scala.language.postfixOps

object ApiConnection extends LazyLogging {

  private val typesIds: ParSeq[Int] = (1 to 18).par
  // todo: check if IO.blocking can help here

  private def sendTypeRequest(id: Int): Option[TypeData] =
    val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()
    basicRequest
      .get(uri"https://pokeapi.co/api/v2/type/$id")
      .response(asJson[TypeData])
      .send(backend)
      .body match
      case Left(error) =>
        logger.info(s"$error")
        None
      case Right(data) =>
        Some(data)

  def getTypeData: List[TypeData] =
    typesIds.flatMap(sendTypeRequest).toList
}
