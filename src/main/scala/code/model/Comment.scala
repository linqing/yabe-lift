package code.model
import net.liftweb.mapper._
import net.liftweb.util._

class Comment extends LongKeyedMapper[Comment] with IdPK {
	def getSingleton = Comment

	object author extends MappedString(this,140)
	object content extends MappedText(this) {
	  override def validations = {
		def notNull(txt:String ) = {
		  if(txt=="")
		    List(FieldError(this,"Please input content."))
		  else
		    List[FieldError]()
		}
	    
		notNull _ :: Nil
	  }
	}
	object postedAt extends MappedDateTime(this)
	object post extends LongMappedMapper(this,Post) {
	  override def validations = {
	    def validatePost(id:Long) =  {
	      val posts = Post.findAll(By(Post.id, id))
	      posts match {
	        case Nil => List(FieldError(this,"Please add comments to valid posts."))
	        case _ => List[FieldError]()
	      }
	    }
	    
	    validatePost _ :: Nil
	  }
	}
}

object Comment extends Comment with LongKeyedMetaMapper[Comment] with CRUDify[Long,Comment]
