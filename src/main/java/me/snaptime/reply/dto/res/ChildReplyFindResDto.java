package me.snaptime.reply.dto.res;

import lombok.Builder;
import me.snaptime.reply.domain.ChildReply;


@Builder
public record ChildReplyFindResDto(

        String writerEmail,
        String writerUserName,
        String writerProfilePhotoURL,
        String content,
        String tagUserEmail,
        String tagUserName,
        Long parentReplyId,
        Long childReplyId,
        String timeAgo
) {

    public static ChildReplyFindResDto toDto(ChildReply childReply, String profilePhotoURL, String timeAgo){
        if(childReply.getReplyTagUser() == null){
            return ChildReplyFindResDto.builder()
                    .writerEmail(childReply.getWriter().getEmail())
                    .writerProfilePhotoURL(profilePhotoURL)
                    .writerUserName(childReply.getWriter().getNickname())
                    .content(childReply.getContent())
                    .parentReplyId(childReply.getParentReply().getParentReplyId())
                    .childReplyId(childReply.getChildReplyId())
                    .timeAgo(timeAgo)
                    .build();
        }

        return ChildReplyFindResDto.builder()
                .writerEmail(childReply.getWriter().getEmail())
                .writerProfilePhotoURL(profilePhotoURL)
                .writerUserName(childReply.getWriter().getNickname())
                .content(childReply.getContent())
                .tagUserEmail(childReply.getReplyTagUser().getEmail())
                .tagUserName(childReply.getReplyTagUser().getNickname())
                .parentReplyId(childReply.getParentReply().getParentReplyId())
                .childReplyId(childReply.getChildReplyId())
                .timeAgo(timeAgo)
                .build();
    }
}
