package yp970814.idgenerator;

/**
 * @author pingge814@proton.me
 * date 2026/9/9 16:56
 */
public interface EntityIdGenerator {

    Long generateLongId() throws InvalidSystemClockException, GetHardwareIdFailedException;

}
