package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import code.model._
import Helpers._
import code.lib._

class Users {
  def list:CssSel = {
    val users = User.findAll()
    var odd = "even"
    "#users" #> users.map{
      u => 
      odd=YabeHelper.oddOrEven(odd);
      "tr [class]" #> odd &
      "a [href]" #> ("/admin/user/a/b/"+u.id.toString) &
      "a *" #> u.email
    }
  }

  def add:CssSel = {
   val user = User.create

    def process()= {
      user.validate match {
		case Nil => user.save;S.redirectTo("/admin/users/")
		case errors => S.error(errors)
	  }
      //S.redirectTo("/admin/users/add")
    }
    "#email" #> SHtml.onSubmit(user.email.set(_)) &
    "#password" #> SHtml.onSubmit(user.password.set(_))&
    "type=submit" #> SHtml.onSubmitUnit(process)
  }
}
