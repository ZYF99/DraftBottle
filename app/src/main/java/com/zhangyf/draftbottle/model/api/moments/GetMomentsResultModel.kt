package com.zhangyf.draftbottle.model.api.moments


import com.zhangyf.draftbottle.model.api.SimpleProfileResp
import com.zhangyf.draftbottle.model.api.mine.UsrProfile
import java.io.Serializable


data class GetMomentsResultModel(
	val momentContentList: List<MomentContent>,
	val pageInfoResp: PageInfoResp
)

data class MomentContent(
	val momentId: Long,
	val publisher: SimpleProfileResp,
	val originPublisher: SimpleProfileResp,
	val topic: String,
	val title: String,
	val classify: Int,
	val content: String,
	val pictures: List<String>,
	val publishedDate: Long,
	val latitude: Double,
	val longitude: Double,
	val publishType: String,
	val momentCommentList: List<MomentComment>
) {
	val realCommentList: List<MomentComment>?
		get() {
			return momentCommentList.filter { it.commentType == COMMENT_COMMENT }
		}
	
	val likeList: List<MomentComment>?
		get() {
			return momentCommentList.filter { it.commentType == COMMENT_LIKE }
		}
	

}

data class MomentComment(
	val commentId: Long,
	val commentType: Int,
	val content: String = "favor",
	val commentator: SimpleProfileResp,
	val commentDate: Long,
	val commentReplyList: List<CommentReply>
)

data class CommentReply(
	val replyId: Long,
	val content: String,
	val replyBy: UsrProfile,
	val replyDate: Long
):Serializable

data class PageInfoResp(
	val pageNum: Int,
	val pageSize: Int,
	val size: Int,
	val startRow: Int,
	val endRow: Int,
	val pages: Int,
	val prePage: Int,
	val nextPage: Int,
	val hasPreviousPage: Boolean,
	val hasNextPage: Boolean,
	val firstPage: Boolean,
	val lastPage: Boolean
)