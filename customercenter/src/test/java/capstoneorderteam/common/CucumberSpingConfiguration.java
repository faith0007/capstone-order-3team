package capstoneorderteam.common;


import capstoneorderteam.CustomercenterApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = { CustomercenterApplication.class })
public class CucumberSpingConfiguration {
    
}
