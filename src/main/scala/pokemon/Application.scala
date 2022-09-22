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
import dataModel.TypeData.TypeName
import pokemon.neo4j.Neo4jQuery.CypherStringQuery.cypher

import scala.collection.JavaConverters.*
import scala.language.implicitConversions
import pokemon.neo4j.Neo4jQuery.CypherQueryUtil.given
import pokemon.neo4j.Neo4jQuery.ToCypherQueryParam.given
import pokemon.neo4j.Neo4jQuery.CypherQuery
import org.neo4j.driver.Values.parameters


object Application extends IOApp.Simple with LazyLogging {

  override def run: IO[Unit] =
    val types = ApiConnection.getTypeData
    def createNodes(t: TypeData) = cypher"CREATE (x:Type{id:${t.id.toString}, name:${t.name.toString}})"
    def createEdges(a: TypeName, b: TypeName) = cypher"MATCH (x:Type{name:${a.toString}}) MATCH (y:Type{name:${b.toString}}) CREATE (x)-[:DOUBLE_DAMAGE_TO]->(y)"

      Neo4jSimpleClient(new Neo4jConfig("bolt://localhost:7687", "neo4j", "pokemon"))
        .transaction()
        .use { transaction =>
          types.traverse { t =>
            createNodes(t)
              .run(transaction) *>
            t.damageRelations.doubleDamageTo.traverse { b => createEdges(t.name, b.name).run(transaction)}
          }
        }
        .flatMap(result => IO { logger.info(s"Result: ${result.toString})") })

}
