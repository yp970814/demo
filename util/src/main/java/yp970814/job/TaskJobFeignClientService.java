package yp970814.job;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:49
 */
public interface TaskJobFeignClientService {

    @RequestMapping(value = "/run", method = RequestMethod.POST)
    ResultDTO run(@RequestParam(value = "params", required = false) String params);

}
