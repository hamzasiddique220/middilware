package com.backend.security.repository.operation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.security.model.operation.Operation;

public interface OperationRepository extends JpaRepository<Operation,Integer> {

    
	@Query(value = "SELECT op.* FROM operation op WHERE op.status=0 and op.auto_execution=1 order by id  limit 10", nativeQuery = true)
	List<Operation> getImportantOperationToExecute();

} 
