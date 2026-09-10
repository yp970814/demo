package yp970814.generate;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SggCodeGenerator {

    public static void main(String[] args) {
        //TODO 参数输入
        String tableName = "interface_management";//表名

        String packageName = "com.sungrow.integration";
        String url = "jdbc:mysql://127.0.0.1:3306/sungrowx?serverTimezone=GMT%2B8";

        String username = "admin";
        String password = "12345678";

        // 1、创建代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 2、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setOpen(false); // 生成后是否打开资源管理器
        gc.setFileOverride(false); // 重新生成时文件是否覆盖
        gc.setServiceName("%sService"); // 去掉Service接口的首字母I
        gc.setIdType(IdType.INPUT); // 主键策略
        gc.setDateType(DateType.ONLY_DATE);// 定义生成的实体类中日期类型
        gc.setSwagger2(true);// 开启Swagger2模式
        gc.setAuthor("sungrow");//作者

        mpg.setGlobalConfig(gc);

        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(url);
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername(username);
        dsc.setPassword(password);
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(null); // 模块名
        pc.setParent(packageName);
        pc.setController("rest");
        pc.setEntity("model.po");
        pc.setService("service");
        pc.setMapper("mapper");
        pc.setXml("mapper");
        mpg.setPackageInfo(pc);

        // 5、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setInclude(tableName);// 对那一张表生成代码
        strategy.setNaming(NamingStrategy.underline_to_camel);// 数据库表映射到实体的命名策略
        strategy.setTablePrefix(pc.getModuleName() + "_"); // 生成实体时去掉表前缀

        strategy.setColumnNaming(NamingStrategy.underline_to_camel);// 数据库表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作

        strategy.setRestControllerStyle(true); // restful api风格控制器
        strategy.setControllerMappingHyphenStyle(true); // url中驼峰转连字符
        strategy.setSuperControllerClass("BaseController");

        strategy.setLogicDeleteFieldName("deleted");  //设置逻辑删除字段名
        List<TableFill> list = Lists.newArrayList();
        TableFill createTime = new TableFill("create_time", FieldFill.INSERT);
        TableFill creator = new TableFill("creator", FieldFill.INSERT);
        TableFill updateTime = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        TableFill operator = new TableFill("operator", FieldFill.INSERT_UPDATE);
        TableFill deleted = new TableFill("deleted", FieldFill.INSERT);
        list.add(createTime);
        list.add(creator);
        list.add(updateTime);
        list.add(operator);
        list.add(deleted);
        strategy.setTableFillList(list);
        mpg.setStrategy(strategy);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("bo", packageName + ".model.bo");
                map.put("po", packageName + ".model.po");
                map.put("vo", packageName + ".model.vo");
                map.put("dto", packageName + ".model.dto");
                map.put("converter", packageName + ".converter");
                this.setMap(map);

            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/template/Mapper.xml.ftl";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会
                // 跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName()
                        + "Mapper" + StringPool.DOT_XML;
            }
        });
        //
        templatePath = "/template/EntityDTO.java.ftl";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {

                return projectPath + "/src/main/java/"+packageName.replace(".","/")+"/model/dto/" +  tableInfo.getEntityName()+"DTO.java";
            }
        });

        //
        templatePath = "/template/EntityVO.java.ftl";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {

                return projectPath + "/src/main/java/"+packageName.replace(".","/")+"/model/vo/" +  tableInfo.getEntityName()+"VO.java";
            }
        });

        templatePath = "/template/Converter.java.ftl";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {

                return projectPath + "/src/main/java/"+packageName.replace(".","/")+"/converter/" +  tableInfo.getEntityName()+"Converter.java";
            }
        });

        //
        /*templatePath = "/template/Entity.java.ftl";
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {

                return projectPath + "/src/main/java/"+packageName.replaceAll(".","/")+"/model/po/" +  tableInfo.getEntityName()+".java";
            }
        });*/

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        /**
         * 配置模板
         */
        TemplateConfig templateConfig = new TemplateConfig();

        templateConfig.setEntity("template/Entity.java");
        templateConfig.setService("template/Service.java");
        templateConfig.setServiceImpl("template/ServiceImpl.java");
        templateConfig.setController("template/Controller.java");
        templateConfig.setMapper("template/Mapper.java");
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        /**
         * 模板引擎
         */
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        // 6、执行
        mpg.execute();
    }

}
