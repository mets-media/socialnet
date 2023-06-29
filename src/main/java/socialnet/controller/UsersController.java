package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import socialnet.api.request.UserRq;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ComplexRs;
import socialnet.api.response.ErrorRs;
import socialnet.api.response.PersonRs;
import socialnet.aspects.OnlineStatusUpdatable;
import socialnet.model.SearchOptions;
import socialnet.service.FindService;
import socialnet.service.PersonService;


import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "users-controller", description = "Get user. Get, update, delete, recover personal info. User search")
public class UsersController {

    private final PersonService personService;
    private final FindService findService;

    @OnlineStatusUpdatable
    @GetMapping("/me")
    @Operation(summary = "get information about me", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PersonRs> getMyProfile(@RequestHeader(name = "authorization")
                                               @Parameter(description =  "Access Token", example = "JWT Token")
                                               String authorization) {
        return personService.getMyProfile(authorization);
    }

    @OnlineStatusUpdatable
    @GetMapping("/{id}")
    @Operation(summary = "get user by id", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<PersonRs> getUserById(@RequestHeader(name = "authorization")
                                              @Parameter(description =  "Access Token", example = "JWT Token")
                                              String authorization,
                                          @PathVariable(name = "id")
                                          @Parameter(description =  "id", example = "1")Integer id) {
        return personService.getUserById(authorization, id);
    }

    @OnlineStatusUpdatable
    @GetMapping("/search")
    @Operation(summary = "search post by query", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListPersonRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<List<PersonRs>> findPersons(@RequestHeader
                                                @Parameter(description =  "Access Token", example = "JWT Token")
                                                String authorization,
                                                @RequestParam(name = "age_from", required = false, defaultValue = "0")
                                                @Parameter(description =  "after this age", example = "5")
                                                Integer ageFrom,
                                                @RequestParam(name = "age_to", required = false, defaultValue = "0")
                                                @Parameter(description =  "before this age", example = "50")
                                                Integer ageTo,
                                                @RequestParam(required = false, defaultValue = "")
                                                @Parameter(description =  "city name", example = "Paris")
                                                String city,
                                                @RequestParam(required = false, defaultValue = "")
                                                @Parameter(description =  "country name", example = "France")
                                                String country,
                                                @RequestParam(name = "first_name", required = false, defaultValue = "")
                                                @Parameter(description =  "possible first name", example = "Максим")
                                                String firstName,
                                                @RequestParam(name = "last_name", required = false, defaultValue = "")
                                                @Parameter(description =  "possible last name", example = "Иванов")
                                                String lastName,
                                                @RequestParam(required = false, defaultValue = "0")
                                                @Parameter(description =  "offset", example = "0")
                                                Integer offset,
                                                @RequestParam(required = false, defaultValue = "20")
                                                @Parameter(description =  "perPage", example = "20")
                                                Integer perPage) {

        return findService.findPersons(SearchOptions.builder()
                .jwtToken(authorization)
                .ageFrom(ageFrom)
                .ageTo(ageTo)
                .city(city)
                .country(country)
                .firstName(firstName)
                .lastName(lastName)
                .offset(offset)
                .perPage(perPage)
                .build());
    }

    @OnlineStatusUpdatable
    @PutMapping("/me")
    @Operation(summary = "update information about me", responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public ResponseEntity<CommonRs<PersonRs>> updateUserInfo(@RequestHeader("authorization")
                                                                 @Parameter(description =  "Access Token", example = "JWT Token")
                                                                 String authorization,
                                            @RequestBody @Parameter(description =  "userData", example = "userData")
                                            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "userData")
                                            UserRq userData) {
        return personService.updateUserInfo(authorization, userData);
    }

    @OnlineStatusUpdatable
    @DeleteMapping("/me")
    @Operation(summary = "delete information about me", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> deleteUser(@RequestHeader("authorization")
                                              @Parameter(description =  "Access Token", example = "JWT Token")
                                              String authorization) {
        return personService.delete(authorization);
    }

    @OnlineStatusUpdatable
    @PostMapping("/me/recover")
    @Operation(summary = "recover information about me", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsComplexRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {@Content(schema = @Schema())})})
    public CommonRs<ComplexRs> recoverUser(@RequestHeader("authorization")
                                               @Parameter(description =  "Access Token", example = "JWT Token")
                                               String authorization) {
        return personService.recover(authorization);
    }

}