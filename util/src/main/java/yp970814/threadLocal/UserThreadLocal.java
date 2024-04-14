package yp970814.threadLocal;

import yp970814.jwt.ElsAuthVO;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:08
 */
public class UserThreadLocal {

    private static final ThreadLocal<ElsAuthVO> LOCAL = new ThreadLocal<>();

    private UserThreadLocal(){

    }

    /**
     * 将对象放入到ThreadLocal
     *
     * @param user
     */
    public static void set(ElsAuthVO user){
        LOCAL.set(user);
    }

    /**
     * 返回当前线程中的User对象
     *
     * @return
     */
    public static ElsAuthVO get(){
        return LOCAL.get();
    }

    /**
     * 删除当前线程中的User对象
     */
    public static void remove(){
        LOCAL.remove();
    }

}
