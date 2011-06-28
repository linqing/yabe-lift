/*package model
import net.liftweb.mapper._

class Comment extends LongKeyedMapper[Comment] {
	def getSingleton = Comment
	def primaryKeyField = id

	object id extends MappedLongIndex(this)
	object author extends MappedString(this,140)
	object content extends MappedText(this)
	object postedAt extends MappedDateTime(this)
	object post extends LongMappedMapper(this,Post)
}

object Comment extends Comment with LongKeyedMetaMapper[Comment]*/
