package testBase;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.apache.logging.log4j.LogManager; //Log4j
import org.apache.logging.log4j.Logger;		//Log4j
//import org.testng.annotations.Test;

//import pageObjects.HomePage;

public class BaseClass {

	public static WebDriver driver;
	public Logger logger;  //Log4j
	public Properties p;   //variable used to read "config.properties" file
	
	@SuppressWarnings("deprecation")
	@BeforeClass(groups= {"Sanity","Regression","Master"})
	@Parameters({"os", "browser"}) //Variables we get from master.xml 
	public void setup(String os, String br) throws IOException
	{
		//Loading config.properties file
		FileReader file=new FileReader("./src//tst//resoures//config.properties");  //typo  "tst" vs "test" in /src/test/resources
		//FileReader file=new FileReader("./src//test//resoures//config.properties");

		p=new Properties();
		p.load(file);  //Pass it a FileReader type to properties, "p"
		
		logger=LogManager.getLogger(this.getClass()); //"this", is whatever class called for example, TC_001_AccountRegistrationTest
		
		//REMOTE EXECUTION BEGIN
		//p.getProterty...of 
		if( (p.getProperty("execution_env").equalsIgnoreCase("local")) == false)
		{
			//execution_env=local from "config.properties" file
			DesiredCapabilities capabilities = new DesiredCapabilities();
			
			//os, we need to determine
			if(os.equalsIgnoreCase("windows"))
			{
				capabilities.setPlatform(Platform.WIN11);
			}
			else if(os.equalsIgnoreCase("linux"))
			{
				capabilities.setPlatform(Platform.LINUX);
			}
			else if(os.equalsIgnoreCase("mac"))
			{
				capabilities.setPlatform(Platform.MAC);
			}
			else
			{
				System.out.println("No matching os");
				return;
			} 

			//browser, we need to determine
			switch(br.toLowerCase())
			{
				case "chrome": capabilities.setBrowserName("chrome"); break;
				case "edge": capabilities.setBrowserName("MicrosoftEdge"); break;
				case "firefox": capabilities.setBrowserName("firefox"); break;
				default: System.out.println("No matching browser"); return;
			}
			//By running, java -jar selenium-server-4.31.0.jar standalone, the server is open at localhost.
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),capabilities);
			
		}
		// REMOTE EXECUTION END Browser and OS picked 
		
		if(p.getProperty("execution_env").equalsIgnoreCase("local"))
		{
			switch(br.toLowerCase())
			{
			case "chrome": driver=new ChromeDriver(); break;
			case "edge": driver=new EdgeDriver(); break;
			case "firefox": driver=new FirefoxDriver(); break;
			default : System.out.println("Invalid browser name..."); return;
			}
			
		}// LOCAL EXECUTION Browser picked 
		
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		
//	    URLs will be read from "config.properties" file vs hardcoding 
//		driver.get("http://localhost/opencart/upload/index.php");
//		driver.get("https://tutorialsninja.com/demo/"); 
		driver.get(p.getProperty("appURL2"));

		driver.manage().window().maximize();
	}
	
	@AfterClass(groups= {"Sanity","Regression","Master"})
	public void tearDown()
	{
		//driver.close();
		driver.quit();
	}
	
	//@Test
//	public void verift_account_registration()
//	{
//		HomePage hp=new HomePage(driver);
//		hp.clickMyAccount();
//		hp.clickRegister();
//		
//	}

	public String randomeString()
	{
		String generatedString=RandomStringUtils.randomAlphabetic(5);
		return generatedString;
	}
	
	public String randomeNumber()
	{
		String generatedString=RandomStringUtils.randomNumeric(10);
		return generatedString;
	}
	
	public String randomAlphaNumeric()
	{
		String str=RandomStringUtils.randomAlphabetic(3);
		String num=RandomStringUtils.randomNumeric(3);
		
		return (str+"@"+num);
	}
	
	public String captureScreen(String tname) throws IOException {

		String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
				
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
		
		String targetFilePath=System.getProperty("user.dir")+"\\screenshots\\" + tname + "_" + timeStamp + ".png";
		File targetFile=new File(targetFilePath);
		
		sourceFile.renameTo(targetFile);
			
		return targetFilePath;

	}

}
