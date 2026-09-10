package yp970814.excel.importExcel;

/**
 * @author pingge814@proton.me
 * date 2026/9/10 09:54
 */
public class ImportExcelException extends RuntimeException {

    public ImportExcelException() {
    }

    public ImportExcelException(String message) {
        super(message);
    }

    public ImportExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportExcelException(Throwable cause) {
        super(cause);
    }

}
