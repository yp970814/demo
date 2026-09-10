package yp970814.excel;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Get;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.web.multipart.MultipartFile;
import yp970814.excel.export.ExportUtil;
import yp970814.excel.importExcel.CustomEasyExcelReadListener;
import yp970814.excel.importExcel.ImportExcelException;
import yp970814.model.dto.OrderDTO;
import yp970814.model.dto.OrderDetailDTO;
import yp970814.model.vo.OrderDetailVO;
import yp970814.model.vo.OrderVO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pingge814@proton.me
 * date 2026/9/10 09:57
 */
@Slf4j
public class ExcelDemo {

    /**
     * 下载导入模板
     * @param response
     */
    public void downloadExcelImportTemplate(HttpServletResponse response) {
        ExportUtil.exportExcel(response, "订单导入模板", new ArrayList<>(), OrderImportDTO.class);
    }

    /**
     * 导入订单
     */
    public void importOrderExcel(MultipartFile file) {
        InputStream in = null;
        try {
            // 获取文件流
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
                throw new ImportExcelException("仅支持 .xls 或 .xlsx 格式的文件");
            }
            in = file.getInputStream();
            CustomEasyExcelReadListener<OrderImportDTO> mySheetListener = new CustomEasyExcelReadListener<>(OrderImportDTO.class);
            Integer headRowNumber = mySheetListener.getTotleHeadRowNum();
            EasyExcel.read(in, mySheetListener.getHeadClazz(), mySheetListener)
                    .extraRead(CellExtraTypeEnum.MERGE)
                    .sheet()
                    .headRowNumber(headRowNumber)
                    .doRead();
            mySheetListener.validate();
            List<OrderImportDTO> list = mySheetListener.getRows();
            if (CollectionUtils.isEmpty(list)) {
                throw new ImportExcelException("导入文件无有效数据，请核对模板后重新导入！");
            }

            List<OrderDTO> orderDTOList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                int index = i + 1 + headRowNumber;
                OrderImportDTO dto = list.get(i);
                log.info("订单导入excel，{}行，解析数据为：{}", index, JSON.toJSONString(dto));
                if (dto.getStartRowNum().equals(i)) {
                    OrderDTO orderDTO = new OrderDTO();
                    BeanUtil.copyProperties(dto, orderDTO);
                    // 订单明细
                    orderDTO.setOrderDetailDTOList(new ArrayList<>());
                    orderDTOList.add(orderDTO);
                }
                OrderDTO orderDTO = orderDTOList.get(orderDTOList.size() - 1);

                OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
                BeanUtil.copyProperties(dto, orderDetailDTO);

                orderDTO.getOrderDetailDTOList().add(orderDetailDTO);
            }

        } catch (ImportExcelException e) {
            log.info("解析Excel文件失败", e);
            throw e;
        } catch (Exception e) {
            log.info("解析Excel文件失败", e);
            throw new ImportExcelException("解析Excel文件失败");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("文件流关闭失败", e);
                }
            }
        }
    }

    /**
     * 合并导出excel
     * @param response
     */
    public void exportOrderExcel(HttpServletResponse response) {
        List<OrderVO> orderList = new ArrayList<OrderVO>(){{
           add(getOrderVO());
        }};
        ExportUtil.exportExcel(response, "采购订单导出", orderList, OrderVO.class);
    }

    public static @NonNull OrderVO getOrderVO() {
        OrderVO orderVO = new OrderVO();
        orderVO.setSequenceNo("001");
        orderVO.setOrderType("电商");
        orderVO.setOrderDate(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        List<OrderDetailVO> orderDetailList = new ArrayList<>();
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setMaterialCode("aaa");
        orderDetailVO.setMaterialName("西瓜");
        orderDetailVO.setQuantity(BigDecimal.ONE);
        orderDetailList.add(orderDetailVO);

        OrderDetailVO orderDetailVO2 = new OrderDetailVO();
        orderDetailVO.setMaterialCode("bbb");
        orderDetailVO.setMaterialName("菠萝");
        orderDetailVO.setQuantity(BigDecimal.valueOf(2));
        orderDetailList.add(orderDetailVO2);

        orderVO.setOrderDetailDTOList(orderDetailList);

        return orderVO;
    }

}
