package app.apiutils

//import app.api.PerformanceResultsObject
import com.squareup.okhttp.{OkHttpClient, Request, Response}
import org.joda.time.DateTime

import scala.xml.Elem


/**
 * Created by mmcnamara on 14/12/15.
 */
class WebPageTest(baseUrl: String, passedKey: String, urlfragments: List[String] = List("")) {

  val apiBaseUrl:String = baseUrl
  val apiKey:String = passedKey

  val wptResponseFormat:String = "xml"
  implicit val httpClient = new OkHttpClient()

  def desktopChromeCableTest(gnmPageUrl:String, highPriority: Boolean = false): PerformanceResultsObject = {
    println("Sending desktop webpagetest request to WPT API")
    if (highPriority) {
      val resultPage: String = sendHighPriorityPage(gnmPageUrl)
      println("Accessing results at: " + resultPage)
      val testResults: PerformanceResultsObject = getResults(resultPage)
      println("Results returned")
      testResults
    }else {
      val resultPage: String = sendPageNoAds(gnmPageUrl)
      println("Accessing results at: " + resultPage)
      val testResults: PerformanceResultsObject = getResults(resultPage)
      println("Results returned")
      testResults
    }
  }

  def mobileChrome3GTest(gnmPageUrl:String, wptLocation: String, highPriority: Boolean = false): PerformanceResultsObject = {
    println("Sending mobile webpagetest request to WPT API")
    if(highPriority){
      val resultPage: String = sendHighPriorityMobile3GPage(gnmPageUrl, wptLocation)
      println("Accessing results at: " + resultPage)
      val testResults: PerformanceResultsObject = getResults(resultPage)
      testResults
    }else {
      val resultPage: String = sendMobile3GPageNoAds(gnmPageUrl, wptLocation)
      println("Accessing results at: " + resultPage)
      val testResults: PerformanceResultsObject = getResults(resultPage)
      testResults
    }
  }

  def sendPageAds(gnmPageUrl:String): String = {
    println("Forming desktop webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    println("response received: \n" + responseXML.text)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    println(resultPage)
    resultPage
  }

  def sendPageNoAds(gnmPageUrl:String): String = {
    println("Forming desktop webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?" + "&f=" + wptResponseFormat + "&k=" + apiKey + "&url=" + gnmPageUrl + "#noads"
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    println("response received: \n" + responseXML.text)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    println(resultPage)
    resultPage
  }

  def sendHighPriorityPage(gnmPageUrl:String): String = {
    println("Forming desktop webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey + "&priority=1" + "&script =navigate  " + gnmPageUrl + "#noads"
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    println("response received: \n" + responseXML.text)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    println(resultPage)
    resultPage
  }

  def sendMobile3GPageAds(gnmPageUrl:String, wptLocation: String): String = {
    println("Forming mobile 3G webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey + "&mobile=1&mobileDevice=Nexus5&location=" + wptLocation + ":Chrome.3G"
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    println("response received: \n" + responseXML.text)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    resultPage
  }

  def sendMobile3GPageNoAds(gnmPageUrl:String, wptLocation: String): String = {
    println("Forming mobile 3G webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?" + "&f=" + wptResponseFormat + "&k=" + apiKey + "&mobile=1&mobileDevice=Nexus5&location=" + wptLocation + ":Chrome.3G" + "&url=" + gnmPageUrl + "#noads"
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    println("response received: \n" + responseXML.text)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    resultPage
  }

  def sendHighPriorityMobile3GPage(gnmPageUrl:String, wptLocation: String): String = {
    println("Forming mobile 3G webpage test query")
    val getUrl: String = apiBaseUrl + "/runtest.php?url=" + gnmPageUrl + "&f=" + wptResponseFormat + "&k=" + apiKey + "&mobile=1&mobileDevice=Nexus5&location=" + wptLocation + ":Chrome.3G" + "&priority=1"
    val request: Request = new Request.Builder()
      .url(getUrl)
      .get()
      .build()

    println("sending request: " + request.toString)
    val response: Response = httpClient.newCall(request).execute()
    val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
    println("response received: \n" + responseXML.text)
    val resultPage: String =  (responseXML \\ "xmlUrl").text
    resultPage
  }


  def getResults(resultUrl: String):PerformanceResultsObject = {
    println("Requesting result url:" + resultUrl)
    val request: Request = new Request.Builder()
      .url(resultUrl)
      .get()
      .build()
    var response: Response = httpClient.newCall(request).execute()
    println("Processing response and checking if results are ready")
    var testResults: Elem = scala.xml.XML.loadString(response.body.string)
    var iterator: Int = 0
    val msmaxTime: Int = 16000000
    val msTimeBetweenPings: Int = 30000
    val maxCount: Int = roundAt(0)(msmaxTime.toDouble / msTimeBetweenPings).toInt
    while (((testResults \\ "statusCode").text.toInt != 200) && (iterator < maxCount)) {
      println(DateTime.now + " " + (testResults \\ "statusCode").text + " statusCode response - test not ready. " + iterator + " of " + maxCount + " attempts\n")
      Thread.sleep(msTimeBetweenPings)
      iterator += 1
      response = httpClient.newCall(request).execute()
      testResults = scala.xml.XML.loadString(response.body.string)
    }
    if ((testResults \\ "statusCode").text.toInt == 200) {
      //Add one final request as occasionally 200 code comes before the data we want.
      Thread.sleep(5000)
      response = httpClient.newCall(request).execute()
      testResults = scala.xml.XML.loadString(response.body.string)
      if ((testResults \\ "response" \ "data" \ "successfulFVRuns").text.toInt > 0) {
        println("\n" + DateTime.now + " statusCode == 200: Page ready after " + ((iterator + 1) * msTimeBetweenPings).toDouble / 1000 + " seconds\n Refining results")
        val elementsList: List[PageElementFromHTMLTableRow] = obtainPageRequestDetails(resultUrl)
        refineResults(testResults, elementsList)
      } else {
        println(DateTime.now + " Test results show 0 successful runs ")
        failedTestNoSuccessfulRuns(resultUrl, testResults)
      }
    } else {
      println(DateTime.now + " Test timed out after " + ((iterator + 1) * msTimeBetweenPings).toDouble / 1000 + " seconds")
      failedTestTimeout(resultUrl, testResults)
    }
  }

  def refineResults(rawXMLResult: Elem, elementsList: List[PageElementFromHTMLTableRow]): PerformanceResultsObject = {
    println("parsing the XML results")
    val testUrl: String = (rawXMLResult \\ "response" \ "data" \ "testUrl").text.toString
    lazy val adsDisplayed: Boolean = !testUrl.contains("#noads")
    val testType: String = if((rawXMLResult \\ "response" \ "data" \ "from").text.toString.contains("Emulated Nexus 5")){"Android/3G"}else{"Desktop"}
    val timeToFirstByte: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "TTFB").text.toInt
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
    val status: String = "Test Success"

    println("Creating PerformanceResultsObject")
    val result: PerformanceResultsObject = new PerformanceResultsObject(testUrl, testType, adsDisplayed, timeToFirstByte, firstPaint, docTime, bytesInDoc, fullyLoadedTime, totalbytesIn, speedIndex, status, false, false, false)
    val trimmedElementList = trimToEditorialElements(elementsList)
    val sortedElementList = sortPageElementList(trimmedElementList)
    result.populateHeavyElementList(sortedElementList)
    println("Returning PerformanceResultsObject")
    result
  }

  def testMultipleTimes(url: String, typeOfTest: String, wptLocation: String, testCount: Int): PerformanceResultsObject = {
      println("Alert registered on url: " + url + "\n" + "verify by retesting " + testCount + " times and taking median value")
      if(typeOfTest == "Desktop"){
        println("Forming desktop webpage test query to confirm alert status")
        val getUrl: String = apiBaseUrl + "/runtest.php?url=" + url + "&f=" + wptResponseFormat + "&k=" + apiKey + "&runs=" + testCount + "&priority=1" + "&script =navigate  " + url + "#noads"
        val request: Request = new Request.Builder()
          .url(getUrl)
          .get()
          .build()

        println("sending request: " + request.toString)
        val response: Response = httpClient.newCall(request).execute()
        val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
        val resultPage: String =  (responseXML \\ "xmlUrl").text
        println(resultPage)
        val testResultObject: PerformanceResultsObject = getMultipleResults(resultPage)
        testResultObject
    }
    else{
        println("Forming mobile 3G webpage test query to confirm alert status")
        val getUrl: String = apiBaseUrl + "/runtest.php?url=" + url + "&f=" + wptResponseFormat + "&k=" + apiKey + "&mobile=1&mobileDevice=Nexus5&location=" + wptLocation + ":Chrome.3G" + "&priority=1" + "&script =navigate  " + url + "#noads"
        val request: Request = new Request.Builder()
          .url(getUrl)
          .get()
          .build()

        println("sending request: " + request.toString)
        val response: Response = httpClient.newCall(request).execute()
        val responseXML: Elem = scala.xml.XML.loadString(response.body.string)
        val resultPage: String =  (responseXML \\ "xmlUrl").text
        println(resultPage)
        val testResultObject: PerformanceResultsObject = getMultipleResults(resultPage)
        testResultObject
      }
  }


  def getMultipleResults(resultUrl: String): PerformanceResultsObject = {
    println("Requesting url:" + resultUrl)
    val request: Request = new Request.Builder()
      .url(resultUrl)
      .get()
      .build()
    var response: Response = httpClient.newCall(request).execute()
    println("Processing response and checking if results are ready")
    var testResults: Elem = scala.xml.XML.loadString(response.body.string)
    var iterator: Int = 0
    val msmaxTime: Int = 1200000
    val msTimeBetweenPings: Int = 30000
    val maxCount: Int = roundAt(0)(msmaxTime.toDouble / msTimeBetweenPings).toInt
    while (((testResults \\ "statusCode").text.toInt != 200) && (iterator < maxCount)) {
      println(DateTime.now + " " + (testResults \\ "statusCode").text + " statusCode response - test not ready. " + iterator + " of " + maxCount + " attempts\n")
      Thread.sleep(msTimeBetweenPings)
      iterator += 1
      response = httpClient.newCall(request).execute()
      testResults = scala.xml.XML.loadString(response.body.string)
    }
    if ((testResults \\ "statusCode").text.toInt == 200) {
      //Add one final request as occasionally 200 code comes before the data we want.
      Thread.sleep(5000)
      response = httpClient.newCall(request).execute()
      testResults = scala.xml.XML.loadString(response.body.string)
      if ((testResults \\ "response" \ "data" \ "successfulFVRuns").text.toInt > 0) {
        println("\n" + DateTime.now + " statusCode == 200: Page ready after " + ((iterator + 1) * msTimeBetweenPings).toDouble / 1000 + " seconds\n Refining results")
        val elementsList: List[PageElementFromHTMLTableRow] = obtainPageRequestDetails(resultUrl)
        refineMultipleResults(testResults, elementsList)
      } else {
        println(DateTime.now + " Test results show 0 successful runs ")
        failedTestNoSuccessfulRuns(resultUrl, testResults)
      }
    } else {
      println(DateTime.now + " Test timed out after " + ((iterator + 1) * msTimeBetweenPings).toDouble / 1000 + " seconds")
      failedTestTimeout(resultUrl, testResults)
    }
  }

  def refineMultipleResults(rawXMLResult: Elem, elementsList: List[PageElementFromHTMLTableRow]): PerformanceResultsObject = {
    println("parsing the XML results")
    val testUrl: String = (rawXMLResult \\ "response" \ "data" \ "testUrl").text.toString
    val testType: String = if((rawXMLResult \\ "response" \ "data" \ "from").text.toString.contains("Emulated Nexus 5")){"Android/3G"}else{"Desktop"}
    lazy val adsDisplayed: Boolean = !testUrl.contains("#noads")
    val timeToFirstByte: Int = (rawXMLResult \\ "response" \ "data" \ "median" \ "firstView" \ "TTFB").text.toInt
    val firstPaint: Int = (rawXMLResult \\ "response" \ "data" \ "median" \ "firstView" \ "firstPaint").text.toInt
    println ("firstPaint = " + firstPaint)
    val docTime: Int = (rawXMLResult \\ "response" \ "data" \ "median" \ "firstView" \  "docTime").text.toInt
    println ("docTime = " + docTime)
    val bytesInDoc: Int = (rawXMLResult \\ "response" \ "data" \ "median" \ "firstView" \ "bytesInDoc").text.toInt
    println ("bytesInDoc = " + bytesInDoc)
    val fullyLoadedTime: Int = (rawXMLResult \\ "response" \ "data" \ "median" \ "firstView" \ "fullyLoaded").text.toInt
    println ("Time to Fully loaded = " + fullyLoadedTime)
    val totalbytesIn: Int = (rawXMLResult \\ "response" \ "data" \ "median" \ "firstView" \ "bytesIn").text.toInt
    println ("Total bytes = " + totalbytesIn)
    val speedIndex: Int = (rawXMLResult \\ "response" \ "data" \ "median" \ "firstView" \ "SpeedIndex").text.toInt
    println ("SpeedIndex = " + speedIndex)
    val status: String = "Test Success"
    println("Creating PerformanceResultsObject")
    val result: PerformanceResultsObject = new PerformanceResultsObject(testUrl, testType, adsDisplayed, timeToFirstByte, firstPaint, docTime, bytesInDoc, fullyLoadedTime, totalbytesIn, speedIndex, status, false, false, false)
    result.fullElementList = elementsList
    val sortedElementList = sortPageElementList(elementsList)
    result.populateHeavyElementList(sortedElementList)
    println("Result string: " + result.toHTMLSimpleTableCells())
    println("List of heaviest page Elements contains " + result.heavyElementList.length + " elements")
    println("Returning PerformanceResultsObject")
    result
  }

  def obtainPageRequestDetails(webpageTestResultUrl: String): List[PageElementFromHTMLTableRow] = {
    val sliceStart: Int = apiBaseUrl.length + "/xmlResult/".length
    val sliceEnd: Int = webpageTestResultUrl.length - 1
    val testId: String = webpageTestResultUrl.slice(sliceStart,sliceEnd)
    val resultDetailsPage: String =  apiBaseUrl + "/result/" + testId + "/1/details/"
    val request:Request  = new Request.Builder()
      .url(resultDetailsPage)
      .get()
      .build()
    val response: Response = httpClient.newCall(request).execute()
    val responseString:String = response.body().string()
    val tableString: String = trimToHTMLTable(responseString)
    val pageElementList: List[PageElementFromHTMLTableRow] = generatePageElementList(tableString)
    println("List generated - contains: " + pageElementList.length + " elements.")
    pageElementList
  }

  def trimToEditorialElements(elementList: List[PageElementFromHTMLTableRow]): List[PageElementFromHTMLTableRow] = {
    val returnList: List[PageElementFromHTMLTableRow] = for (element <- elementList if element.contentType.contains("image") || element.contentType.contains("video") || element.contentType.contains("application") || element.contentType.contains("document")) yield element
    returnList
  }

  def trimToHTMLTable(pageHTML: String): String = {
    //    val responseStringXML: Elem = scala.xml.XML.loadString(response.body.string)
    val responseStringOuterTableStart: Int = pageHTML.indexOf("<table id=\"tableDetails\" class=\"details center\">")
    val responseStringOuterTableEnd: Int = pageHTML.indexOf("</table>", responseStringOuterTableStart)
    val outerTableString: String = pageHTML.slice(responseStringOuterTableStart, responseStringOuterTableEnd)
    val innerTableStart: Int = outerTableString.indexOf("<tbody>")
    val innerTableEnd: Int = outerTableString.indexOf("</tbody>")
    val innerTableString: String = outerTableString.slice(innerTableStart, innerTableEnd)
    val tableDataRows: String = innerTableString.slice(innerTableString.indexOf("<tr>"), innerTableString.length)
    tableDataRows
  }

  def generatePageElementList(htmlTableRows: String): List[PageElementFromHTMLTableRow] = {
    var restOfTable: String = htmlTableRows
    var pageElementList: List[PageElementFromHTMLTableRow] = List()
    var counter: Int = 0
    while (restOfTable.nonEmpty){
      val (currentRow, rest): (String, String) = restOfTable.splitAt(restOfTable.indexOf("</tr>")+5)
      pageElementList = pageElementList :+ new PageElementFromHTMLTableRow(currentRow)
      restOfTable = rest
      counter += 1
    }
    pageElementList
  }

  def failedTestNoSuccessfulRuns(url: String, rawResults: Elem): PerformanceResultsObject = {
    val failIndicator: Int = -1
    val testType: String = if((rawResults \\ "response" \ "data" \ "from").text.toString.contains("Emulated Nexus 5")){"Android/3G"}else{"Desktop"}
    lazy val adsDisplayed: Boolean = !url.contains("#noads")
    val failComment: String = "No successful runs of test"
    val failElement: PerformanceResultsObject = new PerformanceResultsObject(url, testType, adsDisplayed ,failIndicator, failIndicator,failIndicator,failIndicator,failIndicator,failIndicator,failIndicator, failComment, false, false, true)
    failElement
  }

  def failedTestTimeout(url: String, rawResults: Elem): PerformanceResultsObject = {
    val failIndicator: Int = -1
    val testType: String = if((rawResults \\ "response" \ "data" \ "from").text.toString.contains("Emulated Nexus 5")){"Android/3G"}else{"Desktop"}
    lazy val adsDisplayed: Boolean = !url.contains("#noads")
    val failComment: String = "Test request timed out"
    // set warning status as result may have timed out due to very large page
    val failElement: PerformanceResultsObject = new PerformanceResultsObject(url, testType, adsDisplayed ,failIndicator, failIndicator,failIndicator,failIndicator,failIndicator,failIndicator,failIndicator, failComment, true, true, true)
    failElement
  }

  def sortPageElementList(elementList: List[PageElementFromHTMLTableRow]):List[PageElementFromHTMLTableRow] = {
    elementList.sortWith(_.bytesDownloaded > _.bytesDownloaded)
  }

  def roundAt(p: Int)(n: Double): Double = { val s = math pow (10, p); (math round n * s) / s }

}


//todo - add url into results so we can use it in result element - makes the whole html thing easier- get from xml?
