package com.backend.security.model.step;
import org.hibernate.annotations.GenericGenerator;
import com.backend.security.model.operation.OperationConfig;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "step_config")
public class StepConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private int id;
    private String name;
    private String url;
    private String urlType;
    private String callType;
    private String callDataType;
    @Lob
    private String inputParameters;
    @Lob
    private String requestParameters;
    private int stepId;
    private int status;
    private String ansibleType;
    private String result_parameters;
    private String rollbackURL;
    private int priority;
    private int optional = 0;
    private int providerId = 0;
    private int is_rollback = 0;
    private int rollback_depend_on = 0;
    private int rollback_step_id = 0;
    private String rollback_step_name;
    private String defaultParameters;
    private int dependOn;


    @ManyToOne
    @JoinColumn(name = "con_op_id")
    private OperationConfig operation;


    public OperationConfig getOperation() {
        return operation;
    }

    public void setOperation(OperationConfig operation) {
        this.operation = operation;
    }

}
