package cn.yznu.vms.interaction.controller;

import cn.yznu.vms.common.exception.BusinessException;
import cn.yznu.vms.common.result.PageResult;
import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.common.result.ResultCode;
import cn.yznu.vms.interaction.dto.CommentAddDTO;
import cn.yznu.vms.interaction.entity.Comment;
import cn.yznu.vms.interaction.service.CommentService;
import cn.yznu.vms.interaction.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 评论控制器
 */
@Tag(name = "评论管理", description = "评论的增删查")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "获取视频评论列表")
    @GetMapping("/video/{videoId}")
    public Result<PageResult<CommentVO>> listByVideo(
            @PathVariable Long videoId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(PageResult.of(commentService.listByVideoId(videoId, pageNo, pageSize)));
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public Result<Long> add(
            @Valid @RequestBody CommentAddDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Long id = commentService.addComment(dto.getVideoId(), userId, dto.getContent(),
                dto.getParentId(), dto.getReplyUserId());
        return Result.success(id);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        Comment comment = commentService.getById(id);
        if (comment == null) {
            throw new BusinessException(ResultCode.COMMENT_NOT_FOUND);
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此评论");
        }
        commentService.removeById(id);
        return Result.success();
    }
}
