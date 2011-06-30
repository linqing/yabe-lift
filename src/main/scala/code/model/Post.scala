package code.model

import net.liftweb.mapper._

class Post extends LongKeyedMapper[Post] with IdPK{
	def getSingleton = Post
	//object author extends LongMappedMapper(this,User)
	object title extends MappedString(this,140)
	object content extends MappedText(this)
	object postedAt extends MappedDateTime(this)
}

object Post extends Post with LongKeyedMetaMapper[Post] with CRUDify[Long,Post]
