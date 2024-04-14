package yp970814.job;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yp970814.mongo.MongoTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 清理monogdb日志数据
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:48
 */
@Component
public class CleanMongodbCronJob implements TaskJobFeignClientService {
    private static final Logger logger = LoggerFactory.getLogger(CleanMongodbCronJob.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 每天凌晨一点清理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO run(String params) {
        try {
            logger.info("---------------- 定时清理monogdb上个月日志数据 ----------------");
            Calendar now = Calendar.getInstance();
            //查询30天以前的时间
            now.add(Calendar.DAY_OF_MONTH, -30);
            String endDate = new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());
            logger.info("endDate：" + endDate);
            String[] nowDate = endDate.split("-");
            //年月日
            int startYear = Integer.parseInt(nowDate[0]);
            int startMonth = Integer.parseInt(nowDate[1]);
            int startDay = Integer.parseInt(nowDate[2]);

            Bson filter = Filters.lt("date", new Date(startYear - 1900, startMonth - 1, startDay));
            Long syslogInput = mongoTemplate.deletePatch("SYSLOG_INPUT", filter);
            Long syslogOutput = mongoTemplate.deletePatch("SYSLOG_OUTPUT", filter);
        } catch (Exception e) {
            logger.error("CleanMonogdbCronJob failed", e);
            throw e;
        }
        return ResultDTO.resultSuccess("清除成功");
    }
}
