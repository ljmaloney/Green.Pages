package com.green.yp.classifieds.data.repository;

import com.green.yp.classifieds.data.model.ClassifiedCategory;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassifiedCategoryRepository extends JpaRepository<ClassifiedCategory, UUID> {
    Optional<ClassifiedCategory> findByUrlName(String categoryName);
}
