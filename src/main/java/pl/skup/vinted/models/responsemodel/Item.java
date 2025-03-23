package pl.skup.vinted.models.responsemodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {

    @JsonProperty("title")
    private String title;

    @JsonProperty("price")
    @Getter
    private String price;

    @JsonProperty("path")
    private String path;

    public String getPrice() {
        return this.price;
    }

    public String getTitle() {
        return title;
    }
}
