package socialnet.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "info about currency in current city")
public class CurrencyRs {
    @Schema(description = "info about EUR exchange rate in roubles", example = "65.8432")
    private String euro;

    @Schema(description = "info about USD exchange rate in roubles", example = "60.4312")
    private String usd;
}
