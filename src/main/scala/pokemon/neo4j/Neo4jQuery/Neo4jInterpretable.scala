package pokemon.neo4j.Neo4jQuery

import cats.effect.kernel.Sync
import org.neo4j.driver.{Result, Transaction}
import scala.collection.JavaConverters.*
import pokemon.neo4j.Neo4jQuery.Query
import cats.effect.IO

trait Neo4jInterpretable[A] {
  extension(a: A)
    def execute(transaction: Transaction): IO[Result]
}

object Neo4jInterpretable {

  given Neo4jInterpretable[Query] with
    extension (a: Query)
      def execute(transaction: Transaction): IO[Result] =
        IO.delay(transaction.run(a.query, a.params.asJava))

}
