package com.hamtaro.sunflowerplat.entity.address;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "districts")
public class DistrictsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "districts_id")
    private Long districtsId;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "districts_name")
    private String districtsName;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity cityEntity;

    @OneToMany(mappedBy = "dong_id" , cascade = CascadeType.ALL)
    private List<DongEntity> dongEntityList;
}
