package pl.skup.vinted.models.endpointContraints;

public enum OrdersSort {
    PRICE_DESC("price_desc"),
    PRICE_ASC("price_asc"),
    DATE_DESC("date_desc"),
    DATE_ASC("date_asc");

    public String value;

    OrdersSort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
