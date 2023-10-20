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
@Table(name = "city")
public class CityEntity {

    @Id @GeneratedValue
    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "city_name")
    private String cityName;

    @OneToMany(mappedBy = "districts_id", cascade = CascadeType.ALL)
    private List<DistrictsEntity> districtsEntityList;
}
