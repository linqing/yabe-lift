package code.lib

object YabeHelper {
  /**
   * Control list style from posts, users, etc..
   */
  def oddOrEven(current:String) = {
    current match {
      case "odd" => "even"
      case _ => "odd"
    }
  }
  
  def fmtDateStr(date:java.util.Date) = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd")
    format.format(date)
  }
}