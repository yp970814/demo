package yp970814.aspect;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-22 16:08
 */
@Service
public class OperationLog {

    @Autowired
    private OperationLogDao operationLogDao;

    /**
     * 保存操作日志
     * @param data
     */
    @Transactional
    public void insert(Map<String, Object> data) {
        operationLogDao.insert(data);
    }
}
