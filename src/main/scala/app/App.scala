package app

// note an _ instead of {} would get everything

import java.io.FileWriter

import app.apiutils.{ArticleUrls, WebPageTest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext.Implicits.global


object App {
  def main(args: Array[String]) {
//  Define location of configuration and output Files
    val s3Bucket = "capi-wpt-querybot"
    val configFileName = "config.conf"
    val output: FileWriter = new FileWriter("results.csv")
    var resultsString: String = "Article Url, Time to First Paint, Time to Document Complete, Time to Fully Loaded, Speed Index \n"
//  Retrieve configuration from S3 bucket
    val conf = getS3Config(s3Bucket, configFileName)
    val contentApiKey:String = conf.getString("content.api.key")
    val wptBaseUrl:String = conf.getString("wpt.api.baseUrl")
    val wptApiKey:String = conf.getString("wpt.api.key")
//  Obtain a list of urls from the content API
    val articleUrlList = new ArticleUrls(contentApiKey)
//  For each url in the list send a test request to the webpagetest API, and follow the resulting url to get the results
    articleUrlList.getUrls map { urlList => for (url <- urlList ) yield {
            println("url: " + url)
            resultsString = resultsString.concat(url + ", ")
//          Define new web-page-test API request
            val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
//          Test the url returned from CAPI
            val webPageTestResults: webpageTest.ResultElement = webpageTest.test(url)
            println("test results: " + webPageTestResults.toStringList())
            resultsString = resultsString.concat(webPageTestResults.toString() + "\n")
          }
      //write out results
      println("Final Results:\n" + resultsString)
      output.write(resultsString)
      output.close()
    }
  }

  def getS3Config(bucketName: String, configFileName: String): Config = {
    val s3Client = new AmazonS3Client()
    val s3Object = s3Client.getObject(new GetObjectRequest( bucketName, configFileName))
    val objectData = s3Object.getObjectContent
    val configString = scala.io.Source.fromInputStream(objectData).mkString
    val conf = ConfigFactory.parseString(configString)
    conf
  }
}

// todo - change code to concatenate results to one big string in the loop then write out once at the end
// todo - once this is done, use code in example here: https://github.com/aws/aws-sdk-java/blob/master/src/samples/AmazonS3/S3Sample.java to write to S3
// todo - local version is fine but need to get libraries and build working in version cloned from github
// todo - how to store and present data - ie output to S3 when running on AWS then acces via web page - td 1 and 2 are the start of this
// todo - how to schedule job - ask Dom about adding as AWS Lamba function - needs to build into a .jar file to work in Lamda - Will catch up with Dom again to look at this
// todo - add running multiple tests on different connection speeds for each page
// todo - look again at what is the most useful data to surface - think of your audience
// todo - capi queries beyond a certain size will time out - need to be able to handle this - ie split query into two or something
// todo - get local instance of webpagetest serving api - get appropriate key
// todo - look at alerting functionality - possibly use event monitoring tools in AWS?
// todo - refactor - clean all the ugly! - use futures to try and make this more efficient
