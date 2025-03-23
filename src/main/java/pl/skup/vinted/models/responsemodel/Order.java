package pl.skup.vinted.models.responsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @JsonProperty("title")
    private String title;

    @JsonProperty("price")
    @Getter
    private Price price;

    @JsonProperty("date")
    private String date;


    public Price getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }
}
