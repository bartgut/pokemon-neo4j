package pokemon

import cats.effect.{IO, IOApp}
import com.typesafe.scalalogging.LazyLogging
import pokemon.neo4j.client.Neo4jSimpleClient
import pokemon.neo4j.config.Neo4jConfig
import pokemon.neo4j.Neo4jQuery.Neo4jInterpretable.given
import pokemon.neo4j.Neo4jQuery.ResultConverter.*
import pokemon.TestNeo4j
import cats.implicits.*
import conversion.OpaqueConversions.given
import dataModel.TypeData
import pokemon.neo4j.Neo4jQuery.CypherStringQuery.cypher
import scala.collection.JavaConverters.*
import scala.language.implicitConversions
import pokemon.neo4j.Neo4jQuery.CypherQueryUtil.given
import pokemon.neo4j.Neo4jQuery.ToCypherQueryParam.given
import pokemon.neo4j.Neo4jQuery.CypherQuery
import org.neo4j.driver.Values.parameters

object Application extends IOApp.Simple with LazyLogging {

  override def run: IO[Unit] =
    val t = ApiConnection.getTypeData.map(_.name.toString)
      Neo4jSimpleClient(new Neo4jConfig("bolt://localhost:7687", "neo4j", "pokemon"))
        .transaction()
        .use { transaction =>
          t.traverse { name =>
            cypher"CREATE (x:Type{name:$name}) RETURN x.name as name"
              .run(transaction)
              .list[String]
          }
        }
        .flatMap(result => IO { logger.info(s"Result: ${result.toString})") })

}
