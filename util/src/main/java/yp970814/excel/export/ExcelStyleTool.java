package yp970814.excel.export;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:50
 */
public class ExcelStyleTool {

    /**
     * 设置excel样式
     */
    public static HorizontalCellStyleStrategy getStyleStrategy() {
        // 头的策略  样式调整
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 设置头部样式
        setHeadStyle(headWriteCellStyle, true, true);
        // 设置细边框
        setBorder(headWriteCellStyle);
        // 表头字体样式
        WriteFont headWriteFont = getHeadFont();
        headWriteCellStyle.setWriteFont(headWriteFont);

        // 内容的策略
        WriteCellStyle contentStyle = new WriteCellStyle();
        // 设置内容样式
        setContentStyle(contentStyle, false, true);
        // 设置边框
        setBorder(contentStyle);
        // 内容字体
        WriteFont contentWriteFont = getContentFont();
        contentStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentStyle);
    }

    /**
     * 获取表头字体
     * @return
     */
    private static WriteFont getHeadFont() {
        //表头字体样式
        WriteFont headWriteFont = new WriteFont();
        // 头字号
        headWriteFont.setFontHeightInPoints((short) 13);
        // 字体样式
//        headWriteFont.setFontName("微软雅黑");
        // 字体加粗
        headWriteFont.setBold(true);
        return headWriteFont;
    }

    /**
     * 获取内容字体
     * @return
     */
    private static WriteFont getContentFont() {
        //内容字体
        WriteFont contentWriteFont = new WriteFont();
        // 内容字号
        contentWriteFont.setFontHeightInPoints((short) 11);
        // 字体样式
//        contentWriteFont.setFontName("宋体");
        // 加粗
//        contentWriteFont.setBold(true);
        return contentWriteFont;
    }

    /**
     *
     * @param cellStyle
     * @param wrappedFlag   自动换行标识，true:开启自动换行
     * @param centerFlag    水平居中开关，true:开启水平居中
     */
    private static void setHeadStyle(WriteCellStyle cellStyle, boolean wrappedFlag, boolean centerFlag) {
        // 头背景 白色
        cellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        if (wrappedFlag) {
            // 自动换行
            cellStyle.setWrapped(true);
        }
        if (centerFlag) {
            // 水平对齐方式
            cellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        }
        // 垂直对齐方式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    /**
     *
     * @param cellStyle
     * @param wrappedFlag   自动换行标识，true:开启自动换行
     * @param centerFlag    水平居中开关，true:开启水平居中
     */
    private static void setContentStyle(WriteCellStyle cellStyle, boolean wrappedFlag, boolean centerFlag) {
        // 头背景 白色
        cellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        if (wrappedFlag) {
            // 自动换行
            cellStyle.setWrapped(true);
        }
        if (centerFlag) {
            // 水平对齐方式
            cellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        }
        // 垂直对齐方式
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    /**
     * 设置边框
     * @param cellStyle
     */
    private static void setBorder(WriteCellStyle cellStyle){
        // 设置细边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        // 设置边框颜色 25灰度
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
    }

}
