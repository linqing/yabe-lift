package code.snippet

import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.mapper._
import js._
import JsCmds._
import JE._
import java.util.Date
import Helpers._
import code.model._
import code.lib._

class Comments {
  object postId extends RequestVar(S.param("id").openTheBox.toLong)
  
  def add = {
	val comment = Comment.create
	var captchaCode = ""
	//User curry function to keep postId, otherwise, postId will be lost in ajax request
	def process(id:Long)():JsCmd = {
	  comment.postedAt.set(new java.util.Date)
	  comment.post.set(id)
	  println()
	  println(S.getSessionAttribute("captcha"))
	  println()
	  if((S.getSessionAttribute("captcha") openOr "") != captchaCode) {
	    S.error("captcha_error","Captcha is not correct.");Noop
	  } else {
	    comment.validate match {
		  case Nil => comment.save; Noop
		  case errors => S.error(errors); Noop
	  	}
	  }
	} 
	
	"name=author" #> SHtml.text(comment.author.get, comment.author.set(_)) &
	"name=code" #> SHtml.text(captchaCode, captchaCode = _) &
	"name=content" #> (SHtml.textarea(comment.content.get, comment.content.set(_)) ++ 
	SHtml.hidden(process(postId.is)))
  }
}