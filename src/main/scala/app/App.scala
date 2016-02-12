package app

// note an _ instead of {} would get everything

import java.io._

import app.api.S3Operations
import app.apiutils._
import com.typesafe.config.{Config, ConfigFactory}
import org.joda.time.DateTime

import scala.io.Source


object App {
  def main(args: Array[String]) {
    /*  This value stops the forces the config to be read and the output file to be written locally rather than reading and writing from/to S3
    #####################    this should be set to false before merging!!!!################*/
    val iamTestingLocally = true
    /*#####################################################################################*/
    println("Job started at: " + DateTime.now)
    println("Local Testing Flag is set to: " + iamTestingLocally.toString)

    //  Define names of s3bucket, configuration and output Files
    val s3BucketName = "capi-wpt-querybot"
    val configFileName = "config.conf"
    val outputFileName = "liveBlogPerformanceData.html"
    val simpleOutputFileName = "liveBlogPerformanceDataExpurgated.html"
    val interactiveOutputFilename = "interactivePerformanceData.html"

    //  Initialize results string - this will be used to accumulate the results from each test so that only one write to file is needed.
    val averageColor: String = "\"grey\""
    val warningColor: String = "\"#FFFF00\""
    val alertColor = "\"#FF0000\""

    val htmlString = new HtmlStringOperations(averageColor, warningColor, alertColor)
    var simplifiedResults: String = htmlString.initialisePageForLiveblog + htmlString.initialiseTable
    var interactiveResults: String = htmlString.initialisePageForInteractive + htmlString.initialiseTable

    val listofLargeInteractives: List[String] = List("http://www.theguardian.com/us-news/2015/sep/01/moving-targets-police-shootings-vehicles-the-counted")
    val liveBlogItemlabel: String = "LiveBlog"
    val interactiveItemLabel: String = "Interactive"

    println("defining new S3 Client (this is done regardless but only used if 'iamTestingLocally' flag is set to false)")
    val s3Interface = new S3Operations(s3BucketName, configFileName)
    var configArray: Array[String] = Array("", "", "", "")

    //Get config settings
    println("Extracting configuration values")
    if (!iamTestingLocally) {
      println(DateTime.now + " retrieving config from S3 bucket: " + s3BucketName)
      configArray = s3Interface.getConfig
    }
    else {
      println(DateTime.now + " retrieving local config file: " + configFileName)
      val configReader = new LocalFileOperations
      configArray = configReader.readInConfig(configFileName)
    }
    println("checking validity of config values")
    if ((configArray(0).length < 1) || (configArray(1).length < 1) || (configArray(2).length < 1) || (configArray(3).length < 1)) {
      println("problem extracting config\n" +
        "contentApiKey: " + configArray(0) + "\n" +
        "wptBaseUrl: " + configArray(1) + "\n" +
        "wptApiKey: " + configArray(2) + "\n" +
        "wptLocation: " + configArray(3))
      System exit 1
    }
    println("config values ok")
    val contentApiKey: String = configArray(0)
    val wptBaseUrl: String = configArray(1)
    val wptApiKey: String = configArray(2)
    val wptLocation: String = configArray(3)

    //  Define new CAPI Query object
    val articleUrlList = new ArticleUrls(contentApiKey)
    val articleUrls: List[String] = articleUrlList.getLiveBlogUrls
    println(DateTime.now + " Closing Liveblog Content API query connection")
    articleUrlList.shutDown
    // check results returned from CAPI and extract data form liveblogs if there are any
    if (articleUrls.nonEmpty) {
      println("Combined results from LiveBLog CAPI calls")
      articleUrls.foreach(println)
      println("Generating average values for migrated liveblogs")
      val migratedLiveBlogAverages: PageAverageObject = new LiveBlogDefaultAverages
      simplifiedResults = simplifiedResults.concat(migratedLiveBlogAverages.toHTMLString)
      println("Performance testing liveblogs")
      // Send each article URL to the webPageTest API and obtain resulting data
      val testResults: List[Array[PerformanceResultsObject]] = articleUrls.map(url => {
        testUrl(url, wptBaseUrl, wptApiKey, wptLocation, migratedLiveBlogAverages)
      })
      val resultsList: List[String] = testResults.map(x => htmlString.generateHTMLRow("Desktop", x(0)) + htmlString.generateHTMLRow("Mobile", x(1)))
      simplifiedResults = simplifiedResults.concat(resultsList.mkString)
      println(DateTime.now + " Results added to accumulator string \n")
    }
    else {
      println(DateTime.now + " WARNING: No results returned from Content API for LiveBlog Queries")
      simplifiedResults = simplifiedResults.concat("<tr><th>No Liveblogs found to test</th></tr>")
    }

    //close off table in HTML string
    simplifiedResults = simplifiedResults + htmlString.closeTable
    // add closing decoration in HTML string so that page is complete
    simplifiedResults = simplifiedResults + htmlString.closePage

    //write liveblog results
    if (!iamTestingLocally) {
      println(DateTime.now + " Writing the following to S3:\n" + simplifiedResults + "\n")
      s3Interface.writeFileToS3(simpleOutputFileName, simplifiedResults)
    }
    else {
      val outputWriter = new LocalFileOperations
      val writeSuccess: Int = outputWriter.writeLocalResultFile(simpleOutputFileName, simplifiedResults)
      if (writeSuccess != 0) {
        println("problem writing local outputfile")
        System exit 1
      }
      println("LiveBlog Performance Test Complete")

      //  Define new CAPI Query object
      val interactiveUrlList = new ArticleUrls(contentApiKey)
      //  Request a list of urls from Content API
      val interactiveUrls: List[String] = interactiveUrlList.getInteractiveUrls
      println(DateTime.now + " Closing Interactive Content API query connection")
      interactiveUrlList.shutDown
      if (interactiveUrls.isEmpty) {
        println(DateTime.now + " WARNING: No results returned from Content API for Interactive Query")
        interactiveResults = interactiveResults.concat("<tr><th>No Interactives found to test</th></tr>")
      }
      else {
        // Send each article URL to the webPageTest API and obtain resulting data
        println("Results from Interactive CAPI calls")
        interactiveUrls.foreach(println)
        println("Generating average values for migrated Interactives")
        val averageInteractivesPerformance: PageAverageObject = generatePageAverages(listofLargeInteractives, wptBaseUrl, wptApiKey, wptLocation, interactiveItemLabel)
        interactiveResults = interactiveResults.concat(averageInteractivesPerformance.toHTMLString)

        val interactiveTestResults: List[Array[PerformanceResultsObject]] = articleUrls.map(url => {
          testUrl(url, wptBaseUrl, wptApiKey, wptLocation, averageInteractivesPerformance)
        })
        val simplifiedInteractiveResultsList: List[String] = interactiveTestResults.map(x => htmlString.generateHTMLRow("Desktop", x(0)) + htmlString.generateHTMLRow("Mobile", x(1)))
        interactiveResults = interactiveResults.concat(simplifiedInteractiveResultsList.mkString)
        println(DateTime.now + " Results added to accumulator string \n")
      }
      interactiveResults = interactiveResults.concat(htmlString.closeTable + htmlString.closePage)

      if (!iamTestingLocally) {
        println(DateTime.now + " Writing the following to S3:\n" + interactiveResults + "\n")
        s3Interface.writeFileToS3(interactiveOutputFilename, interactiveResults)
        s3Interface.closeS3Client()
      }
      else {
        val interactiveOutput: FileWriter = new FileWriter(interactiveOutputFilename)
        println(DateTime.now + " Writing the following to local file: " + interactiveOutputFilename + ":\n" + interactiveResults)
        interactiveOutput.write(interactiveResults)
        interactiveOutput.close()
        println(DateTime.now + " Writing to file: " + interactiveOutputFilename + " complete. \n")
      }
      println(DateTime.now + " Job complete")
    }
  }

  def testUrl(url: String, wptBaseUrl: String, wptApiKey: String, wptLocation: String, averages: PageAverageObject): Array[PerformanceResultsObject] = {
    //  Define new web-page-test API request and send it the url to test
    println(DateTime.now + " creating new WebPageTest object with this base URL: " + wptBaseUrl)
    val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
    println(DateTime.now + " calling methods to test url: " + url + " on desktop")
    val webPageDesktopTestResults: PerformanceResultsObject = webpageTest.desktopChromeCableTest(url)
    println(DateTime.now + " calling methods to test url: " + url + " on emulated 3G mobile")
    val webPageMobileTestResults: PerformanceResultsObject = webpageTest.mobileChrome3GTest(url, wptLocation)
    //  Add results to string which will eventually become the content of our results file
    if((webPageDesktopTestResults.timeDocComplete/1000 >= averages.desktopTimeDocComplete80thPercentile) ||
      (webPageDesktopTestResults.bytesInFullyLoaded/1000 >= averages.desktopKBInFullyLoaded80thPercentile) ||
      (webPageDesktopTestResults.estUSPrePaidCost >= averages.desktopEstUSPrePaidCost80thPercentile) ||
      (webPageDesktopTestResults.estUSPostPaidCost >= averages.desktopEstUSPostPaidCost80thPercentile))
    {
      if((webPageDesktopTestResults.timeDocComplete/1000 >= averages.desktopTimeDocComplete) ||
        (webPageDesktopTestResults.bytesInFullyLoaded/1000 >= averages.desktopKBInFullyLoaded) ||
        (webPageDesktopTestResults.estUSPrePaidCost >= averages.desktopEstUSPrePaidCost) ||
        (webPageDesktopTestResults.estUSPostPaidCost >= averages.desktopEstUSPostPaidCost))
      {
        println("row should be red one of the items qualifies")
        webPageDesktopTestResults.warningStatus = true
        webPageDesktopTestResults.alertStatus = true
      }
      else {
        println("row should be yellow one of the items qualifies")
        webPageDesktopTestResults.warningStatus = true
        webPageDesktopTestResults.alertStatus = false
      }
    }
    else
    {
      println("all fields within size limits")
      webPageDesktopTestResults.warningStatus = false
      webPageDesktopTestResults.alertStatus = false
    }

    //checking if status of mobile test needs an alert
    if((webPageMobileTestResults.timeDocComplete/1000 >= averages.mobileTimeDocComplete80thPercentile) ||
      (webPageMobileTestResults.bytesInFullyLoaded/1000 >= averages.mobileKBInFullyLoaded80thPercentile) ||
      (webPageMobileTestResults.estUSPrePaidCost >= averages.mobileEstUSPrePaidCost80thPercentile) ||
      (webPageMobileTestResults.estUSPostPaidCost >= averages.mobileEstUSPostPaidCost80thPercentile))
    {
      if((webPageMobileTestResults.timeDocComplete/1000 >= averages.mobileTimeDocComplete) ||
        (webPageMobileTestResults.bytesInFullyLoaded/1000 >= averages.mobileKBInFullyLoaded) ||
        (webPageMobileTestResults.estUSPrePaidCost >= averages.mobileEstUSPrePaidCost) ||
        (webPageMobileTestResults.estUSPostPaidCost >= averages.mobileEstUSPostPaidCost))
      {
        println("warning and alert statuses set to true")
        webPageMobileTestResults.warningStatus = true
        webPageMobileTestResults.alertStatus = true
      }
      else {
        println("warning status set to true")
        webPageMobileTestResults.warningStatus = true
        webPageMobileTestResults.alertStatus = false
      }
    }
    else
    {
      println("all fields within size limits - both warning and alert status set to false")
      webPageMobileTestResults.warningStatus = false
      webPageMobileTestResults.alertStatus = false
    }
    println("Returning desktop and mobile results")
    Array(webPageDesktopTestResults, webPageMobileTestResults)
  }


    def generatePageAverages(urlList: List[String], wptBaseUrl: String, wptApiKey: String, wptLocation: String, itemtype: String): PageAverageObject = {
      val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)

      val resultsList: List[Array[PerformanceResultsObject]] = urlList.map(url => {
        val webPageDesktopTestResults: PerformanceResultsObject = webpageTest.desktopChromeCableTest(url)
        val webPageMobileTestResults: PerformanceResultsObject = webpageTest.mobileChrome3GTest(url, wptLocation)
        val combinedResults = Array(webPageDesktopTestResults, webPageMobileTestResults)
        combinedResults
      })

      val pageAverages: PageAverageObject = new GeneratedPageAverages(resultsList)
      pageAverages
    }

}


