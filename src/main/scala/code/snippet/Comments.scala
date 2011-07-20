package code.snippet

import scala.xml.Unparsed
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
import code.comet._

class Comments {
  object postId extends RequestVar(S.param("id").openTheBox.toLong)
  
  def add = {
	var comment = Comment.create
	var captchaCode = ""
	  
	//User curry function to keep postId, otherwise, postId will be lost in ajax request
	def process(id:Long)():JsCmd = {
	  comment.postedAt.set(new java.util.Date)
	  comment.post.set(id)

	  if((S.getSessionAttribute("captcha") openOr "") != captchaCode) {
	    JE.Call("clearError") &
	    JE.Call("showError",Str("Captcha is not correct."))
	  } else {
	    comment.validate match {
		  case Nil => { 
		    comment.save
		    //prepare for another comment
		    comment = Comment.create 
		    
		    CommentsServer ! id
		    
		    JE.Call("clearError") & JE.Call("clearForm") }
		  case errors => S.error(errors); JE.Call("clearError") & JE.Call("showError",Str(errors.head.msg.toString))
	  	}
	  }
	} 
		
	"name=author" #> SHtml.text(comment.author.get, comment.author.set(_)) &
	"name=code" #> SHtml.text(captchaCode, captchaCode = _) &
	"name=content" #> (SHtml.textarea(comment.content.get, comment.content.set(_),"id"->"content") ++ 
	SHtml.hidden(process(postId.is)))
  }
  
  def initComet = {
   CommentsServer ! postId.is
    "*" #> ""
  }

  /******************************************************************************************
   * For admin panel..
   * ****************************************************************************************/
  private object searchStr extends RequestVar("")
  
  def list:CssSel = {
    val comments = getComments()
    var odd = "even"
    "tr" #> comments.map {
      c=>
	    odd = YabeHelper.oddOrEven(odd);
		"tr [class]" #> odd &
		"a [href]" #> ("/admin/comments/edit/"+c.id) &
		"a *" #> c.content.short
	}
  }
  
  private def getComments() = {
    val comments = validSearch() match {
      case x if x==true => Comment.findAll(
          BySql(" content like '%"+searchStr.is+"%'", 
              IHaveValidatedThisSQL("charliechen","2011-07-11")),
          OrderBy(Comment.content,Ascending))
          
      case _ => Comment.findAll(OrderBy(Comment.content, Ascending))
    }
    
    getCommentsOrder match {
      case "DESC" => comments.reverse
      case "ASC" => comments
    }
  }
  
  private def validSearch() = searchStr.is!=""
    
  private def getCommentsOrder = {
    S.param("order") match {
      case Full(p) if p=="DESC" => "DESC"
      case _ => "ASC"
    }
  }
}