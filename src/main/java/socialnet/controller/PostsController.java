package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.PostRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.PostRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.model.SearchOptions;
import socialnet.service.FindService;
import socialnet.service.PostService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "posts-controller", description = "Get feeds. Get, update, delete, recover, find post, get users post," +
        " create post")
public class PostsController {
    private final PostService postsService;
    private final FindService findService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/feeds")
    @Operation(summary = "get all news", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListPostRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PostRs>> getFeeds(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestParam(required = false, defaultValue = "0") @Parameter(description =  "offset", example = "0")
            Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter(description =  "perPage", example = "20")
            Integer perPage) {
        return postsService.getFeeds(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/users/{id}/wall")
    @Operation(summary = "get all post by author id", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PostRs>> getWall(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable(name = "id") @Parameter(description =  "id", example = "1") Long id,
            @RequestParam(required = false, defaultValue = "0") @Parameter(description =  "offset", example = "0")
            Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter(description =  "perPage", example = "20")
            Integer perPage) {
        return postsService.getFeedsByAuthorId(id, authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/api/v1/users/{id}/wall")
    @Operation(summary = "create new post", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PostRs> createPost(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestBody @Parameter(description =  "postRq", example = "postRq") PostRq postRq,
            @RequestParam(required = false, name = "publish_date")
            @Parameter(description =  "publishDate", example = "publishDate") Long publishDate,
            @PathVariable @Parameter(description =  "id", example = "1") int id) {
        return postsService.createPost(postRq, id, publishDate, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post/{id}")
    @Operation(summary = "get post by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PostRs> getPostById(@RequestHeader
                                        @Parameter(description = "Access Token", example = "JWT Token")
                                        String authorization,
                                        @PathVariable @Parameter(description =  "id", example = "1") int id) {
        return postsService.getPostById(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}")
    @Operation(summary = "create new post", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsPostRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PostRs> updateById(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable @Parameter(description =  "id", example = "1") int id,
            @RequestBody @Parameter(description =  "postRq", example = "postRq") PostRq postRq) {

        return postsService.updatePost(id, postRq, authorization);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/post/{id}")
    @Operation(summary = "delete post by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsPostRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PostRs> deleteById(@RequestHeader @Parameter(description = "Access Token", example = "JWT Token")
                                       String authorization,
                                       @PathVariable @Parameter(description =  "id", example = "1") int id) {
        return postsService.markAsDelete(id, authorization);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/post/{id}/recover")
    @Operation(summary = "recover post by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsPostRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PostRs> recoverPostById(@RequestHeader
                                            @Parameter(description = "Access Token", example = "JWT Token")
                                            String authorization,
                                            @PathVariable @Parameter(description =  "id", example = "1") int id) {
        return postsService.recoverPost(id, authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/post")
    @Operation(summary = "get posts by query", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListPostRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PostRs>> getPostsByQuery(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestParam(required = false, defaultValue = "") @Parameter(description =  "possible post author",
                    example = "Максим Иванов") String author,
            @RequestParam(required = false, name = "date_from", defaultValue = "0")
            @Parameter(description =  "post can be written after this date", example = "111111111") Long dateFrom,
            @RequestParam(required = false, name = "date_to", defaultValue = "0")
            @Parameter(description =  "post can be written before this date", example = "111111111") Long dateTo,
            @RequestParam(required = false, defaultValue = "0")
            @Parameter(description =  "offset", example = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20")
            @Parameter(description =  "perPage", example = "20") Integer perPage,
            @RequestParam(required = false) @Parameter(description =  "post tags") String[] tags,
            @RequestParam(required = false, defaultValue = "") @Parameter(description =  "text that can contain a post",
                    example = "Hogwarts") String text) {

        return findService.getPostsByQuery(SearchOptions.builder()
                .jwtToken(authorization)
                .author(author)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .tags(tags)
                .text(text)
                .offset(offset)
                .perPage(perPage)
                .build());
    }
}
