package parser

import entity.QueryGraph
import org.slizaa.neo4j.opencypher.openCypher.Cypher
import org.slizaa.neo4j.opencypher.openCypher.impl._

import entity.MyJsonProtocol._
import spray.json._

import scala.collection.mutable

object CypherToJson {
  def parseCypherToJson(queryStr: String) = {
    val cypher: Cypher = CypherParser.parseString(queryStr)
    cypher match {
      case cypherImpl: CypherImpl =>

        if (cypherImpl.getStatement.isInstanceOf[SinglePartQueryImpl]) {
          val statement = cypherImpl.getStatement().asInstanceOf[SinglePartQueryImpl]
          val readingClauses = statement.getReadingClauses()
          val readingClauseIter = readingClauses.iterator()

          var isDirected = false
          var isLabelled = false
          var vertices: List[(Int, Option[String])] = List()
          var edges: List[(Int, Int, Option[String])] = List()
          val nodeMap: mutable.HashMap[String, Int] = mutable.HashMap()
          val edgeSet: mutable.HashSet[(Int, Int)] = mutable.HashSet()
          val partialOrder = None

          var nodeIdx: Int = 0
          while (readingClauseIter.hasNext) {
            val readingClause = readingClauseIter.next()

            readingClause match {
              case matchImpl: MatchImpl =>
                val patterns = matchImpl.getPattern.getPatterns
                val patternsIter = patterns.iterator()

                while (patternsIter.hasNext) {
                  val p = patternsIter.next

                  p match {
                    case pat: PatternElementImpl =>
                      val node = pat.getNodepattern
                      var nodeLabel: String = ""
                      val nodeLabelIter = node.getNodeLabels.iterator()

                      if (!nodeMap.contains(node.getVariable.getName)) {
                        while (nodeLabelIter.hasNext) {
                          val labelStr = nodeLabelIter.next.getLabelName
                          nodeLabel += labelStr + ","
                        }
                        if (nodeLabel.length() > 1) {
                          nodeLabel = nodeLabel.dropRight(1)
                          isLabelled = true
                        }
                        println("node name:" + node.getVariable.getName + ", node label: " + nodeLabel)

                        nodeMap.put(node.getVariable.getName, nodeIdx)
                        vertices = (nodeIdx, Some(nodeLabel)) :: vertices
                        nodeIdx += 1
                      }

                      val elementChains = pat.getChain
                      var lastNode = node
                      val chainIter = elementChains.iterator()

                      while (chainIter.hasNext) {
                        val chain = chainIter.next()

                        chain match {
                          case chainElem: PatternElementChainImpl =>
                            val relationshipType = chainElem.getRelationshipPattern
                            val curNode = chainElem.getNodePattern

                            if (!nodeMap.contains(curNode.getVariable.getName)) {
                              var curNodeLabel: String = ""
                              val curNodeLabelIter = curNode.getNodeLabels.iterator()

                              while (curNodeLabelIter.hasNext) {
                                val labelStr = curNodeLabelIter.next().getLabelName
                                curNodeLabel += labelStr + ","
                              }
                              if (curNodeLabel.length() > 1) {
                                curNodeLabel = curNodeLabel.dropRight(1)
                                isLabelled = true
                              }
                              println("node name:" + curNode.getVariable.getName + ", node label: " + curNodeLabel)

                              nodeMap.put(curNode.getVariable.getName, nodeIdx)
                              vertices = (nodeIdx, Some(curNodeLabel)) :: vertices
                              nodeIdx += 1
                            }

                            val edgeLabel: String = relationshipType.getDetail.getRelTypeNames.toString
                            println("Relationship name: " + edgeLabel +
                              ", incoming: " + relationshipType.isIncoming + ", outgoing: " +
                              relationshipType.isOutgoing + ", between " + lastNode.getVariable.getName
                              + " and " + curNode.getVariable.getName)

                            if (relationshipType.isIncoming || relationshipType.isOutgoing) {
                              isDirected = true
                            }

                            assert(nodeMap.get(lastNode.getVariable.getName).isDefined)
                            assert(nodeMap.get(curNode.getVariable.getName).isDefined)

                            val lastNodeIdx = nodeMap.getOrElse(lastNode.getVariable.getName, 0)
                            val curNodeIdx = nodeMap.getOrElse(curNode.getVariable.getName, 0)
                            if (!edgeSet.contains(lastNodeIdx, curNodeIdx)) {
                              if (relationshipType.isIncoming()){
                                edgeSet.add((curNodeIdx, lastNodeIdx))
                              }else {
                                edgeSet.add((lastNodeIdx, curNodeIdx))
                              }
                              edges = (lastNodeIdx, curNodeIdx, Some(edgeLabel)) :: edges
                            }
                            lastNode = curNode
                          case _ =>
                        }
                      }
                    case _ =>
                  }
                }
              case _ =>
            }
          }
          println("Node len:" + vertices.length + ", Edge len:" + edges.length)
          val query_graph = QueryGraph(isDirected, isLabelled, vertices, edges, partialOrder).toJson
          println(query_graph.prettyPrint)

        }
      case _ =>
    }
  }
}