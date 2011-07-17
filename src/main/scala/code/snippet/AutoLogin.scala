package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.mapper._
import code.model._
import Helpers._

object AutoLogin {	
  def render:CssSel = {
	def process() = {
	  val u = User.findAll(By(User.email,"pkufashuo400@gmail.com"))
	  if(u.length>0)
	    User.logUserIn(u.head, ()=>S.redirectTo("/admin/posts/index"))
	}
    "type=submit" #> SHtml.onSubmitUnit(process)
  }
}
