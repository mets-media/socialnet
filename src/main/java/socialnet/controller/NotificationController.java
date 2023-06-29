package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.NotificationRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.NotificationsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "notification-controller", description = "Get, read notifications")
public class NotificationController {

    private final NotificationsService notificationsService;

    @OnlineStatusUpdatable
    @GetMapping("/notifications")
    @Operation(summary = "get all notifications for user", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<NotificationRs>> notifications(
        @RequestHeader @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
        @RequestParam(required = false, defaultValue = "10")
        @Parameter(description =  "itemPerPage", example = "10") Integer itemPerPage,
        @RequestParam(required = false, defaultValue = "0")
        @Parameter(description =  "offset", example = "0") Integer offset)
    {
        return notificationsService.getAllNotifications(itemPerPage, authorization, offset);
    }

    @OnlineStatusUpdatable
    @PutMapping("/notifications")
    @Operation(summary = "read notification", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListNotificationRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<NotificationRs>> notifications(
        @RequestHeader @Parameter(description =  "Access Token", example = "JWT Token") String authorization,
        @RequestParam(required = false) @Parameter(description =  "id", example = "1") Integer id,
        @RequestParam(required = false) @Parameter(description =  "all", example = "false") Boolean all)
    {
        return notificationsService.putNotifications(all, id, authorization);
    }
}
