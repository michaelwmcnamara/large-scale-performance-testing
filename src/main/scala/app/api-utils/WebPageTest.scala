package app.apiutils

import com.squareup.okhttp.{OkHttpClient, Request, Response}

import scala.xml.Elem


/**
 * Created by mmcnamara on 14/12/15.
 */
class WebPageTest(baseUrl: String, passedKey: String) {

  val apiBaseUrl:String = baseUrl
  val apiKey:String = passedKey

  val wptResponseFormat:String = "xml"
  implicit val httpClient = new OkHttpClient()

  class ResultElement(tFP:Int, tDC: Int, bDC: Int, tFL: Int, bFL: Int, sI: Int) {
    val timeFirstPaint: Int = tFP
    val timeDocComplete: Int = tDC
    val bytesInDoccomplete: Int = bDC
    val timeFullyLoaded: Int = tFL
    val bytesInFullyLoaded: Int = bFL
    val speedIndex: Int = sI


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
    val resultPage: String = sendPage(gnmPageUrl)
    val testResults: ResultElement = getResults(resultPage)
    testResults
  }

  def mobileChrome3GTest(gnmPageUrl:String): ResultElement = {

    val resultPage: String = sendPage(gnmPageUrl)
    val testResults: ResultElement = getResults(resultPage)
    testResults
  }

  def sendPage(gnmPageUrl:String): String = {

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
    val request: Request = new Request.Builder()
      .url(resultUrl)
      .get()
      .build()
    var response: Response = httpClient.newCall(request).execute()
    var testResults: Elem = scala.xml.XML.loadString(response.body.string)
    while ((testResults \\ "statusCode").text.toInt != 200) {
      println((testResults \\ "statusCode").text + " statusCode response - test not ready\n")
      Thread.sleep(20000)
      response = httpClient.newCall(request).execute()
      testResults = scala.xml.XML.loadString(response.body.string)
    }
    println("\n statusCode == 200: Page ready! \n Refining results")
    refineResults(testResults)
  }

  def refineResults(rawXMLResult: Elem): ResultElement = {
    val result: ResultElement = new ResultElement(
      (rawXMLResult \\ "response" \ "data" \ "run" \ "1" \ "firstView" \ "firstPaint").text.toInt,
      (rawXMLResult \\ "response" \ "data" \ "run" \ "1" \ "firstView" \ "docTime").text.toInt,
      (rawXMLResult \\ "response" \ "data" \ "run" \ "1" \ "firstView" \ "bytesInDoc").text.toInt,
      (rawXMLResult \\ "response" \ "data" \ "run" \ "1" \ "firstView" \ "fullyLoaded").text.toInt,
      (rawXMLResult \\ "response" \ "data" \ "run" \ "1" \ "firstView" \ "bytesIn").text.toInt,
      (rawXMLResult \\ "response" \ "data" \ "run" \ "1" \ "firstView" \ "SpeedIndex").text.toInt)
    println(result.toString())
    result
  }

}