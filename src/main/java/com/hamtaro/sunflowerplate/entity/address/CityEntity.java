package com.hamtaro.sunflowerplate.entity.address;

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

    @OneToMany(mappedBy = "cityEntity", cascade = CascadeType.ALL)
    private List<DistrictsEntity> districtsEntityList;
}
