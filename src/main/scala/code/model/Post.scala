package code.model

import net.liftweb.mapper._
import net.liftweb.http._
import net.liftweb.common._
import net.liftweb.util._

class Post extends LongKeyedMapper[Post] with IdPK{
	def getSingleton = Post
	
	object author extends LongMappedMapper(this,User) {
	  override def validSelectValues = {
	    val users = User.findAll().map(x=>(x.id.get,x.email.get))
	    val list = (0.toLong,"(Please select a user)")::users
	    Full(list)
	  }
	}
	
	object title extends MappedString(this,140) {
	  override def validations = {
	    valMinLen(1, "Please input title.") _::Nil
	  }
	}
	
	object content extends MappedText(this) {
	  override def validations = {
		def notNull(txt:String ) = {
		  if(txt=="")
		    List(FieldError(this,"Please input content"))
		  else
		    List[FieldError]()
		}
	    
		notNull _ :: Nil
	  }
	}
	
	object postedAt extends MappedDateTime(this)
}

object Post extends Post with LongKeyedMetaMapper[Post] with CRUDify[Long,Post]
