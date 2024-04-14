package yp970814.unpacke;

import lombok.extern.slf4j.Slf4j;
import yp970814.enums.RespCodeEnum;
import yp970814.exception.SystemRuntimeException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 13:17
 */
@Slf4j
public class ZipUtils {
    /**
     * 提供给用户使用的解压工具
     * @param srcPath
     * @param outPath
     * @throws IOException
     */
    public static void decompressionFile(String srcPath, String outPath) throws IOException {
        //简单判断解压路径是否合法
        if (!new File(srcPath).isDirectory()) {
            //判断输出路径是否合法
            if (new File(outPath).isDirectory()) {
                if (!outPath.endsWith(File.separator)) {
                    outPath += File.separator;
                }
                //zip读取压缩文件
                FileInputStream fileInputStream = new FileInputStream(srcPath);
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream, Charset.forName("GBK"));
                //解压文件
                decompressionFile(outPath, zipInputStream);
                //关闭流
                zipInputStream.close();
                fileInputStream.close();
            } else {
                throw new RuntimeException("输出路径不合法!");
            }
        } else {
            throw new RuntimeException("需要解压的文件不合法!");
        }
    }

    /**
     * ZipInputStream是逐个目录进行读取，所以只需要循环
     * @param outPath
     * @param inputStream
     * @throws IOException
     */
    private static void decompressionFile(String outPath, ZipInputStream inputStream) throws IOException {
        //读取一个目录
        ZipEntry nextEntry = inputStream.getNextEntry();
        //不为空进入循环
        while (nextEntry != null) {
            String name = nextEntry.getName();
            log.info("文件：{}",name);
            File file = new File(outPath+name);
            //如果是目录，创建目录
            if (name.endsWith("/")) {
                file.mkdir();
            } else {
                //文件则写入具体的路径中
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                int n;
                byte[] bytes = new byte[1024];
                while ((n = inputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, n);
                }
                //关闭流
                bufferedOutputStream.close();
                fileOutputStream.close();
            }
            //关闭当前布姆
            inputStream.closeEntry();
            //读取下一个目录，作为循环条件
            nextEntry = inputStream.getNextEntry();
        }
    }

    /**
     * 解压获取zip map
     * @param inputStream
     * @param fileName
     * @throws IOException
     */
    public static Map<String,byte[]> zipToMap(InputStream inputStream,String fileName,Charset charset) {
        if(!fileName.endsWith(".zip")){
            log.error("文件[{}]不是zip文件",fileName);
            throw new SystemRuntimeException("不是zip文件", RespCodeEnum.A0604);
        }
        Map<String, byte[]> zipMap = new HashMap<>();
        try {
            //InputStream inputStream = file.getInputStream();
            ZipInputStream zipInputStream = new ZipInputStream(inputStream, charset);
            //读取一个目录
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            //不为空进入循环
            while (nextEntry != null) {
                String name = nextEntry.getName();
                log.info("文件：{}",name);
                //File file = new File(outPath+name);
                //如果是目录，继续下一个
                if (name.endsWith("/")) {
                    nextEntry = zipInputStream.getNextEntry();
                    continue;
                }
                String code = name.substring(name.lastIndexOf("/")+1,name.lastIndexOf("."));
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024*4];
                int n = 0;
                while ((n = zipInputStream.read(bytes)) != -1) {
                    output.write(bytes, 0, n);
                }
                zipMap.put(code,output.toByteArray());
                output.close();
                //关闭当前布姆
                zipInputStream.closeEntry();
                //读取下一个目录，作为循环条件
                nextEntry = zipInputStream.getNextEntry();
            }
        }catch (Exception e){
            log.error("解压zip异常：",e);
        }
        return zipMap;
    }

    public static void main(String[] args) {
        try {
            InputStream fileInputStream = new FileInputStream("D:\\uat测试两种照片.zip");
            Map<String, byte[]> zipMap = zipToMap(fileInputStream, "uat测试两种照片.zip",Charset.forName("GBK"));
            log.info("返回zipMap:{}",zipMap);
        }catch (Exception e){
            log.error("解压文件异常：",e);
        }

    }
}
