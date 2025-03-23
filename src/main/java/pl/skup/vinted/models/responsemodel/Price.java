package pl.skup.vinted.models.responsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Price {
    @JsonProperty("amount")
    @Getter
    private String amount;

    public String getAmount() {
        return amount;
    }
}
