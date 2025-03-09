package com.backend.security.repository.step;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.security.model.step.Step;

import jakarta.transaction.Transactional;

public interface StepRepository extends JpaRepository<Step, Long> {

    @Query(value = "SELECT st.* FROM step st WHERE st.step_id=:stepId and st.op_id=:operationId order by step_id limit 1 ", nativeQuery = true)
    Step findByStepAndOpertaionId(@Param("stepId") int stepId, @Param("operationId") int operationId);

    @Query(value = "SELECT\n" +
            "st.*\n" +
            "FROM\n" +
            "step st\n" +
            "WHERE\n" +
            "CASE\n" +
            "WHEN st.call_type =\"get\" THEN st.status in (0,1) AND st.op_id =:opId\n" +
            "ELSE st.status =0 AND st.op_id =:opId\n" +
            "END\n" +
            "order by\n" +
            "st.step_id ,\n" +
            "st.priority\n" +
            "limit 1\n", nativeQuery = true)
    Step getStepToExecute(@Param("opId") int opId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE step SET STATUS = 3 WHERE op_id =:opId", nativeQuery = true)
    void setAllStepToFail(@Param("opId") int opId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE step SET STATUS = 3 WHERE id =:id", nativeQuery = true)
    void setStepToFail(@Param("id") int id);


}
