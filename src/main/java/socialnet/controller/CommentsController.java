package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.CommentRq;
import socialnet.api.response.CommentRs;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ErrorRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "comments-controller", description = "Create, delete, read, edit and recover comments")
public class CommentsController {
    private final CommentService commentService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post/{postId}/comments")
    @Operation(summary = "get comment by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<CommentRs>> getComments(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable @Parameter(description = "postId", example = "1") Long postId,
            @RequestParam(required = false, defaultValue = "0") @Parameter(description = "offset", example = "0")
            Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter(description = "perPage", example = "20")
            Integer perPage) {
        return commentService.getComments(postId, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/api/v1/post/{postId}/comments")
    @Operation(summary = "create comment", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsCommentRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<CommentRs> createComment(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable @Parameter(description = "postId", example = "1") Long postId,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "commentRq") CommentRq commentRq) {
        return commentService.createComment(commentRq, postId, authorization);
    }


    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/comments/{comment_id}")
    @Operation(summary = "edit comment by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<CommentRs> editComment(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable @Parameter(description = "id", example = "1") Long id,
            @PathVariable(name = "comment_id") @Parameter(description = "commentId", example = "1") Long commentId,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "commentRq") CommentRq commentRq) {
        return commentService.editComment(authorization, id, commentId, commentRq);
    }


    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/post/{id}/comments/{comment_id}")
    @Operation(summary = "delete comment by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsCommentRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<CommentRs> deleteComment(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable @Parameter(description = "id", example = "1") Long id,
            @PathVariable(name = "comment_id") @Parameter(description = "commentId", example = "1") Long commentId) {
        return commentService.deleteComment(id, commentId);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/comments/{comment_id}/recover")
    @Operation(summary = "recover comment by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsCommentRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<CommentRs> recoverComment(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable @Parameter(description = "id", example = "1") Long id,
            @PathVariable(name = "comment_id") @Parameter(description = "commentId", example = "1") Long commentId) {
        return commentService.recoverComment(id, commentId);
    }
}
