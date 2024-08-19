package com.hsu.shimpyoo.domain.hospital.web.controller;

import com.hsu.shimpyoo.domain.hospital.service.HospitalService;
import com.hsu.shimpyoo.domain.hospital.service.HospitalServiceImpl;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalRequestDto;
import com.hsu.shimpyoo.domain.hospital.web.dto.HospitalVisitRequestDto;
import com.hsu.shimpyoo.global.response.CustomAPIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;
    private static final Logger log = LoggerFactory.getLogger(HospitalServiceImpl.class);

    @PostMapping("/searchHospital")
    public ResponseEntity<CustomAPIResponse<?>> searchHospitals(@Valid @RequestBody HospitalRequestDto hospitalRequestDto){
       ResponseEntity<CustomAPIResponse<?>> result= hospitalService.searchHospital(hospitalRequestDto);
       return result;
    }

    @PostMapping("/setVisitHospital")
    public ResponseEntity<CustomAPIResponse<?>> setVisitHospital(@Valid @RequestBody HospitalVisitRequestDto hospitalVisitRequestDto){
        log.info("Received request: hospitalId={}, reservationDateTime={}",
                hospitalVisitRequestDto.getHospitalId(),
                hospitalVisitRequestDto.getReservationDateTime());
        ResponseEntity<CustomAPIResponse<?>> result=hospitalService.setVisitHospital(hospitalVisitRequestDto);
        return result;
    }
}
