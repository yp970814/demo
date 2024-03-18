package yp970814.kafka;

/**
 * @Author yuanping970814@163.com
 * @Date 2023-11-23 18:58
 */
public class KafkaAvroRecordParse implements Runnable{

    private String recordId;

    public KafkaAvroRecordParse() {
    }

    public KafkaAvroRecordParse(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public void run() {
//        WmsCdcRecordService wmsCdcRecordService = (WmsCdcRecordService) WmsSpringUtils.getBean("wmsCdcRecordServiceImpl");
//        wmsCdcRecordService.createCdcRecordDetailByRecordId(recordId);
    }

}
