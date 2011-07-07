package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.mapper._
import code.model._
import Helpers._
import code.lib._

class Users {
  def list:CssSel = {        
    val users = getUsers()
    var odd = "even"
    "#users" #> users.map{
      u => 
      odd=YabeHelper.oddOrEven(odd);
      "tr [class]" #> odd &
      "a [href]" #> ("/admin/users/edit/"+u.id.toString) &
      "a *" #> u.email
    }
  }
  
  def add:CssSel = {
   val user = User.create

    def process()= {
      user.validate match {
		case Nil => {
		  user.validated.set(true)
		  user.save
		  S.redirectTo("/admin/users/")
		}
		case errors => S.error(errors)
	  }
      //S.redirectTo("/admin/users/add")
    }
   	
    "#email" #> SHtml.onSubmit(user.email.set(_)) &
    "#password" #> SHtml.onSubmit(user.password.set(_)) &
    "#firstname" #> SHtml.onSubmit(user.firstName.set(_)) &
    "#lastname" #> SHtml.onSubmit(user.lastName.set(_)) &
    "#isAdmin" #> SHtml.onSubmit((x:String)=>user.superUser.set(x.toBoolean)) &
    "type=submit" #> SHtml.onSubmitUnit(process)
  }
  
  def edit:CssSel = {
    val id = S.param("id").openTheBox
    val user = User.find(By(User.id,id.toLong)).openTheBox
    
    def process() = {
      user.validate match {
        case Nil => {
          user.save
          S.redirectTo("/admin/users/")
        }
        case errors => S.error(errors)
      }
    }
    
    "#email" #> SHtml.text(user.email,user.email.set(_)) &
    "#password" #> SHtml.text(user.password,user.password.set(_)) & 
    "#firstname" #> SHtml.text(user.firstName,user.firstName.set(_)) &
    "#lastname" #> SHtml.text(user.lastName,user.lastName.set(_)) &
    "#isAdmin" #> SHtml.checkbox(user.superUser, user.superUser.set(_)) &
    "type=submit" #> SHtml.onSubmitUnit(process)
  }
  
  def delete:CssSel = {
    val id = S.param("id").openTheBox
    val user = User.find(By(User.id,id.toLong)).openTheBox
    
    def process() = {
      user.delete_!
      S.redirectTo("/admin/users/")
    }
    
    "type=submit" #> SHtml.onSubmitUnit(process)
  }
  
  def count:CssSel = {
    "span" #> countUsers
  }
  
  private def getUsers() = {
    if(validSearch()) {
      User.findAll(Like(User.email,"%"+S.param("search").openTheBox+"%"))
    } else
      User.findAll()
  }
  
  private def countUsers() = {
    if(validSearch()) {
      User.count(Like(User.email,"%"+S.param("search").openTheBox+"%"))
    } else
      User.count()
  }
  
  private def validSearch() = S.request.openTheBox.post_? && S.param("Search")!=Empty
}
