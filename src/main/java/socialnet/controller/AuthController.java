package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.LoginRq;
import socialnet.api.response.*;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.service.CaptchaService;
import socialnet.service.PersonService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "auth-controller", description = "Working with captcha, login and logout")
public class AuthController {
    private final CaptchaService captchaService;
    private final PersonService personService;

    @GetMapping("/captcha")
    @Operation(summary = "get captcha secret code and image url", responses = {@ApiResponse(responseCode = "200",
            description = "OK", content = {@Content(schema = @Schema(implementation = CaptchaRs.class))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CaptchaRs captcha() {
        return captchaService.getCaptchaData();
    }

    @PostMapping("/login")
    @Operation(summary = "login by email and password", responses = {@ApiResponse(responseCode = "200",
            description = "OK", content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PersonRs> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "loginRq") @RequestBody LoginRq loginRq) {
        return personService.getLogin(loginRq);
    }

    @OnlineStatusUpdatable
    @PostMapping("/logout")
    @Operation(summary = "logout current user", responses = {@ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> logout(@RequestHeader(name = "authorization")
                                      @Parameter(description = "Access Token", example = "JWT Token")
                                      String authorization) {
        return personService.getLogout();
    }
}
