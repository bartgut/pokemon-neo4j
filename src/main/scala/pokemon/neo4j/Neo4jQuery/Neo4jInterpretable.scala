package pokemon.neo4j.Neo4jQuery

import cats.effect.kernel.Sync
import org.neo4j.driver.{Result, Transaction}
import scala.collection.JavaConverters.*
import pokemon.neo4j.Neo4jQuery.CypherQuery
import cats.effect.IO

trait Neo4jInterpretable[A] {
  extension(a: A)
    def run(transaction: Transaction): IO[Result]
}

object Neo4jInterpretable {

  given Neo4jInterpretable[CypherQuery] with
    extension (a: CypherQuery)
      def run(transaction: Transaction): IO[Result] =
        IO.delay(transaction.run(a.query, a.params.asJava))

}
