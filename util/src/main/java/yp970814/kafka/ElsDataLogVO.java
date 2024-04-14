package yp970814.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:33
 */
@XmlRootElement(name = "ElsDataLogVO")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ElsDataLogVO {

    private static final long serialVersionUID = 1L;
    /**
     * uuid
     */
    private Long id;

    /**
     * 日志id
     */
    private String logId;

    /**
     * 操作类型(1:input,2:output)
     */
    private String type;

    /**
     * 参数
     */
    private String param;

    /**
     * 时间戳
     */
    private Long logtime;

    /**
     * 创建用户
     */
    private String createUser;

    /**
     *
     */
    private String fbk1;

    /**
     *
     */
    private String fbk2;

    /**
     *
     */
    private String fbk3;

    /**
     *
     */
    private String fbk4;

    /**
     *
     */
    private String fbk5;

    /**
     *
     */
    private String fbk6;

    /**
     *
     */
    private String fbk7;

    /**
     *
     */
    private String fbk8;

    /**
     *
     */
    private String fbk9;

    /**
     *
     */
    private String fbk10;

}
