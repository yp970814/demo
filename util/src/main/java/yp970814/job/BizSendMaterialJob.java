package yp970814.job;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import yp970814.redis.jedis.RedisContext;
import yp970814.redis.jedis.RedisManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 15:42
 */
@Component
@JobHander("sendMaterialJob")
@Slf4j
public class BizSendMaterialJob extends IJobHandler {

    @Autowired
    RedisContext redisContext;

    @Override
    public ReturnT<String> execute(Map<String, Object> map) throws Exception {
        XxlJobLogger.log("刷新发料过账结果！");
//        Integer tenantId = null;
//        Object tenantIdObj = map.get(WmsCommonConstants.TENANT_ID_KEY);
//        if (ObjectUtils.isNull(tenantIdObj)) {
//            throw new ParamsIncorrectException("租户编码不能为空!!");
//        }
//        Object headIdObj = map.get("headId");
//        String headId = Validator.isEmpty(headIdObj) ? null : headIdObj.toString();
//        Integer maxQueryTime = map.get(WmsCommonConstants.MAX_QUERY_TIME_KEY) == null ? WmsCommonConstants.MAX_QUERY_TIME : (Integer) map.get(WmsCommonConstants.MAX_QUERY_TIME_KEY);
//        Integer maxCallTime = map.get(WmsCommonConstants.MAX_CALL_TIME_KEY) == null ? WmsCommonConstants.MAX_CALL_TIME : (Integer) map.get(WmsCommonConstants.MAX_CALL_TIME_KEY);
//        String tenantIds = Validator.isEmpty(tenantIdObj) ? null : tenantIdObj.toString();
//        List<Integer> tenantIdList = JSON.parseArray(tenantIds, Integer.class);
//        for (Integer tenant : tenantIdList) {
//            tenantId = tenant;
//            CurrentContext.set(WmsCommonConstants.TENANT_ID_KEY, tenant);
//            List<BizSendMaterialHead> heads = bizSendMaterialHeadService.selectUnFinish(tenantId, headId);
//            if (CollectionUtils.isNotEmpty(heads)) {
//                heads = validateMRP(heads);
//                if(CollectionUtils.isEmpty(heads)) continue;
//                XxlJobLogger.log("准备执行发料单过账！条数：" + heads.size());
//                for (BizSendMaterialHead head : heads) {
//                    String uuid = UUIDUtils.get32UUID();
//                    Jedis jedis = null;
//                    boolean lock = false;
//                    try {
//                        jedis = redisContext.getJedis();
//                        lock = RedisManager.tryGetDistributedLock(jedis, WmsCommonConstants.ASYNC_POST_LOCK + BizTypeEnum.SEND.getCode() + head.getId(), uuid, 1200 * 1000);
//                        if (lock) {
//                            bizSendMaterialPostService.refreshPostResult(tenantId, headId, maxQueryTime, maxCallTime, Lists.newArrayList(head));
//                        } else {
//                            XxlJobLogger.log("发料过账任务正在执行中" + head.getId());
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        XxlJobLogger.log(e.getMessage());
//                        throw new RuntimeException(e);
//                    } finally {
//                        if (jedis != null) {
//                            RedisManager.releaseDistributedLock(jedis, WmsCommonConstants.ASYNC_POST_LOCK + BizTypeEnum.SEND.getCode() + head.getId(), uuid);
//                            redisContext.returnJedis(jedis);
//                        }
//                    }
//                }
//            }
//        }
        XxlJobLogger.log("刷新发料过账结果完成！");

        return ReturnT.SUCCESS;
    }

    /**
     * 校验MRP运算是否进行中
     * @param heads
     * @return
     */
//    private List<BizSendMaterialHead> validateMRP(List<BizSendMaterialHead> heads){
//        List<BizSendMaterialHead> taskHeads = new ArrayList<>();
//        List<String> factoryNos = heads.stream().map(BizSendMaterialHead::getFactoryNo).distinct().collect(Collectors.toList());
//        Map<String, String> mesEbsMappingMap = dtsMesEbsMappingService.selectOrganizationsByFactoryNos(factoryNos);
//        for (Map.Entry<String, String> entry : mesEbsMappingMap.entrySet()) {
//            //MRP运算错误
//            try {
//                boolean b = scmRequestInvoke.mrpRunningStatus(entry.getValue());
//                XxlJobLogger.log("查询 organizationId = " + entry.getValue() + " MRP运算状态：" + b);
//                if (b) {
//                    XxlJobLogger.log("MRP运算中，跳过 organizationId = " + entry.getKey() + " 本次账务过账请求！");
//                    continue;
//                }
//                List<BizSendMaterialHead> matchList = heads.stream().filter(i -> StringUtils.equals(entry.getKey(), i.getFactoryNo())).collect(Collectors.toList());
//                if (CollectionUtils.isNotEmpty(matchList)) taskHeads.addAll(matchList);
//            } catch (Exception e) {
//                XxlJobLogger.log("查询MRP运算异常！" + e.getMessage());
//            }
//        }
//        if (CollectionUtils.isEmpty(taskHeads)) {
//            XxlJobLogger.log("没有需要执行的单据号！");
//            return taskHeads;
//        }
//        return  taskHeads;
//    }

}
