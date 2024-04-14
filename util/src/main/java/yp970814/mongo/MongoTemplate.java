package yp970814.mongo;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author yuanping970814@163.com
 * @Date 2024-04-14 12:41
 */
@Service
public class MongoTemplate {

    private static final Logger logger = LoggerFactory.getLogger(MongoTemplate.class);

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.data.mongodb.authentication-database}")
    private String authenticationDatabase;

    @Value("${spring.data.mongodb.database}")
    private String dataName;

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.port}")
    private String port;
    private static MongoClient mongoClient;

//    @Autowired
//    private MongoClient mongoClient;
//    @Autowired
//    private MongoProperties mongoProperties;

    /**
     * 连接或创建数据集
     *
     * @param collectionName
     * @return
     */

    private synchronized MongoCollection<Document> collection(String collectionName) {
        //解决连接失败 没有释放问题
        if(mongoClient==null){
            List<ServerAddress> serverAddresses = new ArrayList<>();
            String[] hosts = host.split(",");
            for(String host : hosts){
                serverAddresses.add(new ServerAddress(host, Integer.parseInt(port)));
            }

            MongoCredential credential = MongoCredential.createCredential(username, dataName, password.toCharArray());   //验证对象
            MongoClientOptions options = MongoClientOptions.builder().sslEnabled(false).build();     //连接操作对象
            mongoClient = new MongoClient(serverAddresses, Collections.singletonList(credential), options);   //连接对象
        }
        MongoDatabase database = mongoClient.getDatabase(dataName);
//        MongoDatabase database = mongoClient.getDatabase(mongoProperties.getDatabase());
        return database.getCollection(collectionName);
    }

    /**
     * 向指定数据集中插入文档
     *
     * @param document
     * @param collectionName
     */
    public void insert(Document document, String collectionName) {
        try {
            logger.info("插入数据到mongodb：{}",document.toJson());
            collection(collectionName).insertOne(document);
            logger.info("mongodb --- 向数据集" + collectionName + "----插入数据成功！");
        } catch (Exception e) {
            logger.error("mongodb --- 向数据集" + collectionName + "----插入数据失败:" + e);
        }
    }

    /**
     * 向指定数据集中批量插入文档
     *
     * @param documents
     * @param collectionName
     */
    public void insertPatch(List<Document> documents, String collectionName) {
        try {
            collection(collectionName).insertMany(documents);
            logger.info("mongodb --- 向数据集" + collectionName + "----批量插入数据成功！");
        } catch (Exception e) {
            logger.error("mongodb --- 向数据集" + collectionName + "----批量插入数据失败:" + e);
        }
    }

    /**
     * 修改
     *
     * @param collectionName 集合名称
     * @param filter         过滤器--指定要修改的数据
     * @param document       修改文档
     */
    public void update(String collectionName, Bson filter, Document document) {
        try {
            //修改单个文档
            collection(collectionName).updateOne(filter, document);
            logger.info("mongodb --- 向数据集" + collectionName + "----修改数据成功！");
        } catch (Exception e) {
            logger.error("mongodb --- 向数据集" + collectionName + "----修改数据失败:" + e);
        }
    }

    /**
     * 删除数据
     *
     * @param collectionName --集合名称
     * @param filter         --过滤器 --指定要删除的数据
     */
    public void delete(String collectionName, Bson filter) {
        try {
            collection(collectionName).deleteOne(filter);
            logger.info("mongodb --- 从数据集" + collectionName + "----删除数据成功！");
        } catch (Exception e) {
            logger.error("mongodb --- 从数据集" + collectionName + "----删除数据失败:" + e);
        }
    }

    /**
     * 批量删除
     *
     * @param collectionName
     * @param filter
     */
    public Long deletePatch(String collectionName, Bson filter) {
        try {
            DeleteResult deleteResult = collection(collectionName).deleteMany(filter);
            logger.info("mongodb --- 从数据集" + collectionName + "----批量删除数据成功！");
            return deleteResult.getDeletedCount();
        } catch (Exception e) {
            logger.error("mongodb --- 从数据集" + collectionName + "----批量删除数据失败:" + e);
        }
        return 0L;
    }

    /**
     * 查找集合中所有数据
     *
     * @param collectionName
     */
    @SuppressWarnings("rawtypes")
    public MongoCursor query(String collectionName, Document document) {
        //查找集合中的所有文档
        FindIterable findIterable = collection(collectionName).find().projection(document);
        MongoCursor cursor = findIterable.iterator();
        return cursor;
    }

    /**
     * 条件查询
     *
     * @param collectionName
     * @param filter
     */
    @SuppressWarnings("rawtypes")
    public MongoCursor filterQuery(String collectionName, Bson filter, Document document) {
        //指定查询过滤器查询
        FindIterable findIterable = collection(collectionName).find(filter).projection(document);
        MongoCursor cursor = findIterable.iterator();
        return cursor;
    }

    /**
     * 模糊查询
     *
     * @param collectionName
     * @param document       模糊查询过滤条件
     * @param document2      查询字段
     * @return
     */
    @SuppressWarnings("rawtypes")
    public MongoCursor fuzzyQuery(String collectionName, Document document, Document document2) {
        //指定查询过滤器查询
        FindIterable findIterable = collection(collectionName).find(document).projection(document2);
        MongoCursor cursor = findIterable.iterator();
        return cursor;
    }

    /**
     * 设置返回值
     * 0 表示 不返回 该字段, 1 表示 返回该字段
     *
     * @return
     */
    private Document fetchFields() {
        Document fetchFields = new Document();
        fetchFields.put("_id", 0);//查询的结果 不返回 _id 字段
        return fetchFields;
    }

}
