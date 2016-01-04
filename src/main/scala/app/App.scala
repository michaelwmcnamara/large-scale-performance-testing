package app

// note an _ instead of {} would get everything

import java.io.FileWriter

import app.apiutils.{ArticleUrls, WebPageTest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source


object App {
  def main(args: Array[String]) {
    val configFile = "config.config"
    val resultsFile = "performanceResults.csv"
    val output:FileWriter = new FileWriter("results.txt")
    var contentApiKey:String = ""
    var wptBaseUrl:String = ""
    var wptApiKey:String = ""
    for (line <- Source.fromFile(configFile).getLines()){
      if (line.contains("content.api.key")){contentApiKey = line.takeRight((line.length - line.indexOf("=")) - 1) }
      if (line.contains("wpt.api.baseUrl")){wptBaseUrl = line.takeRight((line.length - line.indexOf("=")) - 1) }
      if (line.contains("wpt.api.key")){wptApiKey = line.takeRight((line.length - line.indexOf("=")) - 1) }
    }
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
      //output.flush()
    }
      output.close()
    }
  }
}

// todo - how to store and present data
// todo - how to schedule job
// todo - run multiple tests on different connection speeds for each page
// todo - look again at what is the most useful data to surface
// todo - refactor with better name
// todo - add to github
// todo - get local instance of webpagetest serving api - get appropriate key
// todo - look at alerting functionality
// todo - refactor - use futures to try and make this more efficient
// todo - queries beyond a certain size will time out - need to be able to handle this - ie split query into two or something