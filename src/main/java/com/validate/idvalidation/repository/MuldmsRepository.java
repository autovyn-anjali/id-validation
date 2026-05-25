package com.validate.idvalidation.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MuldmsRepository extends Repository<Object, Long> {

    @Query(value = """
            SELECT BUYING_NUM
            FROM MULDMS.SH_TV_EVL
            WHERE BUYING_NUM IN (:buyingIds)
            """, nativeQuery = true)
    List<String> findExistingBuyingIds(
            @Param("buyingIds") List<String> buyingIds
    );
}