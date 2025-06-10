package com.example.codegenerator;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CodeGenerator {
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入").append(tip).append("：");
        System.out.println(help);
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * 数据源配置
     * @return
     */
    private static DataSourceConfig getDataSourceConfig() {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/demo1?allowPublicKeyRetrieval=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true&useAffectedRows=true");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");

        return dsc;
    }

    /**
     * 全局策略配置
     * @return
     */
    private static GlobalConfig getGlobalConfig(String projectPath) {
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("我叫小呆呆");
        gc.setOpen(false);
        gc.setFileOverride(true);
        gc.setEntityName("%s");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");

        return gc;
    }

    /**
     * 包名配置
     * @return
     */
    private static PackageConfig getPackageConfig(String moduleName) {
        PackageConfig pc = new PackageConfig();
        // 数据库业务的模块名
        pc.setModuleName(moduleName);
        // 生成代码所在的包名
        pc.setParent("com.example");

        return pc;
    }

    /**
     * 数据库表配置
     * @return
     */
    private static StrategyConfig getStrategyConfig() {
        StrategyConfig sc = new StrategyConfig();
        sc.setNaming(NamingStrategy.underline_to_camel);
        sc.setColumnNaming(NamingStrategy.underline_to_camel);
        sc.setEntityLombokModel(true);
        sc.setRestControllerStyle(true);
        sc.setInclude(scanner("表名，多个英文逗号分割").split(","));

        return sc;
    }

    /**
     * 配置模板
     * @return
     */
    private static TemplateConfig getTemplateConfig() {
        TemplateConfig tc = new TemplateConfig();
        tc.setEntity("customTemplates/entity.java"); // 会根据模板引擎识别后缀 这种方式只能读不能写 下面的自定义输出才没有这种
        tc.setService("customTemplates/service.java");
        tc.setMapper("customTemplates/mapper.java");
//        tc.setService(null);
//        tc.setMapper(null);
        tc.setController(null);
        tc.setServiceImpl(null);
        tc.setXml(null); // 放到自定义输出中配置

        return tc;
    }

    /**
     * 注入配置
     * @return
     */
    private static InjectionConfig getInjectionConfig(String projectPath, String moduleName) {
        // 自定义输出配置 自定义配置会被优先输出
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("customTemplates/mapper.xml.ftl") { // "customTemplates/mapper.xml.ftl" .ftl 后缀根据模版引擎调整
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        InjectionConfig ic = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
                // 自定义属性注入:abc，在.ftl(或者是.vm)模板中，通过${cfg.abc}获取属性
            }
        };
        ic.setFileOutConfigList(focList);

        return ic;
    }

    public static void main(String[] args) {
        String moduleName = scanner("模块名");
        String projectPath = System.getProperty("user.dir") + "/" + moduleName; // 获得当前模块项目的路径

        // *************构建配置*************
        DataSourceConfig dsc = getDataSourceConfig();
        GlobalConfig gc = getGlobalConfig(projectPath);
        PackageConfig pc = getPackageConfig(moduleName);
        StrategyConfig sc = getStrategyConfig();
        TemplateConfig tc = getTemplateConfig();
        InjectionConfig ic = getInjectionConfig(projectPath,  pc.getModuleName());

        // *************代码生成器*************
        AutoGenerator mpg = new AutoGenerator();
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());        // 默认VelocityTemplateEngine
        mpg.setDataSource(dsc);
        mpg.setGlobalConfig(gc);
        mpg.setPackageInfo(pc);
        mpg.setStrategy(sc);
        mpg.setTemplate(tc);
        mpg.setCfg(ic);

        mpg.execute();// 执行的时候会将前面的配置信息加入到ConfigBuilder再加入到同一个map中，对应模版文件中的占位符，最终输出目标文件
    }

}
