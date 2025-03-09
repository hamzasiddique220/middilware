package com.backend.security.model.step;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.backend.security.model.operation.Operation;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "step")
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for consistency
    private Long id; // Use Long for ID
    private String name;
    private String url;
    private String callType;
    private String token;
    private String callDataType;
    private String ansibleType;
    private int status=0;
    @Lob
    private String inputParameters;
    @Lob
    private String stepParameters;
    @Lob
    private String requestParameters;
    @Lob
    private String headerParameters;
    @Lob
    private String result;
    private int stepId=0;
    private int priority=0;
    private int optional = 0;
    private int providerId = 0;
    @Lob
    private String defaultParameters;
    private int dependOn=0;
    private int isRollback = 0; // Correct the naming convention
    private int rollbackDependOn = 0;
    private int rollbackStepId = 0;
    private String rollbackStepName;
    private String rollbackUrl; // Use camel case for consistency
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY) // Use LAZY fetching for better performance
    @JoinColumn(name = "op_id", nullable = false)
    private Operation operation;

    // Lombok @Data generates getters, setters, toString, equals, and hashCode methods.
}
