package com.jjld.domain.complex.repository;

import com.jjld.domain.complex.entity.Complex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplexRepository extends JpaRepository<Complex, Long> {
}
