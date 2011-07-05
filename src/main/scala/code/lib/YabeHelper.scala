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
}