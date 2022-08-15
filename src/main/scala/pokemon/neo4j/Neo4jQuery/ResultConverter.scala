package pokemon.neo4j.Neo4jQuery

import cats.Monad
import cats.effect.IO
import cats.effect.kernel.Sync
import org.neo4j.driver.{Result, Transaction, Record}
import pokemon.conversion.FromValue
import pokemon.conversion.FromMap

import scala.collection.JavaConverters.*
import cats.implicits._
import cats.ApplicativeError
import pokemon.conversion.ConversionError
import scala.annotation.tailrec

object ResultConverter {

  extension(record: Record)
    def asScalaMap: Map[String, Any] =
      javaToScalaMap(record.asMap())
      
  extension[F[_]: Monad](resultF: F[Result])
    def unique[A](using F: Sync[F], AE: ApplicativeError[F, Throwable], FM: FromMap[A]): F[A] =
      resultF.flatMap { result =>
        if (result.hasNext) AE.fromEither(FM.fromMap(result.single.asScalaMap))
        else AE.raiseError(ConversionError("Expected 1 result, received 0"))
      }

    def option[A](using F: Sync[F], AE: ApplicativeError[F, Throwable], FM: FromMap[A]): F[Option[A]] =
      resultF.flatMap { result =>
        if (result.hasNext) AE.fromEither(FM.fromMap(result.single.asScalaMap)).flatMap(p => F.delay { Option(p) })
        else F.delay { None }
      }

    def list[A](using F: Sync[F], AE: ApplicativeError[F, Throwable], fv: FromMap[A]): F[List[A]] =
      resultF.flatMap { result =>
        fs2.Stream.evals( F.delay { result.list().asScala.toList } )
          .map(record => record.asScalaMap)
          .map(resultMap => fv.fromMap(resultMap))
          .evalMap(resultEither => AE.fromEither(resultEither))
          .compile
          .toList
      }

  private def javaToScalaMap(javaMap: java.util.Map[String, Object]): Map[String, Any] =
    javaMap.asScala.map { case (key, value) => 
      value match 
        case map: java.util.Map[?, ?] => 
          (key, javaToScalaMap(map.asInstanceOf[java.util.Map[String, Object]]))
        case list: java.util.List[?] =>
          (key, list.asScala.toList)
        case _ => (key, value)
    }.toMap
}

