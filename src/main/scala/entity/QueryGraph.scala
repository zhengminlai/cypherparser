package entity
import spray.json._

case class QueryGraph(is_directed: Boolean,
                      is_labelled: Boolean,
                      vertices: List[(Int, Option[String])],
                      edges: List[(Int, Int, Option[String])],
                      partial_order: Option[List[(Int, Int)]])


object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val queryFormat = jsonFormat5(QueryGraph)
}
