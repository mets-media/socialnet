package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.PersonRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.FriendsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "friends-controller", description = "Get recommended or potential friends. Add, delete, get friends." +
        " Send, delete friendship request")
public class FriendsController {
    private final FriendsService friendsService;

    @OnlineStatusUpdatable
    @GetMapping("/friends")
    @Operation(summary = "get friends of current user"
            , responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PersonRs>> getFriends(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestParam(required = false, defaultValue = "0") @Parameter(description = "offset", example = "0")
            Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter(description = "perPage", example = "20")
            Integer perPage) {
        return friendsService.getFriends(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/block_unblock/{id}")
    @Operation(summary = "block or unblock (if user in block) user by user", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(implementation = HttpStatus.class))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public HttpStatus userBlocks(@RequestHeader @Parameter(description = "Access Token", example = "JWT Token")
                                 String authorization,
                                 @PathVariable(value = "id") @Parameter(description = "id", example = "1") Integer id) {
        return friendsService.userBlocks(authorization, id);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/outgoing_requests")
    @Operation(summary = "get outgoing requests by user", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PersonRs>> getOutgoingRequests(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestParam(required = false, defaultValue = "0")  @Parameter(description = "offset", example = "0")
            Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter(description = "perPage", example = "20")
            Integer perPage) {
        return friendsService.getOutgoingRequests(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/recommendations")
    @Operation(summary = "get recommendation friends", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PersonRs>> getRecommendedFriends(@RequestHeader
                                                          @Parameter(description = "Access Token", example = "JWT Token")
                                                          String authorization) {
        return friendsService.getRecommendedFriends(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/friends/request")
    @Operation(summary = "get potential friends of current user", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PersonRs>> getPotentialFriends(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @RequestParam(required = false, defaultValue = "0")  @Parameter(description = "offset", example = "0")
            Integer offset,
            @RequestParam(required = false, defaultValue = "20") @Parameter(description = "perPage", example = "20")
            Integer perPage) {
        return friendsService.getPotentialFriends(authorization, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/request/{id}")
    @Operation(summary = "add friend by id", responses = {@ApiResponse(responseCode = "200",
            description = "OK", content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> addFriend(@RequestHeader @Parameter(description = "Access Token", example = "JWT Token")
                                         String authorization,
                                         @PathVariable(value = "id") @Parameter(description = "id", example = "1")
                                         Integer id) {
        return friendsService.addFriend(authorization, id);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/friends/request/{id}")
    @Operation(summary = "decline friendship request by id", responses = {@ApiResponse(responseCode = "200",
            description = "OK", content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> deleteFriendsRequest(@RequestHeader
                                                    @Parameter(description = "Access Token", example = "JWT Token")
                                                    String authorization,
                                                    @PathVariable(value = "id")
                                                    @Parameter(description = "id", example = "1") Integer id) {
        return friendsService.deleteFriend(authorization, id);
    }

    @OnlineStatusUpdatable
    @PostMapping("/friends/{id}")
    @Operation(summary = "send friendship request by id of another user", responses = {@ApiResponse(responseCode = "200",
            description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> sendFriendsRequest(@RequestHeader
                                                  @Parameter(description = "Access Token", example = "JWT Token")
                                                  String authorization,
                                                  @PathVariable(value = "id")
                                                  @Parameter(description = "id", example = "1") Integer id) {
        return friendsService.sendFriendsRequest(authorization, id);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/friends/{id}")
    @Operation(summary = "delete friend by id", responses = {@ApiResponse(responseCode = "200",
            description = "OK", content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> deleteFriend(@RequestHeader
                                            @Parameter(description = "Access Token", example = "JWT Token")
                                            String authorization,
                                            @PathVariable(value = "id") @Parameter(description = "id", example = "1")
                                            Integer id) {
        return friendsService.deleteFriend(authorization, id);
    }
}
