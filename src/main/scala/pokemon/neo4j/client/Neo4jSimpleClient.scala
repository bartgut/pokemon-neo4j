package pokemon.neo4j.client

import cats.effect.IO
import cats.effect.kernel.Resource
import org.neo4j.driver.{AuthTokens, GraphDatabase, Result, Session, Transaction}
import pokemon.neo4j.config.Neo4jConfig
import cats.implicits.*
import pokemon.conversion.FromValue

import scala.collection.JavaConverters.*

class Neo4jSimpleClient(config: Neo4jConfig) {

  private def initializeDriver() =
    GraphDatabase.driver(config.uri, AuthTokens.basic(config.user, config.password))

  private def session(): Resource[IO, Session] =
    Resource.make( IO.blocking { initializeDriver().session() })(session => IO { session.close() })

  def transaction(): Resource[IO, Transaction] =
    session().flatMap {
      session => Resource.make(IO { session.beginTransaction() })(transaction => IO(transaction.commit()) *> IO(transaction.close()))
    }

  def writeQuery(cypher: String): Resource[IO, Result] =
    session().map {
      session => session.writeTransaction(tx => tx.run(cypher))
    }

}
