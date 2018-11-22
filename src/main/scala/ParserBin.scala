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
    println("Cypher: " + cypherQuery + ", Output file: " + outputFile)

    CypherToJson.parseCypherToJson(cypherQuery.toString(), outputFile)
  }
}
