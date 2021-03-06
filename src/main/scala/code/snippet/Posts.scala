package code.snippet

import scala.xml._
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
        ".post-title-link [href]" #> ("/read/"+p.id) &
        ".post-title-link *" #> p.title.get &
        ".post-author *" #> (p.author.getAuthor.firstName + " " +
            p.author.getAuthor.lastName) &
        ".post-date *" #> YabeHelper.fmtDateStr(p.postedAt.get) &
        ".post-comments *" #> (" | " + "2 comments, lastest by aaaa") &
        "#post-content-span" #>  Unparsed(p.content.is.replaceAll("\n","<br />")) 
      }
      case _ =>   "#aa" #> <ha></ha>
    }
  }

  def listOlder:CssSel = {
    val latestPost = Post.find(OrderBy(Post.id,Descending))
    
    latestPost match {
      case Full(p) => {
        val olderPosts = Post.findAll(OrderBy(Post.id,Descending)).
        		filter(p.id!=_.id)
        "*" #> olderPosts.map{
          p=>
            ".post-title-link [href]" #> ("/read/"+p.id) &
            ".post-title-link *" #> p.title.get &
            ".post-author *" #> (p.author.getAuthor.firstName + " " +
	            p.author.getAuthor.lastName) &
	        ".post-date *" #> YabeHelper.fmtDateStr(p.postedAt.get) 
	    }
      }
      case _ => "*" #> ""
    }

  }
  
  def read:CssSel = {
    val post = Post.find(By(Post.id,S.param("id").openTheBox.toLong))
    
    post match {
      case Full(p) => {
        ".post-title-link [href]" #> ("/read/"+p.id) &
        ".post-title-link *" #> p.title.get &
        ".post-author *" #> (p.author.getAuthor.firstName + " " +
            p.author.getAuthor.lastName) &
        ".post-date *" #> YabeHelper.fmtDateStr(p.postedAt.get) &
        ".post-content-span" #>  Unparsed(p.content.is.replaceAll("\n","<br />")) 
      }
      case _ => "*" #> ""
    }
  }
  
  def prev:CssSel = {
    val currentPostId = S.param("id").openTheBox.toLong
    val prevPost = 
      Post.find(OrderBy(Post.id,Descending),
        By_<(Post.id,currentPostId))
    
    prevPost match {
      case Full(p)=> {
        "a [href]" #> ("/read/"+p.id.get) &
        "a *" #> p.title.get
      }
      case _ => "*" #> ""
    }
  }
  
  def next:CssSel = {
	val currentPostId = S.param("id").openTheBox.toLong
    val nextPost = 
      Post.find(OrderBy(Post.id,Ascending),
        By_>(Post.id,currentPostId))
    
    nextPost match {
      case Full(p)=> {
        "a [href]" #> ("/read/"+p.id.get) &
        "a *" #> p.title.get
      }
      case _ => "*" #> ""
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