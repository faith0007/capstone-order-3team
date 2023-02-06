package capstoneorderteam.main;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

import java.beans.Transient;

import org.junit.runner.RunWith;


@RunWith(Cucumber.class)
@CucumberOptions(plugin={"pretty","html:target/cucumber"},
                features = "src/test/resources/features",
                extraGlue="capstoneorderteam/common")
public class TestMain {

    public void test001(){

    };
}
