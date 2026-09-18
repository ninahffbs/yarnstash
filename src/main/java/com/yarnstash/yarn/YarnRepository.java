package com.yarnstash.yarn;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface YarnRepository extends JpaRepository<Yarn, Long> {
    @Query("""
              select y from Yarn y
              where (:weight is null or y.weight = :weight)
                and (cast(:fiber as string) is null
                     or lower(y.fiber) like lower(concat('%', cast(:fiber as string), '%')))
              """)
    Page<Yarn> search(@Param("weight") YarnWeight weight,
                      @Param("fiber") String fiber,
                      Pageable pageable);
}
