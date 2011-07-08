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
  }
}