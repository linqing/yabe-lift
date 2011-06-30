package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import code.model._
import Helpers._

object AutoLogin {	
  def render:CssSel = {
	def process() = {
	  //val u = Users.findBy
	}
    "type=submit" #> SHtml.onSubmitUnit(process)
  }
}
