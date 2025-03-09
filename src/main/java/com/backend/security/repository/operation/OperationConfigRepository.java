package com.backend.security.repository.operation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.security.model.operation.OperationConfig;

public interface OperationConfigRepository extends JpaRepository<OperationConfig,Integer> {

    OperationConfig getByName(String name);

    	@Query(
				  value = "SELECT con.* FROM operation_config con WHERE id in (:ids)", 
				  nativeQuery = true)
		
		List<OperationConfig> findByIds(@Param("ids") String ids);


    
}
