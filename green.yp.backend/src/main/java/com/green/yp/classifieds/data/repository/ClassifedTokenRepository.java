package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClassifedTokenRepository extends JpaRepository<ClassifiedToken, UUID> {}
