package entity
import spray.json._

case class QueryGraph(is_directed: Boolean,
                      is_labelled: Boolean,
                      vertices: List[(String, Option[String])],
                      edges: List[(String, String, Option[String])],
                      partial_order: Option[List[(String, String)]])

// Json serialization and deserialization support
object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val queryFormat = jsonFormat5(QueryGraph)
}
