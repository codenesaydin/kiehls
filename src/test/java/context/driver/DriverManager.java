package context.driver;

import context.objects.Configuration;
import net.lightbody.bmp.BrowserMobProxy;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Properties;

public abstract class DriverManager
{
    protected static RemoteWebDriver driver;
    protected static BrowserMobProxy proxy;
    protected static Configuration configuration;
    public static String session;
    public static Properties prop;

    protected static final String accessKey = "FPqXDFz4bqk1jvqXDZ3d";
    protected static final Integer DEFAULT_WAIT_A_MOMENT_SECONDS = 2;
    protected static final Integer DEFAULT_WAIT_SECONDS = 5;
    protected static final Integer DEFAULT_WAIT_TOO_LONG_SECONDS = 15;

    protected void createDriver(Boolean withProxy) throws Exception
    {
    }

    public RemoteWebDriver getDriver(Boolean withProxy) throws Exception
    {
        if (driver == null)
        {
            prop = new Properties();
            prop.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));

            configuration = new Configuration();

            createDriver(withProxy);

            PropertyConfigurator.configure(this.getClass().getClassLoader().getResource("log4j.properties").getPath());
        }
        return driver;
    }
}
