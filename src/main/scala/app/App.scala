package app

// note an _ instead of {} would get everything

import java.io.FileWriter

import app.apiutils.{ArticleUrls, WebPageTest}
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global


object App {
  def main(args: Array[String]) {
    val configFile = "config.conf"
    val resultsFile = "performanceResults.csv"
    val output:FileWriter = new FileWriter("results.txt")
    val s3Client = new AmazonS3Client()
    val s3Object = s3Client.getObject(new GetObjectRequest( "capi-wpt-querybot", "config.conf"))
    val objectData = s3Object.getObjectContent()
    val configString = scala.io.Source.fromInputStream(objectData).mkString
    val conf = ConfigFactory.parseString(configString)
    val contentApiKey:String = conf.getString("content.api.key")
    val wptBaseUrl:String = conf.getString("wpt.api.baseUrl")
    val wptApiKey:String = conf.getString("wpt.api.key")
    val articleUrlList = new ArticleUrls(contentApiKey)
    articleUrlList.getUrls map { urlList => for (url <- urlList ) yield {
      println("url: " + url)
      output.write("page url: " + url + "\n")
      output.write("Time to First Paint, Time to Document Complete, Time to Fully Loaded, Speed Index \n")
      val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
      val resultPage: String = webpageTest.sendPage(url)
      val testResults: webpageTest.ResultElement = webpageTest.getResults(resultPage)
      println("test results: " + testResults.toStringList())
      output.write(testResults.toString() + "\n")
    }
      output.close()
    }
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
