package yp970814.idgenerator;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:57
 */
public class GetHardwareIdFailedException extends Exception {

    GetHardwareIdFailedException(String reason) {
        super(reason);
    }

    GetHardwareIdFailedException(Exception e) {
        super(e);
    }

}
