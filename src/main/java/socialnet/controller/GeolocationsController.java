package socialnet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import socialnet.api.response.CommonRs;
import socialnet.api.response.ErrorRs;
import socialnet.service.GeolocationsService;
import socialnet.api.response.GeolocationRs;

import java.util.List;

@RestController
@RequestMapping("/api/v1/geolocations")
@RequiredArgsConstructor
@Tag(name = "geolocations-controller", description = "Create or get records about cities and countries")
public class GeolocationsController {
    private final GeolocationsService geolocationsService;
    @GetMapping("cities/api")
    @Operation(summary = "get cities from api", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListGeolocationRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))})})
    public CommonRs<List<GeolocationRs>> getCitiesFromApiStartsWith(@RequestParam("country")
                                                                    @Parameter(description = "country", example = "country")
                                                                    String country,
                                                                    @RequestParam("starts")
                                                                    @Parameter(description = "starts", example = "starts")
                                                                    String starts) {
        return new CommonRs<>(geolocationsService.getCitiesByCountryAndStarts(country, starts));
    }
    @GetMapping("cities/db")
    @Operation(summary = "get cities from database", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListGeolocationRs"))}),
            @ApiResponse(responseCode = "400", description = "Name of error",
                    content = {@Content(schema = @Schema(implementation = ErrorRs.class))})})
    public CommonRs<List<GeolocationRs>> getCitiesByStarts(@RequestParam("country")
                                                           @Parameter(description = "country", example = "country")
                                                           String country,
                                                           @RequestParam("starts")
                                                           @Parameter(description = "starts", example = "starts")
                                                           String starts) {
        return new CommonRs<>(geolocationsService.getCitiesByCountryAndStarts(country, starts));
    }
    @GetMapping("cities/uses")
    @Operation(summary = "get cities from persons", responses = {@ApiResponse(responseCode = "200", description = "OK"),
    @ApiResponse(responseCode = "400", description = "Name of error",
            content = {@Content(schema = @Schema(implementation = ErrorRs.class))})})
    public CommonRs<List<GeolocationRs>> getCitiesByCountry(@RequestParam("country")
                                                           @Parameter(description = "country", example = "country")
                                                           String country) {
        return new CommonRs<>(geolocationsService.getCitiesByCountry(country));
    }
    @GetMapping("countries")
    @Operation(summary = "get countries", responses = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(schema = @Schema(ref = "#/components/schemas/CommonRsListGeolocationRs"))})})
    public CommonRs<List<GeolocationRs>> getCountries() {
        return new CommonRs<>(geolocationsService.findAllCountry());
    }

}
