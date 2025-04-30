package com.asaki0019.enterprisemanagementsb.core.sysLogger;

import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class SysLoggerTest {

    private final SysLogger sysLogger;

    @Autowired
    public SysLoggerTest(SysLogger sysLogger) {
       this.sysLogger = sysLogger;
    }
    @BeforeAll
    public static void BeforeAll() {
        System.out.println("BeforeAll");
    }

    @AfterEach
    public void cleanLogFiles() {
        // 关闭Logback以释放文件句柄
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();

        // 删除所有匹配的日志文件
        File logDir = new File("logs");
        if (logDir.exists()) {
            File[] files = logDir.listFiles((dir, name) ->
                    name.startsWith("enterprise-management-sb.log")
            );
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
    @Test
    public void test() {
        sysLogger.info("test");
        sysLogger.warn("test");
        sysLogger.error("test", new RuntimeException("test"));
        sysLogger.debug("test");
        String loggerPath = "logs/enterprise-management-sb.log";
        var loggerFile = new File(loggerPath);
        assert loggerFile.exists();
    }

    @Test
    public void test2() {
        for(int i = 0; i < 10000; i++){
            sysLogger.info("test" + i);
        }
    }
}