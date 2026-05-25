package com.validate.idvalidation.repository;

import com.validate.idvalidation.entity.ShTvEvl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MuldmsRepository
        extends JpaRepository<ShTvEvl, String> {

    @Query(value = """
            SELECT "BUYING_NUM"
            FROM "MULDMS"."SH_TV_EVAL"
            WHERE "BUYING_NUM" IN (:buyingIds)
            """, nativeQuery = true)
    List<String> findExistingBuyingIds(
            @Param("buyingIds") List<String> buyingIds
    );
}