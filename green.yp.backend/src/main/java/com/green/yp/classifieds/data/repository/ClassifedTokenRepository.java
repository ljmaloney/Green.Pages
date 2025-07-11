package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedToken;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassifedTokenRepository extends JpaRepository<ClassifiedToken, UUID> {}
