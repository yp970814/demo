package yp970814.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yp970814.redis.RedisClusterDao;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 15:01
 */
@WebListener
public class OnlineUserListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(OnlineUserListener.class);

    public static final String ONLINE_USER_LIST_KEY_PREFIX = "key_onlineUserList#";

    protected static final Map<String, Object> sessionMap = new HashMap<String, Object>();

    protected static final Map<String, String> removeSessionIdMap = new HashMap<String, String>();

    // 子账号在线 子账号级别
    public static final String ONLINE_ELS_ACCOUNT_LIST_KEY_PREFIX = "key_onlineElsAccountList#";

    // 企业在线 账号级别
    public static final String ONLINE_ENTERPRISE_LIST_KEY_PREFIX = "key_onlineEnterpriseList#";

    // public static List<String> onlineUserList=new ArrayList<String>();

    // public static List<String> onlineElsAccountList=new ArrayList<String>();

//	public OnlineUserListener() {
//
//	}

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext application = session.getServletContext();
        logger.info("sessionId=" + session.getId());
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext application = session.getServletContext();
        // 取得登录的用户名
        String username = (String) session.getAttribute("username");
        // 清空用户权限缓存

        // 从在线列表中删除用户名
        // RedisDao redisDao=new RedisDao();

        RedisClusterDao redisDao = new RedisClusterDao();

        if (username != null)
            redisDao.lrem(ONLINE_USER_LIST_KEY_PREFIX, 1, username);
        // onlineUserList.remove(username);
        logger.info(username + "退出了");
        String elsAccount = (String) session.getAttribute("elsAccount");
        String elsSubAccount = (String) session.getAttribute("elsSubAccount");
        // onlineElsAccountList.remove(elsAccount+"_"+elsSubAccount);
        if (elsAccount != null) {
            redisDao.lrem(ONLINE_ELS_ACCOUNT_LIST_KEY_PREFIX, 1, elsAccount + "_" + elsSubAccount);
            if (session.getAttribute("username") != null) {
                sessionMap.remove(elsAccount + "_" + elsSubAccount);
            }
        }
        // 清空用户权限缓存
        redisDao.del("key_accountApp#" + elsAccount + "_" + elsSubAccount);
        redisDao.del("key_accountResource#" + elsAccount + "$" + elsSubAccount);
        redisDao.del("key_dataRule#" + elsAccount + "$" + elsSubAccount);
    }
    public static Map<String, Object> getSessionmap() {
        return sessionMap;
    }

    public static Map<String, String> getRemovesessionidmap() {
        return removeSessionIdMap;
    }

}
