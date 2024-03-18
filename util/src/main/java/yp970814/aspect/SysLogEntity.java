package yp970814.aspect;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-22 16:10
 */
@Table(name = "sys_log")
@Data
public class SysLogEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    private String id;

    @Column(name = "module")
    private String module;

    @Column(name = "operation")
    private String operation;

    @Column(name = "detail")
    private String detail;

    @Column(name = "execute_time")
    private Double executeTime;

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "error_msg")
    private String errorMsg;

    @Column(name = "class_name")
    private String className;

    @Column(name = "method_name")
    private String methodName;

    @Column(name = "crt_user")
    private String crtUser;

    @Column(name = "crt_name")
    private String crtName;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "crt_host")
    private String crtHost;

    public SysLogEntity() {
    }

    public SysLogEntity(String module, String operation, String detail, Double executeTime, String errorMsg, String className, String methodName) {
        this.module = module;
        this.operation = operation;
        this.detail = detail;
        this.executeTime = executeTime;
        this.errorMsg = errorMsg;
        this.className = className;
        this.methodName = methodName;
    }

    public SysLogEntity(String module, String operation, String detail, String className, String methodName) {
        this.module = module;
        this.operation = operation;
        this.detail = detail;
        this.className = className;
        this.methodName = methodName;
    }

}
