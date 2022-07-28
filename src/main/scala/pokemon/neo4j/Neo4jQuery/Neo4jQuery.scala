package pokemon.neo4j.Neo4jQuery

case class Query(query: String, params: Map[String, Object])

object Query {
  def fromString(query: String) = Query(query, Map.empty)
}


