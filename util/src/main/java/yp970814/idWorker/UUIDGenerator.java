package yp970814.idWorker;

import java.util.UUID;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:44
 */
public class UUIDGenerator {

    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        return str.replace("-", "");
    }

    public static String[] getUuids(int number) {// 获得指定数量的UUID
        if (number < 1) {
            return null;
        }
        String[] ss = new String[number];
        for (int i = 0; i < number; i++) {
            ss[i] = getUuid();
        }
        return ss;
    }

    public static void main(String[] args) {
        String a = "asd1asdwasx";
        a = a.replaceAll("sdw", "aa");
        System.out.println(a);
    }

}
