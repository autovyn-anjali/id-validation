package com.validate.idvalidation.repository;

import com.validate.idvalidation.entity.BuyingMasterTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuyingMasterTestRepository
        extends JpaRepository<BuyingMasterTest, String> {

    List<BuyingMasterTest> findByBuyingIdIn(List<String> buyingIds);
}