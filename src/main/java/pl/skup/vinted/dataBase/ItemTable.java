package pl.skup.vinted.dataBase;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@Table(name = "buty")
public class ItemTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tytul")
    private String title;

    @Column(name = "na_stanie")
    private String stock;

    @Column(name = "klasa")
    private String type;

    @Column(name = "price")
    private String price;

    public ItemTable(String title, String stock, String type, String price) {
        this.title = title;
        this.stock = stock;
        this.type = type;
        this.price = price;
    }


    public ItemTable() {

    }

    public String getTitle() {
        return title;
    }
}
