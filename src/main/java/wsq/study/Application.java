package wsq.study;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableTransactionManagement
@MapperScan({"wsq.study.mapper"})
public class Application {
    static
    {
        //引入配置中心文件
        System.setProperty("spring.config.name", "wsq");
    }
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
