package me.snaptime.reply.dto.res;

import com.querydsl.core.Tuple;
import lombok.AccessLevel;
import lombok.Builder;

import static me.snaptime.reply.domain.QParentReply.parentReply;
import static me.snaptime.user.domain.QUser.user;


@Builder(access = AccessLevel.PRIVATE)
public record ParentReplyFindResDto(

        String writerEmail,
        String writerProfilePhotoURL,
        String writerUserName,
        String content,
        Long replyId,
        String timeAgo,
        Long childReplyCnt
) {
    public static ParentReplyFindResDto toDto(Tuple tuple, String profilePhotoURL, String timeAgo, Long childReplyCnt){
        return ParentReplyFindResDto.builder()
                .writerEmail(tuple.get(user.email))
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(tuple.get(user.nickname))
                .content(tuple.get(parentReply.content))
                .replyId(tuple.get(parentReply.parentReplyId))
                .timeAgo(timeAgo)
                .childReplyCnt(childReplyCnt)
                .build();
    }
}
