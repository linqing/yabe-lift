package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.mapper._
import java.util.Date
import Helpers._
import code.model._

class Posts {
  def listLatest:CssSel = {
    val latestPost = Post.find(OrderBy(Post.id,Descending))
    
    latestPost match {
      case Full(p) => {
        ".post-title-link *" #> p.title.get
      }
      case _ =>   "#aa" #> <ha></ha>
    }
  }
  
  def listForUser:CssSel = {
    val userId = User.currentUserId.openTheBox
    //val posts = Post.findAll(By(Post.author))
    "" #> <ha></ha>
  }
  
  def add:CssSel = {
    val post = Post.create
    
    println(post.validate)
    def addPost() = {
      post.author.set(User.currentUserId.openTheBox.toInt)
      post.postedAt.set(new Date())
      post.validate match {
        case Nil => post.save; S. redirectTo("/admin/posts/index")
        case errors => S.error(errors)
      }
    }
    
    "name=title" #> SHtml.onSubmit(post.title.set(_)) &
    "name=content" #> SHtml.onSubmit(post.content.set(_)) &
    "type=submit" #> SHtml.onSubmitUnit(()=>addPost)
    //post.toForm(Full("save"),addPost(_))
  }
}