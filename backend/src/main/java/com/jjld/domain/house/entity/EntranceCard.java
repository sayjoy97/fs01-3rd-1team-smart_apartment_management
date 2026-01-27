package com.jjld.domain.house.entity;


import com.jjld.domain.house.entity.Enum.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entrance_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntranceCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(nullable = false, unique = true)
    private String cardUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

}
