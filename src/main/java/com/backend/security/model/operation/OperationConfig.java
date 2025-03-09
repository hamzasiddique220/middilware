package com.backend.security.model.operation;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.security.model.step.StepConfig;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "operation_config")
public class OperationConfig {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private int id;
	private String name;
	private String displayName;
	private String type;
	private String beforeStart;
	private String afterCompletion;
	private int status;
	private String steps;
	private int stepsFailAction=0;
	private int providerId=0;
	private int preserveData=0;
	private int longOperation=0;
	private int isPooling=1;
	private int permissionId=1;
	private int isRollback=0;
	private String rollbackOperationName;
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	private String createdBy;
	private int childOperationId=0;
	private int autoExecution=1;
	private int approvalSteps=0;
	private int parentOperationId=0;
	private int multiOpStatus=0;

	@OneToMany(targetEntity = StepConfig.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "con_op_id", referencedColumnName = "id")
	private List<StepConfig> step;

	public void setStep(List<StepConfig> step) {
		this.step = step;
	}

	public List<StepConfig> getStep() {
		return step;
	}



}
