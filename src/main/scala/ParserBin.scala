import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException
import exception.NoPatGraphException
import parser.CypherToJson

object ParserBin {
  val usage = """
      scala ParserBin.scala <-c> "cypher" <-o> "output-file"
  """
  def main(args: Array[String]) {
    if (args.length < 4){
      println("The parameters needed have not been specified, please view the usage:\n" + usage)
      return
    }
    val arglist = args.toList
    val cypherQuery =  StringBuilder.newBuilder
    var outputFile: String = ""
    val argIter = arglist.iterator
    var cypherEndsFlag = false
    var ifSpecifyCypher = false
    while(argIter.hasNext){
      val curStr = argIter.next
      if (!curStr.equals("-c")){
        if(!curStr.equals("-o")){
          if (!cypherEndsFlag) {
            cypherQuery.append(curStr)
          }else{
            outputFile = curStr
          }
        }else{
          cypherEndsFlag = true
        }
      }else{
        ifSpecifyCypher = true
      }
    }
    if (!ifSpecifyCypher){
      println("The `cypher` parameter has not been specified, please view the usage:\n" + usage)
      return
    }
    if(!cypherEndsFlag){
      println("The `output-file` parameter has not been specified, please view the usage:\n" + usage)
      return
    }

    try {
      CypherToJson.parseCypherToJson(cypherQuery.toString(), outputFile)
    }catch {
      case e: CompilerException => println(s"Error during cypher parsing, the first error was" + e.getMessage)
      case e: NoPatGraphException => println(s"Error during parsing pattern graph" + e.getMessage)
    }
  }
}
