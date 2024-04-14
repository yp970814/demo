package yp970814.unpacke;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 13:10
 */
@Slf4j
public class UnPackeUtil {


    private static final String SAVE_PATH =  "Temp" + File.separator;

    /**
     * @param srcFile   Zip文件
     * @param createdId 保存地址/创建文件夹的唯一ID,
     * @return 保存路径
     * @throws RuntimeException 异常
     * @throws IOException      文件异常
     */
    public static String unPackZip(MultipartFile srcFile, String createdId) throws RuntimeException, IOException {
        long startTime = System.currentTimeMillis();
        InputStream ins = srcFile.getInputStream();
        ArchiveEntry ze = null;
        byte[] buffer = new byte[1024];
        try {
            ZipArchiveInputStream zis = new ZipArchiveInputStream(ins);
            ze = zis.getNextEntry();
            while (ze != null) {
                if (ze.getSize() == 0) {
                    ze = zis.getNextEntry();
                    continue;
                }
                log.info("文件长度：{}",ze.getSize());
                String fileName = ze.getName().substring(ze.getName().lastIndexOf("/") + 1, ze.getName().length());
                File newFile = new File(SAVE_PATH + createdId + File.separator + fileName);

                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }

            zis.close();
            long endTime = System.currentTimeMillis();
            log.info("unZip time-->" + (endTime - startTime) + " ms");
        } catch (IOException e) {
            log.error("解压zip出错:", e);
        }
        log.info("耗时：{}",System.currentTimeMillis()-startTime);
        return SAVE_PATH + createdId;
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, buffer.length)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
            log.info("MultipartFile transform to File completed!");
        } catch (Exception e) {
            log.info("系统错误！",e);
        }
    }
}
