package yp970814.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.data.util.CastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:45
 */
public class BaseConverter<T> implements Converter<T> {

    private final Class<T> clazz;

    // 子类传入class，接收LocalDate.class,LocalDateTime.class
    public BaseConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return clazz;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public T convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws ParseException {
        if (clazz == Date.class) {
            Date date;
            if (cellData.getType().equals(CellDataTypeEnum.NUMBER)) {
                if (contentProperty == null || contentProperty.getDateTimeFormatProperty() == null) {
                    date = DateUtil.getJavaDate(cellData.getNumberValue().doubleValue(),
                            globalConfiguration.getUse1904windowing(), null);
                } else {
                    date = DateUtil.getJavaDate(cellData.getNumberValue().doubleValue(),
                            contentProperty.getDateTimeFormatProperty().getUse1904windowing(), null);
                }
            } else if (cellData.getType().equals(CellDataTypeEnum.STRING)) {
                String dateString = cellData.getStringValue();
                int length = dateString.length();
                SimpleDateFormat formatter;
                if (length == 8) {
                    formatter = new SimpleDateFormat("yyyyMMdd");
                } else {
                    formatter = new SimpleDateFormat(DateUtils.switchDateFormat(dateString));
                }
                date = formatter.parse(dateString);
            } else {
                return null;
            }
            return CastUtils.cast(date);
        }
        if (clazz == LocalDate.class) {
            if (cellData.getType().equals(CellDataTypeEnum.NUMBER)) {
                LocalDate localDate = LocalDate.of(1900, 1, 1);
                localDate = localDate.plusDays(cellData.getNumberValue().longValue());
                return CastUtils.cast(localDate);
            } else if (cellData.getType().equals(CellDataTypeEnum.STRING)) {
                return CastUtils.cast(LocalDate.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_10)));
            } else {
                return null;
            }
        }
        if (clazz == LocalDateTime.class) {
            return CastUtils.cast(LocalDateTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19)));
        }
        return null;
    }

    @Override
    public WriteCellData<?> convertToExcelData(T value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        if (value instanceof Date) {
            String format = DateUtils.format((Date) value, DateUtils.DATE_FORMAT_10);
            return new WriteCellData<>(format);
        }
        if (value instanceof LocalDate) {
            LocalDate localDate = LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_10));
            return new WriteCellData<>(localDate.toString());
        }
        if (value instanceof LocalDateTime) {
            LocalDateTime localDateTime = LocalDateTime.parse(value.toString(), DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_19));
            return new WriteCellData<>(localDateTime.toString());
        }
        return new WriteCellData<>(value.toString());
    }

}
