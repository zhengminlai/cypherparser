package parser

import java.io.{File, PrintWriter}

import entity.QueryGraph
import org.slizaa.neo4j.opencypher.openCypher.{Cypher, NodePattern, RelationshipPattern}
import org.slizaa.neo4j.opencypher.openCypher.impl._
import entity.MyJsonProtocol._
import exception.NoPatGraphException
import spray.json._

import scala.collection.mutable

// Convert Cypher to query graph in json
object CypherToJson {
  var isDirected = false
  var isLabelled = false
  var vertices: List[(String, Option[String])] = List()
  var edges: List[(String, String, Option[String])] = List()
  val nodeSet: mutable.HashSet[String] = mutable.HashSet()
  val edgeSet: mutable.HashSet[(String, String)] = mutable.HashSet()

  // Parse the given node
  def parseNode(node: NodePattern) = {
    var nodeLabelStr: String = ""
    var nodeLabel: Option[String] = None
    val nodeLabelIter = node.getNodeLabels.iterator()

    if (!nodeSet.contains(node.getVariable.getName)) {
      while (nodeLabelIter.hasNext) {
        val labelStr = nodeLabelIter.next.getLabelName
        nodeLabelStr += labelStr + ","
      }
      if (nodeLabelStr.length() > 1) {
        nodeLabelStr = nodeLabelStr.dropRight(1)
        isLabelled = true
        if (nodeLabelStr.length() > 0) {
          nodeLabel = Some(nodeLabelStr)
        }
      }
      nodeSet.add(node.getVariable.getName)
      vertices = (node.getVariable.getName, nodeLabel) :: vertices
    }
  }

  // Parse the given edge
  def parseEdge(relationshipType: RelationshipPattern, lastNode: NodePattern, curNode: NodePattern) = {
    var edgeLabel: Option[String] = None
    // Note that we drop the '[' and ']' in the edge label
    val edgeLabelStr: String = relationshipType.getDetail.getRelTypeNames.toString.dropRight(1).drop(1)

    if (edgeLabelStr.length() > 0) {
      edgeLabel = Some(edgeLabelStr)
    }

    if (relationshipType.isIncoming || relationshipType.isOutgoing) {
      isDirected = true
    }

    val lastNodeName = lastNode.getVariable.getName
    val curNodeName = curNode.getVariable.getName
    if (!edgeSet.contains(lastNodeName, curNodeName)
      || !edgeSet.contains(curNodeName, lastNodeName)) {
      if (relationshipType.isIncoming) {
        edgeSet.add((curNodeName, lastNodeName))
        edges = (curNodeName, lastNodeName, edgeLabel) :: edges
      }
      if (relationshipType.isOutgoing) {
        edgeSet.add((lastNodeName, curNodeName))
        edges = (lastNodeName, curNodeName, edgeLabel) :: edges
      }
      if (!relationshipType.isIncoming && !relationshipType.isOutgoing) {
        edgeSet.add((lastNodeName, curNodeName))
        edgeSet.add((curNodeName, lastNodeName))
        edges = (lastNodeName, curNodeName, edgeLabel) :: edges
        edges = (curNodeName, lastNodeName, edgeLabel) :: edges
      }
    }
  }

  def parseCypherToJson(queryStr: String, outputPath: String) = {
    val cypher: Cypher = CypherParser.parseString(queryStr)
    cypher match {
      case cypherImpl: CypherImpl =>

        cypherImpl.getStatement match {
          case statement: SinglePartQueryImpl =>
            val readingClauses = statement.getReadingClauses
            val readingClauseIter = readingClauses.iterator

            while (readingClauseIter.hasNext) {
              val readingClause = readingClauseIter.next

              readingClause match {
                case matchImpl: MatchImpl =>
                  val patterns = matchImpl.getPattern.getPatterns
                  val patternsIter = patterns.iterator

                  while (patternsIter.hasNext) {
                    val p = patternsIter.next

                    p match {
                      case pat: PatternElementImpl =>
                        val node = pat.getNodepattern
                        parseNode(node)

                        val elementChains = pat.getChain
                        var lastNode = node
                        val chainIter = elementChains.iterator

                        while (chainIter.hasNext) {
                          val chain = chainIter.next()

                          chain match {
                            case chainElem: PatternElementChainImpl =>
                              val relationshipType = chainElem.getRelationshipPattern
                              val curNode = chainElem.getNodePattern

                              // parse node
                              parseNode(curNode)

                              // parse edge
                              parseEdge(relationshipType, lastNode, curNode)

                              lastNode = curNode
                            case _ => throw new NoPatGraphException(s"Error during parsing pattern graph: No pattern graph found")
                          }
                        }
                      case _ => throw new NoPatGraphException(s"Error during parsing pattern graph: No pattern graph found")
                    }
                  }
                case _ => throw new NoPatGraphException(s"Error during parsing pattern graph: No pattern graph found")
              }
            }
            println("Parsed query node len:" + vertices.length + ", edge len:" + edges.length)
            if (vertices.length == 0) {
              throw new NoPatGraphException(s"Error during parsing pattern graph: No pattern graph found")
            }
            val query_graph = QueryGraph(isDirected, isLabelled, vertices, edges, None).toJson
            val jsonQuery = query_graph.prettyPrint

            val writer = new PrintWriter(new File(outputPath))
            writer.write(jsonQuery)
            writer.close()
          case _ => throw new NoPatGraphException(s"Error during parsing pattern graph: No pattern graph found")
        }
      case _ => throw new NoPatGraphException(s"Error during parsing pattern graph: No pattern graph found")
    }
  }
}