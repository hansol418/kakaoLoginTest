package com.busanit501.springproject3.hjt.repository;

import com.busanit501.springproject3.hjt.domain.HjtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HjtRepository extends JpaRepository<HjtEntity, Long>{
}
