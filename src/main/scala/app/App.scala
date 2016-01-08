package app

// note an _ instead of {} would get everything

import java.io._

import app.apiutils.{ArticleUrls, WebPageTest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{GetObjectRequest, PutObjectRequest}
import com.typesafe.config.{Config, ConfigFactory}


object App {
  def main(args: Array[String]) {
    /*  This value stops the output file from being uploaded to S3 and instead stores it locally
    #####################    this should be set to false before merging!!!!################*/
    val iamTestingLocally = false
    /*#####################################################################################*/


    //  Define names of s3bucket, configuration and output Files
    val s3BucketName = "capi-wpt-querybot"
    val configFileName = "config.conf"
    val outputFileName = "liveBlogPerformanceData.csv"
    //  Initialize results string - this will be used to acculate the results from each test so that only one write to file is needed.
    var resultsString: String = "Article Url, Time to First Paint, Time to Document Complete, Time to Fully Loaded, Speed Index \n"
    //  Define s3Client to all access to config file and enable uploading of results to S3
    val s3Client = new AmazonS3Client()
    //  Retrieve configuration from S3 bucket
    val conf = getS3Config(s3Client, s3BucketName, configFileName)
    val contentApiKey: String = conf.getString("content.api.key")
    val wptBaseUrl: String = conf.getString("wpt.api.baseUrl")
    val wptApiKey: String = conf.getString("wpt.api.key")
    //  Obtain a list of urls from the content API
    val articleUrlList = new ArticleUrls(contentApiKey)
    //  For each url in the list send a test request to the webpagetest API, and follow the resulting url to get the results
    val articleurls: List[String] = articleUrlList.getUrls
    if (articleurls.isEmpty)
      println("no results returned")
    else {
      val testResults: List[String] = articleurls.map(url => testUrl(url, wptBaseUrl, wptApiKey))
      println(testResults)
      resultsString = resultsString.concat(testResults.mkString)
      println("resultsString with results: \n" + resultsString)
    }
    if (!iamTestingLocally) {
      System.out.println("Uploading a new object to S3 from a file\n")
      s3Client.putObject(new PutObjectRequest(s3BucketName, outputFileName, createOutputFile(outputFileName, resultsString)));
    }
    else {
      val output: FileWriter = new FileWriter(outputFileName)
      println("Final Results:\n" + resultsString)
      output.write(resultsString)
      output.close()
    }
    println(resultsString)
  }


  def getS3Config(s3Client: AmazonS3Client, bucketName: String, configFileName: String): Config = {
    //    val s3Client = new AmazonS3Client()
    val s3Object = s3Client.getObject(new GetObjectRequest(bucketName, configFileName))
    val objectData = s3Object.getObjectContent
    val configString = scala.io.Source.fromInputStream(objectData).mkString
    val conf = ConfigFactory.parseString(configString)
    conf
  }

  def createOutputFile(fileName: String, content: String): File = {
    val file: File = File.createTempFile(fileName.takeWhile(_ != '.'), fileName.dropWhile(_ != '.'))
    file.deleteOnExit()
    val writer: Writer = new OutputStreamWriter(new FileOutputStream(file))
    writer.write(content)
    writer.close()
    file
  }

  def testUrl(url: String, wptBaseUrl: String, wptApiKey: String): String = {
    var returnString: String = url + ", "
    //  Define new web-page-test API request and send it the url to test
    val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
    val webPageTestResults: webpageTest.ResultElement = webpageTest.test(url)
    //  Add results to string which will eventually become the content of our results file
    returnString = returnString.concat(webPageTestResults.toString() + "\n")
    returnString
  }
}
// Done! - once this is done, use code in example here: https://github.com/aws/aws-sdk-java/blob/master/src/samples/AmazonS3/S3Sample.java to write to S3

// todo - weird future handling nees to be fixed
// todo - how to schedule job - ask Dom about adding as AWS Lamba function - needs to build into a .jar file to work in Lamda - Will catch up with Dom again to look at this
// todo - add running multiple tests on different connection speeds for each page
// todo - look again at what is the most useful data to surface - think of your audience
// todo - capi queries beyond a certain size will time out - need to be able to handle this - ie split query into two or something
// todo - get local instance of webpagetest serving api - get appropriate key
// todo - look at alerting functionality - possibly use event monitoring tools in AWS?
// todo - refactor - clean all the ugly! - use futures to try and make this more efficient
