
import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException
import org.scalatest.FunSuite
import parser.CypherToJson
import spray.json._
import entity.QueryGraph
import entity.MyJsonProtocol._

class CypherParserTest extends FunSuite{
  test("infer schema for ConnectedSegments") {
    try {
      CypherToJson.parseCypherToJson(
        """MATCH
          |  (sensor:Sensor)<-[mb1:monitoredBy]-(segment1:Segment),
          |  (segment1:Segment)-[ct1:connectsTo]->
          |  (segment2:Segment)-[ct2:connectsTo]->
          |  (segment3:Segment)-[ct3:connectsTo]->
          |  (segment4:Segment)-[ct4:connectsTo]->
          |  (segment5:Segment)-[ct5:connectsTo]->(segment6:Segment),
          |  (segment2:Segment)-[mb2:monitoredBy]->(sensor:Sensor),
          |  (segment3:Segment)-[mb3:monitoredBy]->(sensor:Sensor),
          |  (segment4:Segment)-[mb4:monitoredBy]->(sensor:Sensor),
          |  (segment5:Segment)-[mb5:monitoredBy]->(sensor:Sensor),
          |  (segment6:Segment)-[mb6:monitoredBy]->(sensor:Sensor)
          |RETURN sensor, segment1, segment2, segment3, segment4, segment5, segment6
          |""".stripMargin)
    } catch {
      case e: CompilerException => println(s"Error during cypher parsing, the first error was" + e.getMessage())
    }
  }

  test("Parsing query graph to json"){
    val vertices = List((0, None),(1, None), (2, None))
    val edges = List((0,1,None), (0,2,None), (1,2,None))
    val partial_order = Some(List((0,1),(0,2),(1,2)))
    val query_graph = QueryGraph(false, false, vertices, edges, partial_order).toJson
    println(query_graph.prettyPrint)
  }
}
