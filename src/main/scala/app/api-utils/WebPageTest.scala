package app.apiutils

import com.squareup.okhttp.{OkHttpClient, Request, Response}
import org.joda.time.DateTime

import scala.xml.Elem


/**
 * Created by mmcnamara on 14/12/15.
 */
class WebPageTest(baseUrl: String, passedKey: String) {

  val apiBaseUrl:String = baseUrl
  val apiKey:String = passedKey

  val wptResponseFormat:String = "xml"
  implicit val httpClient = new OkHttpClient()

  class ResultElement(tFP:Int, tDC: Int, bDC: Int, tFL: Int, bFL: Int, sI: Int, status: String) {
    val timeFirstPaint: Int = tFP
    val timeDocComplete: Int = tDC
    val bytesInDoccomplete: Int = bDC
    val timeFullyLoaded: Int = tFL
    val bytesInFullyLoaded: Int = bFL
    val speedIndex: Int = sI
    val resultStatus:String = status

    def toIntList(): List[Int] = {
      List(timeFirstPaint, timeDocComplete, timeFullyLoaded, speedIndex)
    }

    def toStringList(): List[String] = {
      List(timeFirstPaint.toString + "ms", timeDocComplete.toString + "ms", timeFullyLoaded.toString + "ms", speedIndex.toString)
    }

    override def toString(): String = {
      timeFirstPaint.toString + "ms, " + timeDocComplete.toString + "ms, " + (bytesInDoccomplete/1000) + "kB, " + timeFullyLoaded.toString + "ms, " + (bytesInFullyLoaded/1000) + "kB, " + speedIndex.toString
    }
  }

  def desktopChromeCableTest(gnmPageUrl:String): ResultElement = {
    println("Sending desktop webpagetest request to WPT API")
    val resultPage: String = sendPage(gnmPageUrl)
    println("Accessing results at: " + resultPage)
    val testResults: ResultElement = getResults(resultPage)
    println("Results returned")
    testResults
  }

  def mobileChrome3GTest(gnmPageUrl:String): ResultElement = {
    println("Sending mobile webpagetest request to WPT API")
    val resultPage: String = sendMobile3GPage(gnmPageUrl)
    println("Accessing results at: " + resultPage)
    val testResults: ResultElement = getResults(resultPage)
    testResults
  }

  def sendPage(gnmPageUrl:String): String = {
    println("Forming desktop webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    println(resultPage)
    resultPage
  }

  def sendMobile3GPage(gnmPageUrl:String): String = {
    println("Forming mobile 3G webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey + "&mobile=1&mobileDevice=Nexus5&location=Dulles:Chrome.3G"
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    resultPage
  }


  def getResults(resultUrl: String):ResultElement = {
    println("Requesting url:" + resultUrl)
    val request: Request = new Request.Builder()
      .url(resultUrl)
      .get()
      .build()
    var response: Response = httpClient.newCall(request).execute()
    println("Processing response and checking if results are ready")
    var testResults: Elem = scala.xml.XML.loadString(response.body.string)
    var iterator: Int = 0
    val maxCount: Int = 10
    val msTimeBetweenPings: Int = 20000
    while (((testResults \\ "statusCode").text.toInt != 200) && (iterator < maxCount)) {
      println(DateTime.now + " " + (testResults \\ "statusCode").text + " statusCode response - test not ready. " + iterator + " attempts\n")
      Thread.sleep(msTimeBetweenPings)
      iterator += 1
      response = httpClient.newCall(request).execute()
      testResults = scala.xml.XML.loadString(response.body.string)
    }
    if (((testResults \\ "statusCode").text.toInt == 200) && ((testResults \\ "response" \ "data" \ "successfulFVRuns").text.toInt > 0) && ((testResults \\ "response" \ "data" \ "successfulRVRuns").text.toInt > 0)) {
      println("\n" + DateTime.now + " statusCode == 200: Page ready! \n Refining results")
      refineResults(testResults)
    } else {
        if((testResults \\ "statusCode").text.toInt == 200) {
          println(DateTime.now + " Test results show 0 successful runs ")
          failedTest()
        }else{
          println(DateTime.now + "Test timed out after " + (iterator * msTimeBetweenPings)/1000 + " seconds")
          failedTestTimeout()
        }
      }
  }

  def refineResults(rawXMLResult: Elem): ResultElement = {
    println("parsing the XML results")
    val firstPaint: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "firstPaint").text.toInt
    println ("firstPaint = " + firstPaint)
    val docTime: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "docTime").text.toInt
    println ("docTime = " + docTime)
    val bytesInDoc: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "bytesInDoc").text.toInt
    println ("bytesInDoc = " + bytesInDoc)
    val fullyLoadedTime: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "fullyLoaded").text.toInt
    println ("Time to Fully loaded = " + fullyLoadedTime)
    val totalbytesIn: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "bytesIn").text.toInt
    println ("Total bytes = " + totalbytesIn)
    val speedIndex: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "SpeedIndex").text.toInt
    println ("SpeedIndex = " + speedIndex)
    val status: String = "Test Successful"

    println("Creating ResultElement")
    val result: ResultElement = new ResultElement(firstPaint, docTime, bytesInDoc, fullyLoadedTime, totalbytesIn, speedIndex, status)
    println("Returning ResultElement")
    result
  }

  def failedTest(): ResultElement = {
    val failIndicator: Int = -1
    val failStatement: String = "Test Failed"
    val failElement: ResultElement = new ResultElement(failIndicator,failIndicator,failIndicator,failIndicator,failIndicator,failIndicator, failStatement)
    failElement
  }

  def failedTestTimeout(): ResultElement = {
    val failIndicator: Int = -1
    val failStatement: String = "Test Timed Out"
    val failElement: ResultElement = new ResultElement(failIndicator,failIndicator,failIndicator,failIndicator,failIndicator,failIndicator, failStatement)
    failElement
  }
}