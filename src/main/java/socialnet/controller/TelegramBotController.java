package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.TgApiRequest;
import socialnet.api.response.TgApiRs;
import socialnet.service.TelegramBotService;

@RestController
@RequestMapping("/api/v1/tg")
@RequiredArgsConstructor
@Tag(name = "telegram-bot-controller", description = "Telegram connection")
public class TelegramBotController {
    private final TelegramBotService telegramBotService;

    @PostMapping
    @Operation(summary = "telegramBot execute command")
    public ResponseEntity<TgApiRs> execCommand(@RequestBody @Parameter(description = "request", example = "request")
                                                   TgApiRequest request) {
        return ResponseEntity.ok(telegramBotService.execCommand(request));
    }

    @GetMapping
    @Operation(summary = "telegramBot register user")
    public ResponseEntity<TgApiRs> register(@RequestParam @Parameter(description = "id", example = "1") long id,
                                            @RequestParam
                                            @Parameter(description = "email", example = "fullName@gmail.com")
                                            String email,
                                            @RequestParam
                                            @Parameter(description = "cmd", example = "cmd")
                                            String cmd) {
        return ResponseEntity.ok(telegramBotService.register(id, email, cmd));
    }
}
