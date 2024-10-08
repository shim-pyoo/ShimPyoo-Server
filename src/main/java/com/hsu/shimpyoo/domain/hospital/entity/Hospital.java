package com.hsu.shimpyoo.domain.hospital.entity;

import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalSearchResponseDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HOSPITAL")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hospital_id")
    private Long hospitalId; // 병원 기본키

    @Column(name="hospital_name")
    private String hospitalName; // 병원 이름

    @Column(name="hospital_phone")
    private String hospitalPhone; // 병원 연락처

    @Column(name="hospital_address")
    private String hospitalAddress; // 병원 주소

    public static Hospital toEntity(HospitalSearchResponseDto hospitalSearchResponseDto) {
        Hospital hospitals = Hospital.builder()
                .hospitalName(hospitalSearchResponseDto.getHospitalName())
                .hospitalAddress(hospitalSearchResponseDto.getHospitalAddress())
                .hospitalPhone(hospitalSearchResponseDto.getHospitalPhone())
                .build();

        return hospitals;

    }
}
