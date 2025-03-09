package com.backend.security.model.operation;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.security.model.step.Step;

import lombok.Data;

@Data // Combines @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor
@Entity
@Table(name = "operation")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for portability
    private int id; // Use Long for ID
    @NotNull(message = "Please provide operation name")
    @Column(name = "name", nullable = false)
    private String name;
    private String type;
    private String region;
    private String userName;
    private String userId;
    private int contractId;
    private int productId;
    private int status=0;
    private int childOperationId=0;
	private int autoExecution=0;
	private int approvalSteps=0;
	private int parentOperationId=0;
	private int multiOpStatus=0;
    private int isPooling=0;
    private int preserveData=0;
    private int isRollback=0;
    private int longOperation=0;
	private String rollbackOperationName;
    private int providerId;



    @Lob
    private String parameters;
    @Lob
    private String otherParameters;
    @Lob
    private String result;
    private int priority = 0; // Default value set in Java
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(targetEntity = Step.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Use LAZY fetching for better performance
    @JoinColumn(name = "op_id", referencedColumnName = "id")
    private List<Step> steps;



    // Constructor, getters, and setters are handled by @Data (Lombok)
}
