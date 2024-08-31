package me.snaptime.reply.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.snaptime.common.CommonResponseDto;
import me.snaptime.reply.dto.req.ChildReplyAddReqDto;
import me.snaptime.reply.dto.req.ParentReplyAddReqDto;
import me.snaptime.reply.dto.req.ReplyUpdateReqDto;
import me.snaptime.reply.dto.res.ChildReplyPagingResDto;
import me.snaptime.reply.dto.res.ParentReplyPagingResDto;
import me.snaptime.reply.service.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "[Reply] Reply API")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/snaps/{snapId}/parent-replies")
    @Operation(summary = "댓글 등록요청", description = "댓글을 등록할 snap의 Id와 댓글내용을 보내주세요.")
    @Parameter(name = "snapId", description = "댓글을 등록할 snap의 id", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> addParentReply(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("snapId") Long snapId,
            @RequestBody @Valid ParentReplyAddReqDto parentReplyAddReqDto){

        replyService.addParentReply(reqLoginId, snapId, parentReplyAddReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("댓글등록이 성공했습니다.",null));
    }

    @PostMapping("/snaps/parent-replies/{parentReplyId}/child-replies")
    @Operation(summary = "대댓글 등록요청", description = "대댓글을 등록할 부모댓글의 Id와 태그할 유저의 loginId,댓글내용을 입력해주세요" +
                                        "<br>태그할 유저가 없다면 tagLoginId는 보내지 않아도 됩니다.")
    @Parameter(name = "parentReplyId", description = "대댓글을 추가할 부모댓글의 id", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> addChildReply(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("parentReplyId") Long parentReplyId,
            @RequestBody @Valid ChildReplyAddReqDto childReplyAddReqDto){

        replyService.addChildReply(reqLoginId, parentReplyId,childReplyAddReqDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponseDto.of("대댓글등록이 성공했습니다.",null));
    }

    @GetMapping("/snaps/{snapId}/parent-replies/{pageNum}")
    @Operation(summary = "댓글 조회요청", description = "댓글조회할 snapId와 페이지번호를 입력해주세요<br>" +
                                        "댓글을 20개씩 반환합니다.")
    @Parameters({
            @Parameter(name = "pageNum", description = "페이지번호", required = true, example = "1"),
            @Parameter(name = "snapId", description = "조회할 snapId", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto<ParentReplyPagingResDto>> findParentReplyPage(
            final @PathVariable("snapId") Long snapId,
            final @PathVariable("pageNum") Long pageNum){

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponseDto.of("댓글조회 성공했습니다.",
                replyService.findParentReplyPage(snapId,pageNum)));
    }

    @GetMapping("/snaps/parent-replies/{parentReplyId}/child-replies/{pageNum}")
    @Operation(summary = "대댓글 조회요청", description = "대댓글조회할 부모댓글의 Id와 페이지번호를 입력해주세요<br>" +
                                        "대댓글을 20개씩 반환합니다.")
    @Parameters({
            @Parameter(name = "pageNum", description = "페이지번호", required = true, example = "1"),
            @Parameter(name = "parentReplyId", description = "대댓글을 조회할 부모댓글의Id", required = true, example = "1"),
    })
    public ResponseEntity<CommonResponseDto<ChildReplyPagingResDto>> findChildReplyPage(
            final @PathVariable("parentReplyId") Long parentReplyId,
            final @PathVariable("pageNum") Long pageNum){

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponseDto.of(
                "대댓글조회 성공했습니다.", replyService.findChildReplyPage(parentReplyId,pageNum)));
    }

    @PatchMapping("/snaps/parent-replies/{parentReplyId}")
    @Operation(summary = "댓글 수정요청", description = "댓글 ID와 수정할 댓글내용을 입력해주세요")
    @Parameter(name = "parentReplyId", description = "댓글ID", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> updateParentReply(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("parentReplyId") Long parentReplyId,
            @RequestBody @Valid ReplyUpdateReqDto replyUpdateReqDto){

        replyService.updateParentReply(reqLoginId, parentReplyId, replyUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("댓글 수정을 완료했습니다",null));
    }

    @PatchMapping("/snaps/parent-replies/child-replies/{childReplyId}")
    @Operation(summary = "대댓글 수정요청", description = "대댓글 ID와 수정할 댓글내용을 입력해주세요")
    @Parameter(name = "childReplyId", description = "대댓글ID", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> updateChildReply(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable("childReplyId") Long childReplyId,
            final @RequestBody @Valid ReplyUpdateReqDto replyUpdateReqDto){

        replyService.updateChildReply(reqLoginId,childReplyId,replyUpdateReqDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("대댓글 수정을 완료했습니다",null));
    }

    @DeleteMapping("/snaps/parent-replies/{parentReplyId}")
    @Operation(summary = "댓글 삭제요청", description = "삭제할 댓글 ID를 입력해주세요")
    @Parameter(name = "parentReplyId", description = "댓글ID", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> deleteParentReply(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable Long parentReplyId){

        replyService.deleteParentReply(reqLoginId,parentReplyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("댓글 삭제를 완료했습니다",null));
    }

    @DeleteMapping("/snaps/parent-replies/child-replies/{childReplyId}")
    @Operation(summary = "대댓글 삭제요청", description = "삭제할 대댓글 ID를 입력해주세요")
    @Parameter(name = "childReplyId", description = "대댓글ID", required = true, example = "1")
    public ResponseEntity<CommonResponseDto<Void>> deleteChildReply(
            final @AuthenticationPrincipal String reqLoginId,
            final @PathVariable Long childReplyId){

        replyService.deleteChildReply(reqLoginId, childReplyId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponseDto.of("대댓글 삭제를 완료했습니다",null));
    }
}
