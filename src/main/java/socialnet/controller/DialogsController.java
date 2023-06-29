package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.DialogUserShortListDto;
import socialnet.api.response.*;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.DialogsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "dialogs-controller", description = "Get dialogs, start dialog, get read and unread messages")
public class DialogsController {

    private final DialogsService dialogsService;

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "recover comment by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<DialogRs>> getDialogs(@RequestHeader
                                               @Parameter(description = "Access Token", example = "JWT Token")
                                               String authorization) {
        return dialogsService.getDialogs(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs/unreaded", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "get count of unread messages", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> getUnreadedMessages(@RequestHeader
                                                   @Parameter(description = "Access Token", example = "JWT Token")
                                                   String authorization) {
        return dialogsService.getUnreadedMessages(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping(value = "/dialogs/{dialogId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "get messages from dialog", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<MessageRs>> getMessagesFromDialog(
            @RequestHeader @Parameter(description = "Access Token", example = "JWT Token") String authorization,
            @PathVariable("dialogId") @Parameter(description = "dialogId", example = "1") Long dialogId,
            @RequestParam(defaultValue = "0") @Parameter(description = "offset", example = "0") Integer offset,
            @RequestParam(defaultValue = "20") @Parameter(description = "perPage", example = "20") Integer perPage) {
        return dialogsService.getMessagesFromDialog(authorization, dialogId, offset, perPage);
    }

    @OnlineStatusUpdatable
    @PutMapping(value = "/dialogs/{dialogId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "read messages in dialog", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> readMessagesInDialog(@RequestHeader
                                                    @Parameter(description = "Access Token", example = "JWT Token")
                                                    String authorization,
                                                    @PathVariable("dialogId")
                                                    @Parameter(description = "dialogId", example = "1") Long dialogId) {
        return dialogsService.readMessagesInDialog(dialogId);
    }

    @OnlineStatusUpdatable
    @PostMapping(value = "/dialogs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "start dialog with user", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> startDialog(@RequestHeader @Parameter(description = "Access Token", example = "JWT Token")
                                           String authorization,
                                           @RequestBody
                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                           description = "dialogUserShortListDto")
                                           DialogUserShortListDto dialogUserShortListDto) {
        return dialogsService.registerDialog(authorization, dialogUserShortListDto);
    }
}