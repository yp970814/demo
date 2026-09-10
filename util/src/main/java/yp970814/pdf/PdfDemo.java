package yp970814.pdf;

import cn.hutool.core.io.IoUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.RectangleReadOnly;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import yp970814.excel.ExcelDemo;
import yp970814.excel.export.ExportUtil;
import yp970814.model.vo.OrderDetailVO;
import yp970814.model.vo.OrderVO;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author pingge814@proton.me
 * date 2026/9/10 11:52
 */
public class PdfDemo {

    public void downloadPdf(HttpServletResponse response) {
        OrderVO orderVO = ExcelDemo.getOrderVO();
        String fileName = String.format("订单_%s.pdf", orderVO.getSequenceNo());
        CreatePDFTool.printPDF(fileName, this.createApprovalPdf(orderVO), response);
    }

    private byte[] createApprovalPdf(OrderVO vo) {
        try {
            Document document = new Document(new RectangleReadOnly(842f, 595f));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            CreatePDFTool.addPdfTitle(document, "订单信息", 20, 1, 30, 15);
            CreatePDFTool.addPdfTitle(document, "密级：" + "公开", 14, 2, 20, 15);

            Object[] arr1 = {"订单单号", vo.getSequenceNo()};
            Object[] arr2 = {"订单类型", vo.getOrderType()};
            Object[] arr3 = {"订单日期", vo.getOrderDate()};
            CreatePDFTool.addPdfTable(document, new Object[][]{arr1, arr2, arr3}, 14, 20, new float[]{20, 80});

            CreatePDFTool.addPdfTitle(document, "采购件信息", 15, 3, 20, 0);
            List<OrderDetailVO> details = vo.getOrderDetailDTOList();
            Object[][] arr4 = new Object[details.size() + 1][3];
            arr4[0] = new String[]{"物料编码", "物料名称", "数量"};
            for (int i = 0; i < details.size(); i++) {
                OrderDetailVO detail = details.get(i);
                arr4[i + 1] = new Object[]{
                        detail.getMaterialCode(), detail.getMaterialName(), detail.getQuantity()};
            }
            CreatePDFTool.addPdfTable(document, arr4, 14, 20, new float[]{40, 40, 20});

            /**
             * 合并单元格
             * 特俗符号，在linux中没有该字体，可以用图片替代
             */
            Map<String, String> orderTypeMap = ExportUtil.getDictValueKeyByCode("x");
            float[] width = new float[]{20, 80};
            CreatePDFTool.ImagePDF imagePDF = this.createImagePDF(width, orderTypeMap, "电商");
            Object[] arr5 = {"订单类型", imagePDF};
            Object[] arr6 = {"订单信息", new Object[][]{arr1, arr3, arr5}};
            CreatePDFTool.addPdfTableMerge(document, arr6, 14, 20, new float[][]{{10, 90}, {50, 50}});

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成PDF文件出现异常");
        }
    }

    private CreatePDFTool.ImagePDF createImagePDF(float[] width, Map<String, String> map, String value) throws IOException {
        // 图片信息
        InputStream resource1 = new ClassPathResource("static/1.jpg").getInputStream(),
                resource0 = new ClassPathResource("static/0.jpg").getInputStream();
        byte[] gou = IoUtil.readBytes(resource1), kong = IoUtil.readBytes(resource0);
        CreatePDFTool.ImagePDF imagePDF = CreatePDFTool.imagePDFBuilder().width(width);
        map.forEach((k, v) -> {
            CreatePDFTool.ImageAttribute[] arr = new CreatePDFTool.ImageAttribute[2];
            arr[0] = (k.equals(value)
                    ? CreatePDFTool.imageAttributeBuilder().text(gou)
                    : CreatePDFTool.imageAttributeBuilder().text(kong));
//                        : CreatePDFTool.imageAttributeBuilder().text("□");
            arr[1] = CreatePDFTool.imageAttributeBuilder().text(v);
            imagePDF.add(arr);
        });
        int column = width.length / 2;
        int size = imagePDF.size();
        int xx = size % column;
        for (int i = 0; i < xx; i++) {
            CreatePDFTool.ImageAttribute[] arr = new CreatePDFTool.ImageAttribute[2];
            arr[0] = CreatePDFTool.imageAttributeBuilder().text(" ");
            arr[1] = CreatePDFTool.imageAttributeBuilder().text(" ");
            imagePDF.add(arr);
        }
        return imagePDF;
    }

}
