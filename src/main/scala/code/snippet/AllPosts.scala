package code.snippet

import net.liftweb.util._
import net.liftweb.http._
import net.liftweb.common._
import net.liftweb.mapper._
import Helpers._
import code.model._
import code.lib._

class AllPosts {
	
  private object searchStr extends RequestVar("")
  
  def list:CssSel = {
    val posts = getPosts()
    var odd = "even"
    "tr" #> posts.map {
      p=>
	    odd = YabeHelper.oddOrEven(odd);
		"tr [class]" #> odd &
		"a [href]" #> ("/admin/all_posts/edit/"+p.id) &
		"a *" #> p.title
	}
  }
  
  def search:CssSel = {
    "name=search" #> SHtml.textElem(searchStr)
  }
  
  def count = {
    "span" #> countPosts
  }
  
  def sort = {
    val search = searchStr.is
    
    if(getPostsOrder == "DESC")
      "a [class]" #> "crudSortedDesc" &
      "a" #> SHtml.link("/admin/all_posts/index?order=ASC", 
          ()=>searchStr(search), 
          <span>Posts</span>,
          "class"->"crudSortedDesc")
    else 
      "a [class]" #> "crudSortedAsc" &
      "a" #> SHtml.link("/admin/all_posts/index?order=DESC", 
          ()=>searchStr(search), 
          <span>Posts</span>,
          "class"->"crudSortedAsc")
  }
  
  private def countPosts() = {
    if(validSearch()) {
      Post.count(BySql(" title like '%"+searchStr.is+"%' or content like '%"+searchStr.is+"%'", 
              IHaveValidatedThisSQL("charliechen","2011-07-11")))
    } else
      Post.count()
  }
  
  private def getPosts() = {
    val posts = validSearch() match {
      case x if x==true => Post.findAll(
          BySql(" title like '%"+searchStr.is+"%' or content like '%"+searchStr.is+"%'", 
              IHaveValidatedThisSQL("charliechen","2011-07-11")),
          OrderBy(Post.title,Ascending))
          
      case _ => Post.findAll(OrderBy(Post.title,Ascending))
    }
    
    getPostsOrder match {
      case "DESC" => posts.reverse
      case "ASC" => posts
    }
  }
  
  private def validSearch() = searchStr.is!=""
 
  
  private def getPostsOrder = {
    S.param("order") match {
      case Full(p) if p=="DESC" => "DESC"
      case _ => "ASC"
    }
  }
}

class AllPostsAdd extends StatefulSnippet {
  
  var post = Post.create
  
  def dispatch = { case "render" => render }
  
  def render = {
    val post = Post.create
    
    def process() = {
      //post.author.set(User.currentUserId.openTheBox.toInt)
      //post.postedAt.set(new Date())
      post.validate match {
        case Nil => {
          post.save
          S. redirectTo("/admin/all_posts/index")
        }
        case errors => S.error(errors)
      }
    }
    
    "name=title" #> SHtml.text(post.title, post.title.set(_)) &
    "name=content" #> SHtml.textarea(post.content, post.content.set(_)) &
    "name=postedAt" #> post.postedAt.toForm & 
    "name=author_id" #> post.author.toForm &
    "type=submit" #> SHtml.onSubmitUnit(()=>process)
  }
}

/*class PostsEdit extends StatefulSnippet {
  
}*/