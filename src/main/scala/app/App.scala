package app

// note an _ instead of {} would get everything

import java.io._
import java.util

import app.api.{ResultsSummary, WptResultPageListener, S3Operations}
import app.apiutils._
import com.typesafe.config.{Config, ConfigFactory}
import org.joda.time.DateTime
import sbt.complete.Completion

import scala.collection.parallel.immutable.ParSeq
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
    val amazonDomain = "https://s3-eu-west-1.amazonaws.com"
    val s3BucketName = "capi-wpt-querybot"
    val configFileName = "config.conf"
    val emailFileName = "addresses.conf"
    val interactiveSampleFileName = "interactivesamples.conf"

/*    val outputFileName = "liveBlogPerformanceData.html"
    val simpleOutputFileName = "liveBlogPerformanceDataExpurgated.html"
    val interactiveOutputFilename = "interactivePerformanceData.html"
    val videoOutputFilename =  "videoPerformanceData.html"
    val audioOutputFilename = "audioPerformanceData.html"
    val frontsOutputFilename = "frontsData.html"

    val liveBlogResultsUrl: String = amazonDomain + "/" + s3BucketName + "/" + simpleOutputFileName
    val interactiveResultsUrl: String = amazonDomain + "/" + s3BucketName + "/" + interactiveOutputFilename
    val frontsResultsUrl: String = amazonDomain + "/" + s3BucketName + "/" + frontsOutputFilename
*/
    val articleCSVName = "accumulatedarticleperformancedata.csv"
    val liveBlogCSVName = "accumulatedliveblogperformancedata.csv"
    val interactiveCSVName = "accumulatedinteractiveperformancedata.csv"
    val videoCSVName = "accumulatedvideoperformancedata.csv"
    val audioCSVName = "accumulatedaudioperformancedata.csv"
    val frontsCSVName = "accumulatedfrontsperformancedata.csv"
    val summaryCSVFilename = "summaryofaccumulatedperformancedata.csv"



    //Define colors to be used for average values, warnings and alerts
    val averageColor: String = "\"#d9edf7\""
    val warningColor: String = "\"#fcf8e3\""
    val alertColor: String = "\"#f2dede\""

    //  Initialize results string - this will be used to accumulate the results from each test so that only one write to file is needed.
/*    val htmlString = new HtmlStringOperations(averageColor, warningColor, alertColor, liveBlogResultsUrl, interactiveResultsUrl, frontsResultsUrl)
    var liveBlogResults: String = htmlString.initialisePageForLiveblog + htmlString.interactiveTable
    var interactiveResults: String = htmlString.initialisePageForInteractive + htmlString.initialiseTable
    var frontsResults: String = htmlString.initialisePageForFronts + htmlString.initialiseTable

    //Initialize email alerts string - this will be used to generate emails
    var liveBlogAlertList: List[PerformanceResultsObject] = List()
    var interactiveAlertList: List[PerformanceResultsObject] = List()
    var frontsAlertList: List[PerformanceResultsObject] = List()


    var liveBlogAlertMessageBody: String = ""
    var interactiveAlertMessageBody: String = ""
    var frontsAlertMessageBody: String = ""
    
    val interactiveItemLabel: String = "Interactive"
*/
    val csvHeaders: String = "Url, Time of Test, Type of Test, Ads Displayed, Result Status, Time to First Paint (ms), Time to Doc Complete (ms), Bytes In Doc Complete (ms), timeFullyLoaded (ms), Bytes In Fully Loaded (ms), Speed Index (ms), Full Results, Element1 - Resource, Element1 - ContentType, Element1 - Bytes Downloaded,  Element2 - Resource, Element2 - ContentType, Element2 - Bytes Downloaded,  Element3 - Resource, Element3 - ContentType, Element3 - Bytes Downloaded,  Element4 - Resource, Element4 - ContentType, Element4 - Bytes Downloaded,  Element5 - Resource, Element5 - ContentType, Element5 - Bytes Downloaded"
    val summaryHeaders: String = "Content Type, Ads Displayed, Avg Time to First Paint (ms), Avg Time to Doc Complete (ms), Avg Bytes In Doc Complete (ms), Avg timeFullyLoaded (ms), Avg Bytes In Fully Loaded (ms), Avg Speed Index (ms)"
    var articleCSVResults: String = csvHeaders
    var liveBlogCSVResults: String = csvHeaders
    var interactiveCSVResults: String = csvHeaders
    var videoCSVResults: String = csvHeaders
    var audioCSVResults: String = csvHeaders
    var frontsCSVResults: String = csvHeaders

    var fullsummaryCSVResults: String = ""
    var articleSummaryCSV: String = ""
    var liveBlogSummaryCSV: String = ""
    var interactiveSummaryCSV: String = ""
    var videoSummaryCSV: String = ""
    var audioSummaryCSV: String = ""
    var frontsSummaryCSV: String = ""


    //Create new S3 Client
    println("defining new S3 Client (this is done regardless but only used if 'iamTestingLocally' flag is set to false)")
    val s3Interface = new S3Operations(s3BucketName, configFileName, emailFileName)
    var configArray: Array[String] = Array("", "", "", "", "", "")
    var urlFragments: List[String] = List()

    //Get config settings
    println("Extracting configuration values")
    if (!iamTestingLocally) {
     println(DateTime.now + " retrieving config from S3 bucket: " + s3BucketName)
      val returnTuple = s3Interface.getConfig
      configArray = Array(returnTuple._1,returnTuple._2,returnTuple._3,returnTuple._4,returnTuple._5,returnTuple._6,returnTuple._7)
      urlFragments = returnTuple._8
    }
     else {
      println(DateTime.now + " retrieving local config file: " + configFileName)
      val configReader = new LocalFileOperations
      configArray = configReader.readInConfig(configFileName)
    }
    println("checking validity of config values")
    if ((configArray(0).length < 1) || (configArray(1).length < 1) || (configArray(2).length < 1) || (configArray(3).length < 1)) {
      println("problem extracting config\n" +
        "contentApiKey length: " + configArray(0).length + "\n" +
        "wptBaseUrl length: " + configArray(1).length + "\n" +
        "wptApiKey length: " + configArray(2).length + "\n" +
        "wptLocation length: " + configArray(3).length + "\n" +
        "emailUsername length: " + configArray(4).length + "\n" +
        "emailPassword length: " + configArray(5).length)
      System exit 1
    }
    println("config values ok")
    val contentApiKey: String = configArray(0)
    val wptBaseUrl: String = configArray(1)
    val wptApiKey: String = configArray(2)
    val wptLocation: String = configArray(3)
    val emailUsername: String = configArray(4)
    val emailPassword: String = configArray(5)

/*    //obtain list of email addresses for alerting
    val emailAddresses: Array[List[String]] = s3Interface.getEmailAddresses
    val generalAlertsAddressList: List[String] = emailAddresses(0)
    val interactiveAlertsAddressList: List[String] = emailAddresses(1)*/

    //obtain list of interactive samples to determine average size
    val listofLargeInteractives: List[String] = s3Interface.getUrls(interactiveSampleFileName)


    //Create Email Handler class
    //val emailer: EmailOperations = new EmailOperations(emailUsername, emailPassword)

    //  Define new CAPI Query object
    val capiQuery = new ArticleUrls(contentApiKey)
    //get all content-type-lists
    val articleUrls: List[String] = capiQuery.getArticleUrls
    val liveBlogUrls: List[String] = capiQuery.getMinByMinUrls
    val interactiveUrls: List[String] = capiQuery.getInteractiveUrls
    val videoUrls: List[String] = capiQuery.getVideoUrls
    val audioUrls: List[String] = capiQuery.getAudioUrls
    val frontsUrls: List[String] = capiQuery.getFrontsUrls
    println(DateTime.now + " Closing Content API query connection")
    capiQuery.shutDown


    // send all urls to webpagetest at once to enable parallel testing by test agents
    val urlsToSend: List[String] = (articleUrls ::: liveBlogUrls ::: interactiveUrls ::: frontsUrls).distinct
    //val urlsToSend: List[String] = (articleUrls).distinct
    println("Combined list of urls: \n" + urlsToSend)
    val resultUrlList: List[(String, String, Boolean)] = getResultPages(urlsToSend, wptBaseUrl, wptApiKey, wptLocation)

    if (articleUrls.nonEmpty) {
      println("Generating average values for articles")
      val articleResultsList = listenForResultPages(articleUrls, "article", resultUrlList, wptBaseUrl, wptApiKey, wptLocation)
      val articleCSVList: List[String] = articleResultsList.map(x => x.toCSVString())
      val articleAverageResults = new ResultsSummary(articleResultsList)
      articleSummaryCSV = articleAverageResults.generateCSVResultsTable("Article")
      // write article results to string
      articleCSVResults = articleCSVResults.concat(articleCSVList.mkString)
      //write article results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing article results to S3")
        s3Interface.writeFileToS3(articleCSVName, articleCSVResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(articleCSVName, articleCSVResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("article Performance Test Complete")

    } else {
      println("CAPI query found no articles")
    }


    if (liveBlogUrls.nonEmpty) {
      println("Generating average values for liveblogs")
      val liveBlogResultsList = listenForResultPages(liveBlogUrls, "liveblog",resultUrlList, wptBaseUrl, wptApiKey, wptLocation)
      val liveBlogCSVList: List[String] = liveBlogResultsList.map(x => x.toCSVString())
      liveBlogCSVResults = liveBlogCSVResults.concat(liveBlogCSVList.mkString)
      val liveBlogAverageResults = new ResultsSummary(liveBlogResultsList)
      liveBlogSummaryCSV = liveBlogAverageResults.generateCSVResultsTable("LiveBlog")

      //write liveblog results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing liveblog results to S3")
        s3Interface.writeFileToS3(liveBlogCSVName, liveBlogCSVResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(liveBlogCSVName, liveBlogCSVResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("LiveBlog Performance Test Complete")

    } else {
      println("CAPI query found no liveblogs")
    }

    if (interactiveUrls.nonEmpty) {
      println("Generating average values for interactives")
      val interactiveResultsList = listenForResultPages(interactiveUrls, "interactive", resultUrlList, wptBaseUrl, wptApiKey, wptLocation)
      val interactiveCSVList: List[String] = interactiveResultsList.map(x => x.toCSVString())
      // write interactive results to string
      interactiveCSVResults = interactiveCSVResults.concat(interactiveCSVList.mkString)
      val interactiveAverageResults = new ResultsSummary(interactiveResultsList)
      interactiveSummaryCSV = interactiveAverageResults.generateCSVResultsTable("Interactive")

      //write interactive results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing interactive results to S3")
        s3Interface.writeFileToS3(interactiveCSVName, interactiveCSVResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(interactiveCSVName, interactiveCSVResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("interactive Performance Test Complete")

    } else {
      println("CAPI query found no interactives")
    }

    if (videoUrls.nonEmpty) {
      println("Generating average values for videos")
      val videoResultsList = listenForResultPages(videoUrls, "video", resultUrlList, wptBaseUrl, wptApiKey, wptLocation)
      val videoCSVList: List[String] = videoResultsList.map(x => x.toCSVString())
      // write video results to string
      videoCSVResults = videoCSVResults.concat(videoCSVList.mkString)
      val videoAverageResults = new ResultsSummary(videoResultsList)
      videoSummaryCSV = videoAverageResults.generateCSVResultsTable("Video")

      //write video results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing video results to S3")
        s3Interface.writeFileToS3(videoCSVName, videoCSVResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(videoCSVName, videoCSVResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("video Performance Test Complete")

    } else {
      println("CAPI query found no video")
    }

    if (audioUrls.nonEmpty) {
      println("Generating average values for audio pages")
      val audioResultsList = listenForResultPages(audioUrls, "audio", resultUrlList, wptBaseUrl, wptApiKey, wptLocation)
      val audioCSVList: List[String] = audioResultsList.map(x => x.toCSVString())
      // write audio results to string
      audioCSVResults = audioCSVResults.concat(audioCSVList.mkString)
      val audioAverageResults = new ResultsSummary(audioResultsList)
      audioSummaryCSV = audioAverageResults.generateCSVResultsTable("Audio")

      //write audio results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing audio results to S3")
        s3Interface.writeFileToS3(audioCSVName, audioCSVResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(audioCSVName, audioCSVResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("audio Performance Test Complete")

    } else {
      println("CAPI query found no audio")
    }

    if (frontsUrls.nonEmpty) {
      println("Generating average values for liveblogs")
      val frontsResultsList = listenForResultPages(frontsUrls, "front",resultUrlList, wptBaseUrl, wptApiKey, wptLocation)
      val frontsCSVList: List[String] = frontsResultsList.map(x => x.toCSVString())
      // write fronts results to string
      frontsCSVResults = frontsCSVResults.concat(frontsCSVList.mkString)
      val frontsAverageResults = new ResultsSummary(frontsResultsList)
      frontsSummaryCSV = frontsAverageResults.generateCSVResultsTable("Front")
      //write fronts results to file
      if (!iamTestingLocally) {
        println(DateTime.now + " Writing liveblog results to S3")
        s3Interface.writeFileToS3(frontsCSVName, frontsCSVResults)
      }
      else {
        val outputWriter = new LocalFileOperations
        val writeSuccess: Int = outputWriter.writeLocalResultFile(frontsCSVName, frontsCSVResults)
        if (writeSuccess != 0) {
          println("problem writing local outputfile")
          System exit 1
        }
      }
      println("Fronts Performance Test Complete")

    } else {
      println("CAPI query found no fronts")
    }

    fullsummaryCSVResults = articleSummaryCSV + liveBlogSummaryCSV + interactiveSummaryCSV + frontsSummaryCSV
    if (!iamTestingLocally) {
      println(DateTime.now + " Writing liveblog results to S3")
      s3Interface.writeFileToS3(summaryCSVFilename, fullsummaryCSVResults)
    }
    else {
      val outputWriter = new LocalFileOperations
      val writeSuccess: Int = outputWriter.writeLocalResultFile(summaryCSVFilename, fullsummaryCSVResults)
      if (writeSuccess != 0) {
        println("problem writing local outputfile")
        System exit 1
      }
    }

    println("Job complete")


  }


  def getResultPages(urlList: List[String], wptBaseUrl: String, wptApiKey: String, wptLocation: String): List[(String,String, Boolean)] = {
    val wpt: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
    val ads: Boolean = true
    val noAds: Boolean = false
    val desktopResultsAds: List[(String, String, Boolean)] = urlList.map(page => { (page, wpt.sendPageAds(page), ads) })
    val desktopResultsNoAds: List[(String, String, Boolean)] = urlList.map(page => { (page, wpt.sendPageNoAds(page), noAds) })
    val mobileResultsAds: List[(String, String, Boolean)] = urlList.map(page => { (page, wpt.sendMobile3GPageAds(page, wptLocation), ads) })
    val mobileResultsNoAds: List[(String, String, Boolean)] = urlList.map(page => { (page, wpt.sendMobile3GPageNoAds(page, wptLocation), noAds) })
    desktopResultsAds ::: desktopResultsNoAds ::: mobileResultsAds ::: mobileResultsNoAds
  }

  def listenForResultPages(capiUrls: List[String], contentType: String, resultUrlList: List[(String, String, Boolean)], wptBaseUrl: String, wptApiKey: String, wptLocation: String): List[PerformanceResultsObject] = {
    println("ListenForResultPages called with: \n\n" +
      " List of Urls: \n" + capiUrls.mkString +
      "\n\nList of WebPage Test results: \n" + resultUrlList.mkString)

    val listenerList: List[WptResultPageListener] = capiUrls.flatMap(url => {
      for (element <- resultUrlList if element._1 == url) yield new WptResultPageListener(element._1, contentType, element._3, element._2)
    })

    println("Listener List created: \n" + listenerList.map(element => "list element: \n"+ "url: " + element.pageUrl + "\n" + "resulturl" + element.wptResultUrl + "\n"))

    val liveBlogResultsList: ParSeq[WptResultPageListener] = listenerList.par.map(element => {
      val wpt = new WebPageTest(wptBaseUrl, wptApiKey)
      val newElement = new WptResultPageListener(element.pageUrl, element.pageType, element.ads, element.wptResultUrl)
      newElement.testResults = wpt.getResults(newElement.wptResultUrl)
      newElement.testResults.adsDisplayed = newElement.ads
      newElement
    })
    val testResults: List[PerformanceResultsObject] = liveBlogResultsList.map(element => element.testResults).toList
    //val resultsWithAlerts: List[PerformanceResultsObject] = testResults.map(element => setAlertStatus(element, averages))

    //Confirm alert status by retesting alerting urls
    /*println("Confirming any items that have an alert")
    val confirmedTestResults = resultsWithAlerts.map(x => {
      if(x.alertStatus)
        confirmAlert(x, averages, wptBaseUrl, wptApiKey, wptLocation)
      else
        x
    })
    confirmedTestResults*/
    testResults
  }

  def confirmAlert(initialResult: PerformanceResultsObject, averages: PageAverageObject,wptBaseUrl: String, wptApiKey: String, wptLocation: String): PerformanceResultsObject ={
    val webPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
    val testCount: Int = if(initialResult.timeToFirstByte > 1000) {5} else {3}
    println("TTFB for " + initialResult.testUrl + "\n therefore setting test count of: " + testCount)
    val AlertConfirmationTestResult: PerformanceResultsObject = setAlertStatus(webPageTest.testMultipleTimes(initialResult.testUrl, initialResult.typeOfTest, wptLocation, testCount), averages)
    AlertConfirmationTestResult
  }

  def setAlertStatus(resultObject: PerformanceResultsObject, averages: PageAverageObject): PerformanceResultsObject ={
    //  Add results to string which will eventually become the content of our results file
    if(resultObject.typeOfTest == "Desktop") {
      if ((resultObject.timeFirstPaintInMs >= averages.desktopTimeFirstPaintInMs80thPercentile) ||
        (resultObject.speedIndex >= averages.desktopSpeedIndex80thPercentile) ||
        (resultObject.kBInFullyLoaded >= averages.desktopKBInFullyLoaded80thPercentile) ||
        (resultObject.estUSPrePaidCost >= averages.desktopEstUSPrePaidCost80thPercentile) ||
        (resultObject.estUSPostPaidCost >= averages.desktopEstUSPostPaidCost80thPercentile)) {
        if ((resultObject.timeFirstPaintInMs >= averages.desktopTimeFirstPaintInMs) ||
          (resultObject.speedIndex >= averages.desktopSpeedIndex) ||
          (resultObject.kBInFullyLoaded >= averages.desktopKBInFullyLoaded)) {
          println("row should be red one of the items qualifies")
          if(resultObject.timeFirstPaintInMs >= averages.desktopTimeFirstPaintInMs) {resultObject.alertDescription = "<p>Page takes " + resultObject.timeFirstPaintInSec + "s" + " for text to load and page to become scrollable. Should only take " + averages.desktopTimeFirstPaintInSeconds + "s.</p>"}
          if(resultObject.speedIndex >= averages.desktopSpeedIndex) {resultObject.alertDescription = "<p>Page takes " + averages.desktopAboveTheFoldCompleteInSec + "To render visible images etc. It should take " + averages.desktopAboveTheFoldCompleteInSec + "s.</P>"}
          if(resultObject.kBInFullyLoaded >= averages.desktopKBInFullyLoaded) {resultObject.alertDescription = resultObject.alertDescription +  "<p>Page is too heavy. Size is: " + resultObject.kBInFullyLoaded + "KB. It should be less than: " + averages.desktopKBInFullyLoaded + "KB.</p>"}
          println(resultObject.alertDescription)
          resultObject.warningStatus = true
          resultObject.alertStatus = true
        }
        else {
          println("row should be yellow one of the items qualifies")
          resultObject.warningStatus = true
          resultObject.alertStatus = false
        }
      }
      else {
        println("all fields within size limits")
        resultObject.warningStatus = false
        resultObject.alertStatus = false
      }
    } else {
      //checking if status of mobile test needs an alert
      if ((resultObject.timeFirstPaintInMs >= averages.mobileTimeFirstPaintInMs80thPercentile) ||
        (resultObject.speedIndex >= averages.mobileSpeedIndex80thPercentile) ||
        (resultObject.kBInFullyLoaded >= averages.mobileKBInFullyLoaded80thPercentile) ||
        (resultObject.estUSPrePaidCost >= averages.mobileEstUSPrePaidCost80thPercentile) ||
        (resultObject.estUSPostPaidCost >= averages.mobileEstUSPostPaidCost80thPercentile)) {
        if ((resultObject.timeFirstPaintInMs >= averages.mobileTimeFirstPaintInMs) ||
          (resultObject.speedIndex >= averages.mobileSpeedIndex) ||
          (resultObject.kBInFullyLoaded >= averages.mobileKBInFullyLoaded)){
          println("warning and alert statuses set to true")
          if(resultObject.timeFirstPaintInMs >= averages.mobileTimeFirstPaintInMs) {resultObject.alertDescription = "<p>Page takes " + resultObject.timeFirstPaintInSec + "s" + " for text to load and page to become scrollable. Should only take " + averages.mobileTimeFirstPaintInSeconds + "s.</p>"}
          if(resultObject.speedIndex >= averages.mobileSpeedIndex) {resultObject.alertDescription = "<p>Page takes " + averages.mobileAboveTheFoldCompleteInSec + "To render visible images etc. It should take " + averages.mobileAboveTheFoldCompleteInSec + "s or less.</p>"}
          if(resultObject.kBInFullyLoaded >= averages.mobileKBInFullyLoaded) {resultObject.alertDescription = resultObject.alertDescription +  "<p>Page is too heavy. Size is: " + resultObject.kBInFullyLoaded + "KB. It should be less than: " + averages.mobileKBInFullyLoaded + "KB.</p>"}
          resultObject.warningStatus = true
          resultObject.alertStatus = true
        }
        else {
          println("warning status set to true")
          resultObject.warningStatus = true
          resultObject.alertStatus = false
        }
      }
      else {
        println("all fields within size limits - both warning and alert status set to false")
        resultObject.warningStatus = false
        resultObject.alertStatus = false
      }
    }
    println("Returning test result with alert flags set to relevant values")
    resultObject
  }

  def getAverageResults(resultsList: List[PerformanceResultsObject]) = {


    val desktopAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Desktop") && !element.brokenTest && element.adsDisplayed) yield element
    val mobileAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Android") && !element.brokenTest && element.adsDisplayed) yield element
    val desktopNoAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Desktop") && !element.brokenTest && !element.adsDisplayed) yield element
    val mobileNoAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Android") && !element.brokenTest && !element.adsDisplayed) yield element


    val desktopAdsTimeToFirstByte: Int = desktopAdsResultsList.toSeq.map(_.timeToFirstByte).sum
    val desktopAdsResultArray = Array(
      (desktopAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/desktopAdsResultsList.length).toInt,
      (desktopAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/desktopAdsResultsList.length).toInt)

  val desktopNoAdsResultArray = Array(
    (desktopNoAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/desktopNoAdsResultsList.length).toInt,
    (desktopNoAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/desktopNoAdsResultsList.length).toInt)

    val mobileAdsResultArray = Array(
      (mobileAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/mobileAdsResultsList.length).toInt,
      (mobileAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/mobileAdsResultsList.length).toInt)

    val mobileNoAdsResultArray = Array(
      (mobileNoAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/mobileNoAdsResultsList.length).toInt,
      (mobileNoAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/mobileNoAdsResultsList.length).toInt)


  }



/*def generatePageAverages(urlList: List[String], wptBaseUrl: String, wptApiKey: String, wptLocation: String, itemtype: String, averageColor: String): PageAverageObject = {
  val setHighPriority: Boolean = true
  val webpageTest: WebPageTest = new WebPageTest(wptBaseUrl, wptApiKey)

  val resultsList: List[Array[PerformanceResultsObject]] = urlList.map(url => {
    val webPageDesktopTestResults: PerformanceResultsObject = webpageTest.desktopChromeCableTest(url, setHighPriority)
    val webPageMobileTestResults: PerformanceResultsObject = webpageTest.mobileChrome3GTest(url, wptLocation, setHighPriority)
    val combinedResults = Array(webPageDesktopTestResults, webPageMobileTestResults)
    combinedResults
  })

  val pageAverages: PageAverageObject = new GeneratedInteractiveAverages(resultsList, averageColor)
  pageAverages
}*/
  

  def retestUrl(initialResult: PerformanceResultsObject,wptBaseUrl: String, wptApiKey: String, wptLocation: String): PerformanceResultsObject ={
    val webPageTest = new WebPageTest(wptBaseUrl, wptApiKey)
    val testCount: Int = if(initialResult.timeToFirstByte > 1000) {5} else {3}
    println("TTFB for " + initialResult.testUrl + "\n therefore setting test count of: " + testCount)
 //   val AlertConfirmationTestResult: PerformanceResultsObject = setAlertStatus(webPageTest.testMultipleTimes(initialResult.testUrl, initialResult.typeOfTest, wptLocation, testCount), averages)
    webPageTest.testMultipleTimes(initialResult.testUrl, initialResult.typeOfTest, wptLocation, testCount)
  }


}


