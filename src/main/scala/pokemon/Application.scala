package pokemon

import cats.effect.{IO, IOApp}
import com.typesafe.scalalogging.LazyLogging
import pokemon.neo4j.Neo4jQuery.Query
import pokemon.neo4j.client.Neo4jSimpleClient
import pokemon.neo4j.config.Neo4jConfig
import pokemon.neo4j.Neo4jQuery.Neo4jInterpretable.given
import pokemon.neo4j.Neo4jQuery.ResultConverter._
import pokemon.TestNeo4j
import cats.implicits._
import conversion.OpaqueConversions.given
import scala.collection.JavaConverters.*

object Application extends IOApp.Simple with LazyLogging {

  override def run: IO[Unit] =
    Neo4jSimpleClient(new Neo4jConfig("bolt://localhost:7687", "neo4j", "pokemon"))
      .transaction()
      .use { transaction =>
        Query.fromString("OPTIONAL MATCH (x) RETURN 'a' as a, 1 as b, true as c, {d: 1.5, f: [true, true, false] } as d")
          .execute(transaction)
          .list[TestNeo4j]
      }
      .flatMap(result => IO.blocking { logger.info(result.toString) })

}
