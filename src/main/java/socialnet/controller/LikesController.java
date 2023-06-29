package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LikeRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.LikeRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.LikesService;

@RestController
@RequiredArgsConstructor
@Tag(name = "likes-controller", description = "Get likes, delete and put like")
public class LikesController {

    private final LikesService likesService;

    @OnlineStatusUpdatable
    @GetMapping("/api/v1/likes")
    @Operation(summary = "get all my likes by comment or post", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<LikeRs> getLikes(
            @RequestHeader  @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
            @RequestParam(name = "item_id") @Parameter(description = "itemId", example = "1") Integer itemId,
            @RequestParam @Parameter(description = "type", example = "type") String type) {

        return likesService.getLikes(itemId, type);
    }

    @OnlineStatusUpdatable
    @PutMapping("/api/v1/likes")
    @Operation(summary = "put like on post or comment", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsLikeRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<LikeRs> putLike(
            @RequestHeader  @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
            @RequestBody @Parameter(description = "data for put or delete like", example = "likeRq")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "likeRq") LikeRq likeRq) {

        return likesService.putLike(authorization, likeRq);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/api/v1/likes")
    @Operation(summary = "delete like from post or comment", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsLikeRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<LikeRs> deleteLike(
            @RequestHeader  @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
            @RequestParam(name = "item_id") @Parameter(description = "itemId", example = "1") Integer itemId,
            @RequestParam @Parameter(description = "type", example = "type") String type) {

        return likesService.deleteLike(authorization, itemId, type);
    }
}
