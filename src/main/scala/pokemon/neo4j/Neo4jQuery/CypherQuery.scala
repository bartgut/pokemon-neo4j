package pokemon.neo4j.Neo4jQuery

case class CypherQuery(query: String, params: Map[String, Object])

object CypherQuery {
  def fromString(query: String) = CypherQuery(query, Map.empty)
}
