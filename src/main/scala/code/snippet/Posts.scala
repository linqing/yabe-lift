package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import Helpers._
import code.model._

class Posts {
  def listForUser:CssSel = {
    val userId = User.currentUserId.openTheBox
    //val posts = Post.findAll(By(Post.author))
    "" #> <ha></ha>
  } 
  
  def add(xhtml:NodeSeq):NodeSeq = {
    val post = Post.create
    def addPost(x:Post) = {
      post.save
      S.redirectTo("/admin/index")
    }
    post.toForm(Full("save"),addPost(_))
  }
}