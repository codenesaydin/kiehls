package context.base;

import context.driver.DriverManager;
import context.driver.DriverWebTestFactory;
import context.helper.JSHelper;
import context.recorder.VideoRecorder;
import context.utils.ReportGenerate;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractWebTest extends AbstractLayoutDesignTest
{
    private static final Logger logger = Logger.getLogger(AbstractWebTest.class);

    private boolean takeVideo;
    protected JSHelper jsHelper;

    @Rule
    public final TestName testName = new TestName();

    @Rule
    public ReportGenerate reportGenerate = new ReportGenerate();

    @Rule
    public final TestRule watchman = new TestWatcher()
    {
        @Override
        protected void starting(org.junit.runner.Description description)
        {
            logger.info("=================================================================");
            logger.info("TEST STARTED ... → " + description.getMethodName());
            logger.info("=================================================================");
        }

        @Override
        public Statement apply(Statement base, org.junit.runner.Description description)
        {
            return super.apply(base, description);
        }

        @Override
        protected void succeeded(Description description)
        {
            logger.info("=================================================================");
            logger.info("TEST PASSED ...");
            logger.info("=================================================================");
        }

        @Override
        protected void failed(Throwable e, org.junit.runner.Description description)
        {
            createFolder(System.getProperty("user.dir") + "/target/PageSource");

            String file = System.getProperty("user.dir") + String.format("/target/PageSource/%s-DOM.txt", description.getMethodName());

            PrintWriter writer = null;

            try
            {
                writer = new PrintWriter(file, "UTF-8");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            writer.println(driver.getPageSource());
            writer.close();
            logger.info("TEST FAIL ... Fail Screen Page Source --> " + file);
        }

        @Override
        protected void finished(org.junit.runner.Description description)
        {
            if (driver != null)
            {
                driver.close();
                driver.quit();
                driver = null;
            }
        }
    };

    @Before
    public void init() throws Exception
    {
        init(false);
    }

    public void init(Boolean withProxy) throws Exception
    {
        DriverManager driverManager;
        driverManager = DriverWebTestFactory.getManager();

        if (withProxy)
        {
            Runtime.getRuntime().exec("browserstacklocal/BrowserStackLocal --key " + accessKey);

            proxy = new BrowserMobProxyServer();
            proxy.start();
            proxy.enableHarCaptureTypes(CaptureType.REQUEST_BINARY_CONTENT);
            proxy.newHar();
        }

        driver = driverManager.getDriver(withProxy);

        takeVideo = configuration.getTakeAVideo();

        if (takeVideo)
        {
            VideoRecorder.startRecording(testName.getMethodName());
        }
        else
        {
            logger.info("Scenarios will not take video");
        }

        jsHelper = new JSHelper(driver);

        session = (driver).getSessionId().toString();
        logger.info("=================================================================");
        logger.info("This Execute Session ID --> " + session);
        logger.info("=================================================================");
    }


    @After
    public void tearDown() throws Exception
    {
        //setHarFile(testName.getMethodName());
        takeVideo = configuration.getTakeAVideo();

        if (takeVideo) VideoRecorder.stopRecording();

        try
        {
            if (proxy != null) proxy.stop();
        }
        catch (IllegalStateException ex)
        {
            logger.info("Already Stopped Proxy");
        }
    }

    // --------

    private void setHarFile(String harFileName)
    {

        String sFileName = prop.getProperty("har.file.path") + harFileName + ".har";

        try
        {
            Har har = proxy.getHar();
            File harFile = new File(sFileName);
            try
            {
                har.writeTo(harFile);
            }
            catch (IOException ex)
            {
                logger.info("Could not find file " + sFileName);
                ex.printStackTrace();
            }
        }
        catch (Exception ex)
        {
        }
    }

}
