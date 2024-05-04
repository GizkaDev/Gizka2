package ru.gizka.api.model.item;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @Size(min = 1, max = 200)
    @NotBlank
    private String name;

    @Column(name = "weight")
    @PositiveOrZero
    private Long weight;

    @Column(name = "item_value")
    @Positive
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "item_product", referencedColumnName = "name")
    private Product product;
}
