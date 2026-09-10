package yp970814.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:42
 */
public class BooleanConverter implements Converter<Boolean> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Boolean.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.BOOLEAN;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Boolean> context) {
        Boolean contextValue = context.getValue();
        if (null == contextValue) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(contextValue ? "是" : "否");
    }

}
