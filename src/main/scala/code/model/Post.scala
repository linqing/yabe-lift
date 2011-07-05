package code.model

import net.liftweb.mapper._
import net.liftweb.http._
import net.liftweb.common._

class Post extends LongKeyedMapper[Post] with IdPK{
	def getSingleton = Post
	object author extends LongMappedMapper(this,User) {
	  override def toForm = {
	    super.toForm
	  }
	  
	  override def validSelectValues = {
	    val id = User.currentUserId.openTheBox.toInt
	    Full(
	      User.findAll(By(User.id, id)).
	      map(x => (x.id.get,x.email.get)))
	  }
	}
	object title extends MappedString(this,140)
	object content extends MappedText(this)
	object postedAt extends MappedDateTime(this)
}

object Post extends Post with LongKeyedMetaMapper[Post] with CRUDify[Long,Post]
