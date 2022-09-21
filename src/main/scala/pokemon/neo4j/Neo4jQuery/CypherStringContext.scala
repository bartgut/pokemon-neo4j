package pokemon.neo4j.Neo4jQuery

import pokemon.neo4j.Neo4jQuery.CypherQuery 
import pokemon.neo4j.Neo4jQuery.CypherQueryParam
import scala.collection.mutable

object CypherStringQuery {
  extension (sc: StringContext)
    def cypher(args: CypherQueryParam[?]*): CypherQuery =
      val it = sc.parts.zipWithIndex.iterator
      val sb = new StringBuilder()
      while (it.hasNext) {
        val (queryPart, index) = it.next
        sb.append(queryPart)
        if (it.hasNext) {
          sb.append(s"$$param$index")
        }
      }
      val paramMap = args.iterator.zipWithIndex.map { (param, index) =>
        (s"param$index" -> param.asParam)
      }.toMap
      CypherQuery(sb.result(), paramMap)
}
