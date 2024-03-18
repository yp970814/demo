package yp970814.aspect;

import java.util.List;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-22 16:02
 */
public interface BaseDao {
    <T> List<T> queryPage(Object model);
    int countPage(Object model);
    <T> T queryDetail(Object model);
    void update(Object model);
    void delete(Object model);
    void insert(Object model);
    <T> List<T> query(Object model);
    int count(Object model);
}
