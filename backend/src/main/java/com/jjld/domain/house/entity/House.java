package com.jjld.domain.house.entity;

import com.jjld.domain.cargate.entity.RegisteredCar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "house",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"house_dong", "house_ho"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long houseId;

    @Column(nullable = false)
    private Integer houseDong;

    @Column(nullable = false)
    private Integer houseHo;

    @Column(nullable = false)
    private Boolean houseStatus;

    private String householderName;
    private String householderPhone;
    private String householderEmail;

    @UpdateTimestamp
    @Column(nullable = true, columnDefinition = "DATETIME")
    private LocalDate moveInAt;

    @Column(nullable = false)
    private String entrancePass;

    private String appLoginPass;

    private int householdSize;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<EntranceCard> cardList = new ArrayList<>();

    @OneToMany( mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegisteredCar> registeredCars = new ArrayList<>();

}
