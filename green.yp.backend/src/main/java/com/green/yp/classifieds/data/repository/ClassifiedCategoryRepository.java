package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClassifiedCategoryRepository extends JpaRepository<ClassifiedCategory, UUID> {}
