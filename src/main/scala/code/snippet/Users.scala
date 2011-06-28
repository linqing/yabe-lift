package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import code.model._
import Helpers._

class Users {
  def list:CssSel = {
    val users = User.findAll()
    var odd = "even"
    "#users" #> users.map{
      u => 
	odd=oddOrEven(odd);
      "tr [class]" #> odd &
      "a [href]" #> ("/admin/user/"+u.id.toString) &
      "a *" #> u.email
    }
  }

  def add:CssSel = {
   val user = User.create

    def process()= {
      user.validate match {
	case Nil => println("No error");S.redirectTo("/")
	case errors => println("has error");S.redirectTo("/admin/")
      }
      //S.redirectTo("/admin/users/add")
    }
    "#email" #> SHtml.onSubmit(user.email.set(_)) &
    "#password" #> SHtml.onSubmit(user.password.set(_))&
    "type=submit" #> SHtml.onSubmitUnit(process)
  }
  private def oddOrEven(current:String) = {
    current match {
      case "odd" => "even"
      case _ => "odd"
    }
  }
}
