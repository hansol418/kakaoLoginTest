package com.busanit501.springproject3.hjt.service;

import com.busanit501.springproject3.hjt.domain.HjtEntity;
import com.busanit501.springproject3.hjt.repository.HjtRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class HjtService {

    @Autowired
    private HjtRepository hjtRepository;

    public List<HjtEntity> findAll() {
        return hjtRepository.findAll();
    }

    public Optional<HjtEntity> findById(Long id) {
        return hjtRepository.findById(id);
    }


}

