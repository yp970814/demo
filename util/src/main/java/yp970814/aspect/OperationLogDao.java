package yp970814.aspect;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-22 16:06
 */
@Mapper
public interface OperationLogDao extends BaseDao{

    void insert(Map<String, Object> data);

}
