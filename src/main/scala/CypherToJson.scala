import org.slizaa.neo4j.opencypher.openCypher.Cypher
import org.slizaa.neo4j.opencypher.openCypher.impl._

object CypherToJson{
  def parseCypherToJson(queryStr: String) = {
    val cypher: Cypher = CypherParser.parseString(queryStr)
    var cypher_impl: CypherImpl = null
    if (cypher.isInstanceOf[CypherImpl]){

      cypher_impl = cypher.asInstanceOf[CypherImpl]
      var statement: SinglePartQueryImpl = null

      if (cypher_impl.getStatement().isInstanceOf[SinglePartQueryImpl]) {
        statement = cypher_impl.getStatement().asInstanceOf[SinglePartQueryImpl]
        val readingClauses = statement.getReadingClauses()
        val readingClauseIter = readingClauses.iterator()

        while (readingClauseIter.hasNext){
          val readingClause = readingClauseIter.next()

          if (readingClause.isInstanceOf[MatchImpl]){
            val matchImpl = readingClause.asInstanceOf[MatchImpl]
            val patterns = matchImpl.getPattern().getPatterns()
            val patternsIter = patterns.iterator()

            while (patternsIter.hasNext()) {
              val p = patternsIter.next()

              if (p.isInstanceOf[PatternElementImpl]) {
                val pat = p.asInstanceOf[PatternElementImpl]
                val node = pat.getNodepattern()
                var nodeLabels: List[String] = List()
                val nodeLabelIter = node.getNodeLabels().iterator()

                while(nodeLabelIter.hasNext()){
                  val labelStr = nodeLabelIter.next().getLabelName()
                  nodeLabels = labelStr::nodeLabels
                }

                println("node name:" + node.getVariable().getName() + ", node labels: " + nodeLabels)
                val elementChains = pat.getChain()
                var lastNode = node
                val chainIter = elementChains.iterator()

                while (chainIter.hasNext()) {
                  val chain = chainIter.next()

                  if (chain.isInstanceOf[PatternElementChainImpl]) {
                    val chainElem = chain.asInstanceOf[PatternElementChainImpl]
                    val relationshipType = chainElem.getRelationshipPattern()
                    val curNode = chainElem.getNodePattern()
                    var curNodeLabels: List[String] = List()
                    val curNodeLabelIter = curNode.getNodeLabels.iterator()

                    while(curNodeLabelIter.hasNext()){
                      val labelStr = curNodeLabelIter.next().getLabelName()
                      curNodeLabels = labelStr::curNodeLabels
                    }
                    println("node name:" + curNode.getVariable().getName() + ", node labels: " + curNodeLabels)
                    println("Relationship name: " + relationshipType.getDetail().getRelTypeNames() +
                      ", incoming: " + relationshipType.isIncoming() + ", outgoing: " +
                      relationshipType.isOutgoing() + ", between " + lastNode.getVariable().getName()
                      + " and " + curNode.getVariable().getName())
                    lastNode = curNode
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}