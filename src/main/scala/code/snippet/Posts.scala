package code.snippet

import scala.xml.{ NodeSeq, Text}
import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.mapper._
import java.util.Date
import Helpers._
import code.model._
import code.lib._

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
  
  //Count the number of posts that posted by current user
  def countByUser:CssSel = { 
	val userId = User.currentUserId.openTheBox
	val count = Post.count(By(Post.author, userId.toLong))
    "span" #> count
  }
  
  //list posts posted by this user
  def listByUser:CssSel = {
    val userId = User.currentUserId.openTheBox
    val posts = Post.findAll(By(Post.author, userId.toLong))
    
    var odd = "even"
    "*" #> posts.map {
      post =>
      odd=YabeHelper.oddOrEven(odd)
      
      "p [class]" #> ("post "+odd) &
      "a [href]" #> ("/admin/posts/edit/"+post.id) &
      "a *" #> post.title
    }
  }
  
  def add:CssSel = {
    val post = Post.create
    
    def process() = {
      post.author.set(User.currentUserId.openTheBox.toInt)
      post.postedAt.set(new Date())
      post.validate match {
        case Nil => {
          post.save
          S. redirectTo("/admin/posts/index")
        }
        case errors => S.error(errors)
      }
    }
    
    "name=title" #> SHtml.onSubmit(post.title.set(_)) &
    "name=content" #> SHtml.onSubmit(post.content.set(_)) &
    "type=submit" #> SHtml.onSubmitUnit(()=>process)
  }
  
  def edit:CssSel = {
    val id = S.param("id").openTheBox
    val post = Post.find(By(Post.id,id.toLong)).openTheBox
    
    def process() = {
      post.validate match {
        case Nil => {
          post.save
          S.redirectTo("/admin/posts/index")
        }
        case errors => S.error(errors)
      }
    }
    
    if(post.author.toLong != User.currentUserId.openTheBox.toLong) {
      "*" #> <span>Sorry, you do not have permission to edit this post in this page.</span>
    } else {
      "name=title" #> SHtml.text(post.title,post.title.set(_)) &
      "name=content" #> SHtml.textarea(post.content, post.content.set(_)) &
      "type=submit" #> SHtml.onSubmitUnit(process)
    }
  }
  
  def getUserName: CssSel = {
    val firstName = User.currentUser.openTheBox.firstName
    val lastName = User.currentUser.openTheBox.lastName
    
    "span" #> (firstName +" "+ lastName)
  }
}