package cn.yznu.vms.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.sql.Types;
import java.util.Collections;
import java.util.Scanner;

/**
 * VMS 代码生成器
 * <p>
 * 使用方法：
 * 1. 确保数据库 vms_cloud 已创建并执行了 vmscloud.sql
 * 2. 运行此类的 main 方法
 * 3. 按提示输入要生成的模块和表名
 * 4. 生成的代码在对应模块的 src/main/java 目录下
 * <p>
 * 表与模块对应关系：
 * - vms-video: video, video_episode, category, tag, video_tag_rel
 * - vms-interaction: comment, user_favorite, user_like, watch_history
 * - vms-operation: banner
 * - vms-file: 无实体表，仅处理文件上传
 */
public class VmsCodeGenerator {

    // ==================== 数据库配置 ====================
    // ⚠️ 使用前请修改为实际的数据库连接信息
    private static final String DB_URL = "jdbc:mysql://localhost:3306/vms_cloud?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "your_password"; // 请修改为实际密码

    // ==================== 项目根目录 ====================
    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("       VMS 代码生成器");
        System.out.println("========================================");
        System.out.println();
        System.out.println("可选模块：");
        System.out.println("  1. vms-video (视频服务)");
        System.out.println("  2. vms-interaction (互动服务)");
        System.out.println("  3. vms-operation (运营服务)");
        System.out.println();

        System.out.print("请输入模块名称 (如 vms-video): ");
        String moduleName = scanner.nextLine().trim();

        System.out.print("请输入要生成的表名 (多个用逗号分隔，如 video,category,tag): ");
        String tables = scanner.nextLine().trim();

        if (moduleName.isEmpty() || tables.isEmpty()) {
            System.out.println("模块名或表名不能为空！");
            return;
        }

        // 解析包名后缀 (vms-video -> video)
        String packageSuffix = moduleName.replace("vms-", "");

        // 输出路径
        String outputDir = PROJECT_ROOT + "/" + moduleName + "/src/main/java";
        String mapperXmlDir = PROJECT_ROOT + "/" + moduleName + "/src/main/resources/mapper";

        System.out.println();
        System.out.println("生成配置：");
        System.out.println("  模块: " + moduleName);
        System.out.println("  表名: " + tables);
        System.out.println("  输出目录: " + outputDir);
        System.out.println("  Mapper XML: " + mapperXmlDir);
        System.out.println();

        System.out.print("确认生成？(y/n): ");
        String confirm = scanner.nextLine().trim();
        if (!"y".equalsIgnoreCase(confirm)) {
            System.out.println("已取消生成。");
            return;
        }

        // 执行生成
        generate(outputDir, mapperXmlDir, packageSuffix, tables.split(","));

        System.out.println();
        System.out.println("========================================");
        System.out.println("代码生成完成！");
        System.out.println("========================================");
        System.out.println();
        System.out.println("生成的文件结构：");
        System.out.println("  " + moduleName + "/src/main/java/cn.yznu.vms/" + packageSuffix + "/");
        System.out.println("    ├── entity/      # 实体类");
        System.out.println("    ├── mapper/      # Mapper 接口");
        System.out.println("    ├── service/     # Service 接口");
        System.out.println("    └── service/impl/ # Service 实现");
        System.out.println();
        System.out.println("注意：Controller 需要手动创建，参考 vms-user 模块");
    }

    /**
     * 执行代码生成
     */
    private static void generate(String outputDir, String mapperXmlDir, String packageSuffix, String[] tables) {
        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("VMS Generator")       // 作者
                            .outputDir(outputDir)         // 输出目录
                            .disableOpenDir()             // 不自动打开目录
                            .enableSpringdoc();           // 启用 Springdoc 注解
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("cn.yznu.vms")         // 父包名
                            .moduleName(packageSuffix)    // 模块名
                            .entity("entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml, mapperXmlDir
                            ));
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(tables)            // 要生成的表

                            // Entity 策略
                            .entityBuilder()
                            .enableLombok()               // 启用 Lombok
                            .enableTableFieldAnnotation() // 启用字段注解
                            .enableRemoveIsPrefix()       // 移除 is 前缀
                            .logicDeleteColumnName("deleted") // 逻辑删除字段

                            // Mapper 策略
                            .mapperBuilder()
                            .enableMapperAnnotation()     // 启用 @Mapper 注解

                            // Service 策略
                            .serviceBuilder()
                            .formatServiceFileName("%sService")     // 去掉 I 前缀
                            .formatServiceImplFileName("%sServiceImpl");
                })
                // 数据类型映射
                .dataSourceConfig(builder -> {
                    builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                        int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                        if (typeCode == Types.TINYINT) {
                            return DbColumnType.BYTE;     // tinyint -> Byte
                        }
                        return typeRegistry.getColumnType(metaInfo);
                    });
                })
                // 模板引擎
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}
