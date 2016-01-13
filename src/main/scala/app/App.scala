package app

// note an _ instead of {} would get everything

import java.io._

import app.apiutils.{ArticleUrls, WebPageTest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GetObjectRequest, PutObjectRequest}
import com.typesafe.config.{Config, ConfigFactory}
import org.joda.time.DateTime
import scala.io.Source


object App {
  def main(args: Array[String]) {
    /*  This value stops the forces the config to be read and the output file to be written locally rather than reading and writing from/to S3
    #####################    this should be set to false before merging!!!!################*/
    val iamTestingLocally = false
    /*#####################################################################################*/
    println("Job started at: " + DateTime.now)
    println("Local Testing Flag is set to: " + iamTestingLocally.toString)

    //  Define names of s3bucket, configuration and output Files
    val s3BucketName = "capi-wpt-querybot"
    val configFileName = "config.conf"
    val outputFileName = "liveBlogPerformanceData.csv"
    //  Initialize results string - this will be used to accumulate the results from each test so that only one write to file is needed.
    var resultsString: String = "Article Url, Test Type, Time to First Paint, Time to Document Complete, kB transferred at Document Complete, Time to Fully Loaded, kB transferred at Fully Loaded, Speed Index, Status\n"
    var contentApiKey: String = ""
    var wptBaseUrl: String = ""
    var wptApiKey: String = ""
    println("defining new S3 Client (this is done regardless but only used if 'iamTestingLocally' flag is set to false)")
    val s3Client = new AmazonS3Client()
    if(!iamTestingLocally) {
      println(DateTime.now + " retrieving config from S3 bucket: " + s3BucketName)
      val conf = getS3Config(s3Client, s3BucketName, configFileName)
      contentApiKey = conf.getString("content.api.key")
      wptBaseUrl = conf.getString("wpt.api.baseUrl")
      wptApiKey = conf.getString("wpt.api.key")
      if ((contentApiKey.length > 0) && (wptBaseUrl.length > 0) && (wptApiKey.length > 0))
        println(DateTime.now +" Config retrieval successful. \n You are using the following webpagetest instance: " + wptBaseUrl)
      else {
        println(DateTime.now + " ERROR: Problem retrieving config file - one or more parameters not retrieved")
        System.exit(1)
      }
    } else
      {
            println(DateTime.now + " retrieving local config file: " + configFileName)
            for (line <- Source.fromFile(configFileName).getLines()) {
              if (line.contains("content.api.key")) {
                println("capi key found")
                contentApiKey = line.takeRight((line.length - line.indexOf("=")) - 1)
              }
              if (line.contains("wpt.api.baseUrl")) {
                println("wpt url found")
                wptBaseUrl = line.takeRight((line.length - line.indexOf("=")) - 1)
              }
              if (line.contains("wpt.api.key")) {
                println("wpt api key found")
                wptApiKey = line.takeRight((line.length - line.indexOf("=")) - 1)
              }
            }
              if ((contentApiKey.length > 0) && (wptBaseUrl.length > 0) && (wptApiKey.length > 0)){
                println(DateTime.now + " Config retrieval successful. \n You are using the following webpagetest instance: " + wptBaseUrl)}
              else {
                println(DateTime.now + "ERROR: Problem retrieving config file - one or more parameters not retrieved")
                println(contentApiKey + "," + wptBaseUrl + "," + wptApiKey)
                System.exit(1)
              }
      }

    //  Define new CAPI Query object
    val articleUrlList = new ArticleUrls(contentApiKey)
    //  Request a list of urls from Content API
    val articleUrls: List[String] = articleUrlList.getUrls
    if (articleUrls.isEmpty) {
      println(DateTime.now + " WARNING: No results returned from Content API")
    }
    else {
            // Send each article URL to the webPageTest API and obtain resulting data
            val testResults: List[String] = articleUrls.map(url => testUrl(url, wptBaseUrl, wptApiKey))
            // Add results to a single string so that we only need ot write to S3 once (S3 will only take complete objects).
            resultsString = resultsString.concat(testResults.mkString)
            println(DateTime.now + " Final results: \n" + resultsString)
        }
    println(DateTime.now + " Closing Content API query connection")
    articleUrlList.shutDown
    if (!iamTestingLocally) {
      println(DateTime.now + " Writing the following to S3:\n" + resultsString)
      s3Client.putObject(new PutObjectRequest(s3BucketName, outputFileName, createOutputFile(outputFileName, resultsString)));
    }
    else {
      val output: FileWriter = new FileWriter(outputFileName)
      println(DateTime.now + " Writing the following to local file " + outputFileName + ":\n" + resultsString)
      output.write(resultsString)
      output.close()
      println(DateTime.now + " Writing to file: " + outputFileName + " complete. \n")
    }
    println(DateTime.now + " The following records written to " + outputFileName + ":\n" + resultsString)
    println(DateTime.now + " Job complete")
  }


  def getS3Config(s3Client: AmazonS3Client, bucketName: String, configFileName: String): Config = {
    println("Obtaining configfile: " + configFileName + " from S3")
    val s3Object = s3Client.getObject(new GetObjectRequest(bucketName, configFileName))
    val objectData = s3Object.getObjectContent
    println("Converting to string")
    val configString = scala.io.Source.fromInputStream(objectData).mkString
    println("calling parseString on ConfigFactory object")
    val conf = ConfigFactory.parseString(configString)
    println("returning config object")
    conf
  }

  def createOutputFile(fileName: String, content: String): File = {
    println("creating output file")
    val file: File = File.createTempFile(fileName.takeWhile(_ != '.'), fileName.dropWhile(_ != '.'))
    file.deleteOnExit()
    val writer: Writer = new OutputStreamWriter(new FileOutputStream(file))
    writer.write(content)
    writer.close()
    println("returning File object")
    file
  }

  def testUrl(url: String, wptBaseUrl: String, wptApiKey: String): String = {
    var returnString: String = url + ", "
    //  Define new web-page-test API request and send it the url to test
    println(DateTime.now + " creating new WebPageTest object with this base URL: " + wptBaseUrl)
    val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
    println(DateTime.now + " calling methods to test url: " + url + " on desktop")
    val webPageDesktopTestResults: webpageTest.ResultElement = webpageTest.desktopChromeCableTest(url)
    println(DateTime.now + " calling methods to test url: " + url + " on emulated 3G mobile")
    val webPageMobileTestResults: webpageTest.ResultElement = webpageTest.mobileChrome3GTest(url)
    //  Add results to string which will eventually become the content of our results file
    println(DateTime.now + " Adding results of desktop test to results string")
    returnString = returnString.concat("Desktop, " + webPageDesktopTestResults.toString() + "\n")
    println(DateTime.now + " Adding results of mobile test to results string")
    returnString = returnString.concat(", Android/3G, " + webPageMobileTestResults.toString() + "\n")
    println(DateTime.now + " returning results string to main thread")
    returnString
  }
}

