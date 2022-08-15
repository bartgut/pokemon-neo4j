package pokemon

import cats.effect.{IO, IOApp}
import com.typesafe.scalalogging.LazyLogging
import pokemon.neo4j.client.Neo4jSimpleClient
import pokemon.neo4j.config.Neo4jConfig
import pokemon.neo4j.Neo4jQuery.Neo4jInterpretable.given
import pokemon.neo4j.Neo4jQuery.ResultConverter._
import pokemon.TestNeo4j
import cats.implicits._
import conversion.OpaqueConversions.given
import pokemon.neo4j.Neo4jQuery.CypherStringQuery.cypher
import scala.collection.JavaConverters.*
import scala.language.implicitConversions
import pokemon.neo4j.Neo4jQuery.CypherQueryUtil.given
import pokemon.neo4j.Neo4jQuery.ToCypherQueryParam.given
import pokemon.neo4j.Neo4jQuery.CypherQuery

object Application extends IOApp.Simple with LazyLogging {

  override def run: IO[Unit] =
    Neo4jSimpleClient(new Neo4jConfig("bolt://localhost:7687", "neo4j", "pokemon"))
      .transaction()
      .use { transaction =>
        val testParam: String = "xd"
        val res = cypher"MATCH (x) WHERE p = $testParam"
        println(res)
        cypher"OPTIONAL MATCH (x) RETURN 'a' as a, 1 as b, true as c, {d: 1.5, f: [true, true, false] } as d"
          .run(transaction)
          .list[TestNeo4j]
      }
      .flatMap(result => IO { logger.info(s"Result: ${result.toString})") })

}
