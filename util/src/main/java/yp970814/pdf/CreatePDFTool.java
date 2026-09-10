package yp970814.pdf;


import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author pingge814@proton.me
 * date 2026/9/10 11:55
 */
@Slf4j
public class CreatePDFTool {

    private static final BaseFont BASE_FONT;

    private static final int MIN_HEIGHT_CELL;

    private static final int WIDTH_PERCENTAGE;

    static {
        try {
            BASE_FONT = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            MIN_HEIGHT_CELL = 20;
            WIDTH_PERCENTAGE = 100;
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ImagePDF imagePDFBuilder() {
        return new ImagePDF();
    }

    @NoArgsConstructor
    public static class ImagePDF {
        private List<ImageAttribute[]> list;
        private float[] width;
        public ImagePDF add(ImageAttribute[] arr) {
            if (this.list == null) {
                this.list = new ArrayList<>();
            }
            this.list.add(arr);
            return this;
        }
        public ImagePDF add(int index, ImageAttribute[] arr) {
            this.list.add(index, arr);
            return this;
        }
        public int size() {
            return list.size();
        }
        public ImagePDF width(float[] width) {
            this.width = width;
            return this;
        }
    }

    public static ImageAttribute imageAttributeBuilder() {
        return new ImageAttribute();
    }

    @NoArgsConstructor
    public static class ImageAttribute {
        private Object text;
        public ImageAttribute text(Object text) {
            this.text = text;
            return this;
        }
    }

    public static void printPDF(String fileName, byte[] byteArray, HttpServletResponse response) {
        OutputStream outputStream = null;
        try {
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            String fileNameEncoder = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileNameEncoder);
            outputStream = response.getOutputStream();
            outputStream.write(byteArray);
            outputStream.flush();
        } catch (Exception e) {
            log.error("打印文件出现异常", e);
            throw new RuntimeException("打印文件出现异常");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("文件流关闭失败", e);
                }
            }
        }
    }

//    /**
//     * 添加水印
//     */
//    public static void addTextFullWaterMark(PdfWriter pdfWriter) {
//        // 水印内容
//        OpenEngine openEngine = OpenEngineFactory.getOpenEngine();
//        String userId = openEngine.getUserId();
//        User currUser = openEngine.getUserById(userId);
//        String text = String.format("%s-%s", currUser.getName(), currUser.getUsername());
//        // 设置水印属性
//        PdfGState gs = new PdfGState();
//        // 设置水印透明度
//        gs.setFillOpacity(0.3f);
//
//        PdfContentByte content = pdfWriter.getDirectContent();
//        content.beginText();
//        content.setFontAndSize(BASE_FONT, 25);
//        content.setGState(gs);
//        // 设置水印颜色
//        content.setColorFill(BaseColor.GRAY);
//        // 设置水印对其方式 水印内容 X坐标 Y坐标 旋转角度
//        for (int x = 0; x <= 900; x += 200) {
//            for (int y = -50; y <= 800; y += 200) {
//                content.showTextAligned(Element.ALIGN_RIGHT, text, x, y, 35);
//            }
//        }
//        // 结束设置
//        content.endText();
//        content.stroke();
//    }

    /**
     * 添加加粗标题
     */
    public static void addPdfTitleBold(Document document, Object text, int fontSize, int alignment, int height, int side) throws DocumentException {
        addPdfTitle(document, text, new Font(BASE_FONT, fontSize, Font.BOLD), alignment, height, side);
    }

    /**
     * 添加标题
     * @param document      文档
     * @param text          内容
     * @param fontSize      字体大小
     * @param alignment     对齐方式（ParagraphAlignment类  1：居中、2：居右、3：居左）
     * @param height        高度
     * @param side          隐藏边框（‌1‌：隐藏上边框、2‌：隐藏下边框、4‌：隐藏左边框、8‌：隐藏右边框、3‌：隐藏上、下边框、5‌：隐藏左、下边框、9：隐藏右、下边框、11‌：隐藏左、右边框、15‌：隐藏全部边框）
     * @throws DocumentException 文档异常
     */
    public static void addPdfTitle(Document document, Object text, int fontSize, int alignment, int height, int side) throws DocumentException {
        addPdfTitle(document, text, new Font(BASE_FONT, fontSize), alignment, height, side);
    }

    public static void addPdfTitle(Document document, Object text, Font font, int alignment, int height, int side) throws DocumentException {
        addPdfTitle(document, new Object[]{text}, font, new int[]{alignment}, height, side, new float[]{100});
    }

    /**
     * 添加标题内容
     * @param document      文档
     * @param text          内容
     * @param fontSize      字体大小
     * @param alignment     对齐方式（ParagraphAlignment类  2：居中、11‌：居左、12：居右）
     * @param height        高度
     * @param side          隐藏边框（‌1‌：隐藏上边框、
     *                              2‌：隐藏下边框、
     *                              3‌：隐藏上、下边框
     *                              4‌：隐藏左边框、
     *                              5‌：隐藏左、上边框
     *                              6‌：隐藏左、下边框
     *                              7‌：隐藏左、上、下边框
     *                              8‌：隐藏右边框、
     *                              9：隐藏右、上边框、
     *                              10：隐藏右、下边框、
     *                              11‌：隐藏右、上、下边框
     *                              12：隐藏左、右边框
     *                              13：隐藏上、左、右边框
     *                              14：隐藏下、左、右
     *                              15‌：隐藏全部边框）
     * @param width         宽度
     * @throws DocumentException 文档异常
     */
    public static void addPdfTitle(Document document, Object[] text, int fontSize, int[] alignment, int height, int side, float[] width) throws DocumentException {
        addPdfTitle(document, text, new Font(BASE_FONT, fontSize), alignment, height, side, width);
    }

    public static void addPdfTitle(Document document, Object[] text, Font font, int[] alignment, int height, int side, float[] width) throws DocumentException {
        PdfPTable table = new PdfPTable(width);
        table.setWidthPercentage(WIDTH_PERCENTAGE);
        for (int i = 0; i < width.length; i++) {
            table.addCell(createPdfCell(text[i], font, alignment[i], height, side));
        }
        document.add(table);
    }

    /**
     * 添加表格
     * @param document      文档
     * @param text          内容
     * @param fontSize      字体大小
     * @param height        高度
     * @param width         宽度
     */
    public static void addPdfTable(Document document, Object[] text, int fontSize, int height, float[] width) throws DocumentException {
        addPdfTable(document, new Object[][]{text}, fontSize, height, width);
    }

    public static void addPdfTable(Document document, Object[][] text, int fontSize, int height, float[] width) throws DocumentException {
        document.add(addPdfTable(text, fontSize, height, width, false));
    }

    public static void addPdfTableMerge(Document document, Object[] text, int fontSize, int height, float[][] width) throws DocumentException {
        int i = 0;
        PdfPTable table = new PdfPTable(width[i++]);
        table.setWidthPercentage(WIDTH_PERCENTAGE);
        Font font = new Font(BASE_FONT, fontSize);
        for (Object obj : text) {
            if (obj instanceof Object[][]) {
                Object[][] arr = (Object[][]) obj;
                table.addCell(addPdfTable(arr, fontSize, height, width[i++], true));
            } else {
                add(table, obj, font, height, 0);
            }
        }
        document.add(table);
    }

    private static PdfPTable addPdfTable(Object obj, int fontSize, int height, float[] width, boolean disableBorder) {
        PdfPTable table = new PdfPTable(width);
        table.setWidthPercentage(WIDTH_PERCENTAGE);
        Font font = new Font(BASE_FONT, fontSize);
        if (obj instanceof Object[][]) {
            Object[][] text = (Object[][]) obj;
            for (int i = 0; i < text.length; i++) {
                Object[] arr = text[i];
                for (int j = 0; j < arr.length; j++) {
                    int side = 0;
                    if (disableBorder) {
                        int upSide = 0, downside = 0;
                        if (i == 0) {
                            upSide = 1;
                        }
                        if (i == text.length - 1) {
                            downside = 2;
                        }
                        int leftSide = 0, rightSide = 0;
                        if (j == 0) {
                            leftSide = 4;
                        }
                        if (j == arr.length - 1) {
                            rightSide = 8;
                        }
                        side = upSide + downside + leftSide + rightSide;
                    }
                    add(table, arr[j], font, height, side);
                }
            }
        } else if (obj instanceof Object[]) {
            Object[] arr = (Object[]) obj;
            for (Object objText : arr) {
                add(table, objText, font, height, 0);
            }
        }
        return table;
    }

    private static void add(PdfPTable table, Object obj, Font font, int height, int side) {
        if (obj instanceof CreatePDFTool.ImagePDF) {
            PdfPCell cell = new PdfPCell(addPdfImage(obj, font, height));
            cell.disableBorderSide(side);
            table.addCell(cell);
        } else {
            table.addCell(createPdfCell(obj, font, 1, height, side));
        }
    }

    // 1：居中、2：居右、3：居左
    private static PdfPTable addPdfImage(Object obj, Font font, int height) {
        try {
            CreatePDFTool.ImagePDF imagePDF = (CreatePDFTool.ImagePDF) obj;
            List<ImageAttribute[]> list = imagePDF.list;
            float[] width = imagePDF.width;
            PdfPTable table = new PdfPTable(width);
            table.setWidthPercentage(WIDTH_PERCENTAGE);
            for (ImageAttribute[] arr : list) {
                for (ImageAttribute imageAttribute : arr) {
                    Object text = imageAttribute.text;
                    PdfPCell cell;
                    if (text instanceof byte[]) {
                        Image image = Image.getInstance((byte[]) text, true);
                        cell = new PdfPCell(image);
                        cell.setPaddingTop(0.5f);
//                        cell.setPaddingLeft(30);
                    } else {
                        cell = new PdfPCell();
                        Paragraph paragraph = new Paragraph((String) text);
                        paragraph.setFont(font);
                        paragraph.setAlignment(3);
                        cell.addElement(paragraph);
                    }
                    cell.setUseAscender(true);
                    // 设置水平对齐方式为靠右
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    // 设置垂直对齐方式为居中
                    cell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
                    cell.setMinimumHeight(height <= 0 ? MIN_HEIGHT_CELL : height);
                    cell.disableBorderSide(15);
                    table.addCell(cell);
                }
            }
            return table;
        } catch (BadElementException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static PdfPCell createPdfCell(Object obj, Font font, int alignment, int height, int side) {
        PdfPCell cell = new PdfPCell();
        cell.setUseAscender(true);
        // 设置水平对齐方式为靠左
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        // 设置垂直对齐方式为居中
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setMinimumHeight(height <= 0 ? MIN_HEIGHT_CELL : height);
        String text;
        if (obj instanceof BigDecimal) {
            BigDecimal number = (BigDecimal) obj;
            text = eraseZero(number, 4);
        } else if (obj instanceof Double) {
            BigDecimal number = new BigDecimal(Double.toString((Double) obj));
            text = eraseZero(number, 2);
        } else {
            text = Objects.toString(obj, " ");
        }
        Paragraph paragraph = new Paragraph(text);
        paragraph.setFont(font);
        paragraph.setAlignment(alignment);
        cell.addElement(paragraph);
        cell.disableBorderSide(side);
        return cell;
    }

    private static String eraseZero(BigDecimal number, int scale) {
        if (number == null) {
            return "0";
        }
        // 向下取整
        BigDecimal integerPart = number.setScale(0, RoundingMode.DOWN);
        // 小数部分
        BigDecimal decimalPart = number.subtract(integerPart);
        if (decimalPart.compareTo(BigDecimal.ZERO) == 0) {
            number = integerPart;
        } else {
            // 保留两位小数
            number = number.setScale(scale, RoundingMode.HALF_UP);
        }
        return number.toString();
    }

//    private static List<WorkflowIntanceLogInfoVO> listWorkflowLog(Function<String, ResponseResult<List<WorkflowIntanceLogInfoVO>>> function) {
//        ResponseResult<List<WorkflowIntanceLogInfoVO>> result = function.apply("");
//        List<WorkflowIntanceLogInfoVO> list = result.getData();
//        return list.stream().filter(s ->
//                ApprovalActionStatus.PASSIVECANCELLED != s.getApprovalActionStatus() && s.getOriginator() != null).collect(Collectors.toList());
//    }
//
//    public static void addPdfWorkflow(Document document,
//                                      Function<String, ResponseResult<List<WorkflowIntanceLogInfoVO>>> function, boolean isProposer) throws DocumentException {
//        addPdfWorkflow(document, function, isProposer, new float[]{10, 15, 15, 60});
//    }
//
//    public static void addPdfWorkflow(Document document,
//                                      Function<String, ResponseResult<List<WorkflowIntanceLogInfoVO>>> function, boolean isProposer, float[] width) throws DocumentException {
//        List<WorkflowIntanceLogInfoVO> list = listWorkflowLog(function);
//        if (CollectionUtils.isNotEmpty(list)) {
//            if (isProposer) {
//                WorkflowIntanceLogInfoVO s = list.get(0);
//                LocalDate finishTime = null;
//                if (s.getFinishTime() != null) {
//                    finishTime = DateToolUtil.dateConvertLocalDate(s.getFinishTime());
//                }
//                Object[] arr10 = {"发起人", s.getOriginator().getName(), "发起日期", finishTime};
//                CreatePDFTool.addPdfTable(document, arr10, 14, 20, width);
//                list.remove(0);
//            }
//            Object[][] arr11 = new Object[list.size() + 1][5];
//            arr11[0] = new Object[]{"节点", "审批人", "审批结果", "审批意见", "审批日期"};
//            for (int i = 0; i < list.size(); i++) {
//                WorkflowIntanceLogInfoVO s = list.get(i);
//                LocalDate finishTime = null;
//                if (s.getFinishTime() != null) {
//                    finishTime = DateToolUtil.dateConvertLocalDate(s.getFinishTime());
//                }
//                arr11[i + 1] = new Object[]{s.getActivityName(), s.getOriginator().getName(), s.getApprovalActionStatus().getName(), s.getComment(), finishTime};
//            }
//            addPdfTable(document, arr11, 14, 20, new float[]{20, 20, 20, 20, 20});
//        }
//    }
//
//    public static List<String[]> addExcelWorkflowTwo(
//            Function<String, ResponseResult<List<WorkflowIntanceLogInfoVO>>> function, boolean isProposer) {
//        List<WorkflowIntanceLogInfoVO> list = listWorkflowLog(function);
//        List<String[]> lists = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            WorkflowIntanceLogInfoVO s = list.get(i);
//            LocalDate finishTime = null;
//            if (s.getFinishTime() != null) {
//                finishTime = DateToolUtil.dateConvertLocalDate(s.getFinishTime());
//            }
//            String finishTimeStr = Objects.toString(finishTime, null);
//            if (isProposer && i == 0) {
//                String[] arr10 = {"发起人", s.getOriginator().getName(), "发起日期", finishTimeStr};
//                lists.add(arr10);
//            } else {
//                String[] arr11 = {"审批人", s.getOriginator().getName(), "审批意见", s.getComment(), "审批日期", finishTimeStr};
//                lists.add(arr11);
//            }
//        }
//        return lists;
//    }

}
