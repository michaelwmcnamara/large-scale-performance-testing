import app.App.PageAverageObject
import org.joda.time.DateTime
import org.scalatest._
import scala.io.Source
import scala.xml.{Elem, XML}
import app.App



/**
 * Created by mmcnamara on 01/02/16.
 */

abstract class UnitSpec extends FlatSpec with Matchers with
OptionValues with Inside with Inspectors

class ResultElement(url:String, tFP:Int, tDC: Int, bDC: Int, tFL: Int, bFL: Int, sI: Int, status: String) {
  val testUrl: String = url
  val timeFirstPaint: Int = tFP
  val timeDocComplete: Int = tDC
  val bytesInDoccomplete: Int = bDC
  val timeFullyLoaded: Int = tFL
  val bytesInFullyLoaded: Int = bFL
  // translate bytes to MB and apply a constant to get cost
  val estUSPrePaidCost: Double = roundAt(2)((bytesInFullyLoaded.toDouble/1048576)*0.10)
  val estUSPostpaidCost: Double = roundAt(2)((bytesInFullyLoaded.toDouble/1048576)*0.06)
  val speedIndex: Int = sI
  val resultStatus:String = status
  def roundAt(p: Int)(n: Double): Double = { val s = math pow (10, p); (math round n * s) / s }

  def toStringList(): List[String] = {
    List(testUrl.toString + ", " + timeFirstPaint.toString + "ms", timeDocComplete.toString + "ms", (bytesInDoccomplete/1000) + "kB" , timeFullyLoaded.toString + "ms", (bytesInFullyLoaded/1000) + "kB", speedIndex.toString, resultStatus)
  }

  def toHTMLTableCells(): String = {
    "<th>" + testUrl + " </th>" + "<td>" + timeFirstPaint.toString + "ms </td><td>" +  timeDocComplete.toString + "ms </td><td>" + (bytesInDoccomplete/1000) + "kB </td><td>" + timeFullyLoaded.toString + "ms </td><td>" + (bytesInFullyLoaded/1000) + "kB </td><td>" + estUSPrePaidCost + "</td><td>" + speedIndex.toString + " </td><td> " + resultStatus + "</td>"
  }

  def toHTMLSimpleTableCells(): String = {
    "<th>" + testUrl + " </th><td>" +  (timeDocComplete/1000).toString + "s </td><td>" + (bytesInFullyLoaded/1000) + "kB </td><td>(US)$" + estUSPrePaidCost + "</td><td>(US)$" + estUSPostpaidCost + "</td><td>" + speedIndex.toString + " </td><td> " + resultStatus + "</td>"
  }

  override def toString(): String = {
    testUrl + ", " + timeFirstPaint.toString + "ms, " + timeDocComplete.toString + "ms, " + (bytesInDoccomplete/1000) + "kB, " + timeFullyLoaded.toString + "ms, " + (bytesInFullyLoaded/1000) + "kB, " + speedIndex.toString + ", " + resultStatus
  }
}



class testthresholds extends UnitSpec with Matchers {

  val warningColor: String = "yellow"
  val alertColor: String = "red"


  def refineResults(rawXMLResult: Elem): ResultElement = {
    println("parsing the XML results")
    val testUrl: String = (rawXMLResult \\ "response" \ "data" \ "testUrl").text.toString
    val firstPaint: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "firstPaint").text.toInt
    println("firstPaint = " + firstPaint)
    val docTime: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "docTime").text.toInt
    println("docTime = " + docTime)
    val bytesInDoc: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "bytesInDoc").text.toInt
    println("bytesInDoc = " + bytesInDoc)
    val fullyLoadedTime: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "fullyLoaded").text.toInt
    println("Time to Fully loaded = " + fullyLoadedTime)
    val totalbytesIn: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "bytesIn").text.toInt
    println("Total bytes = " + totalbytesIn)
    val speedIndex: Int = (rawXMLResult \\ "response" \ "data" \ "run" \ "firstView" \ "results" \ "SpeedIndex").text.toInt
    println("SpeedIndex = " + speedIndex)
    val status: String = "Test Success"

    println("Creating ResultElement")
    val result: ResultElement = new ResultElement(testUrl, firstPaint, docTime, bytesInDoc, fullyLoadedTime, totalbytesIn, speedIndex, status)
    println("Result time doc complete: " + result.timeDocComplete)
    println("Result time bytes fully loaded: " + result.bytesInFullyLoaded)
    println("Result string: " + result.toHTMLSimpleTableCells())
    println("Returning ResultElement")
    result
  }

  //    val app = new PageAverageObject

  def returnResultsString(url: String): List[String] = {
    val averages = new PageAverageObject(3, 6, 3600, 9, 5500, 0.6, 0.5, 4000, 5, 3, 19, 2500, 20, 3000, 0.4, 0.3, 5000, 3, "")

    //  Define new web-page-test API request and send it the url to test
    val desktopResultFilename = "/Users/mmcnamara/git/capi-wpt-querybot/desktoptest.xml"
    val mobileResultFilename = "/Users/mmcnamara/git/capi-wpt-querybot/mobile3Gtest.xml"

    var returnString: String = ""
    var simpleReturnString: String = ""

    val desktopXml: Elem = XML.loadFile(desktopResultFilename)
    val mobileXml: Elem = XML.loadFile(mobileResultFilename)

    val webPageDesktopTestResults: ResultElement = refineResults(desktopXml)
    val webPageMobileTestResults: ResultElement = refineResults(mobileXml)

    println(DateTime.now + " Adding results of desktop test to simple results string")
    returnString = returnString.concat("<tr><td>" + DateTime.now + "</td><td>Desktop</td>" + webPageDesktopTestResults.toHTMLTableCells() + "</tr>")
    returnString = returnString.concat("<tr><td>" + DateTime.now + "</td><td>Android/3G</td>" + webPageMobileTestResults.toHTMLTableCells() + "</tr>")

    println("\n*******************  Desktop Results  ***********************************************************\n")
    println("Time Doc Complete: \n")
    println("Result: " + webPageDesktopTestResults.timeDocComplete/1000)
    println("Average: " + averages.desktopTimeDocComplete)
    println("Average 80th %ile: " + averages.desktopTimeDocComplete80thPercentile + "\n")

    println("Result: " + webPageDesktopTestResults.bytesInFullyLoaded/1000)
    println("Average: " + averages.desktopKBInFullyLoaded)
    println("Average 80th %ile: " + averages.desktopKBInFullyLoaded80thPercentile + "\n")

    println("Result: " + webPageDesktopTestResults.estUSPrePaidCost)
    println("Average: " + averages.desktopCostUSPrepaid)
    println("Average 80th %ile: " + averages.desktopCostUSprepaid80thPercentile + "\n")

    println("Result: " + webPageDesktopTestResults.estUSPostpaidCost)
    println("Average: " + averages.desktopCostUSPostPaid)
    println("Average 80th %ile: " + averages.desktopCostUSpostpaid80thPercentile + "\n")

    println("Result: " + webPageDesktopTestResults.speedIndex)
    println("Average: " + averages.desktopSpeedIndex)
    println("Average 80th %ile: " + averages.desktopSpeedIndex80thPercentile + "\n \n \n")

    println("\n*******************  Mobile Results  ***********************************************************\n")

    println("Time Doc Complete: \n")
    println("Result: " + webPageMobileTestResults.timeDocComplete/1000)
    println("Average: " + averages.mobileTimeDocComplete)
    println("Average 80th %ile: " + averages.mobileTimeDocComplete80thPercentile + "\n")

    println("Result: " + webPageMobileTestResults.bytesInFullyLoaded/1000)
    println("Average: " + averages.mobileKBInFullyLoaded)
    println("Average 80th %ile: " + averages.mobileKBInFullyLoaded80thPercentile + "\n")

    println("Result: " + webPageDesktopTestResults.estUSPrePaidCost)
    println("Average: " + averages.mobileCostUSPrepaid)
    println("Average 80th %ile: " + averages.mobileCostUSPrepaid80thPercentile + "\n")

    println("Result: " + webPageDesktopTestResults.estUSPostpaidCost)
    println("Average: " + averages.mobileCostUSPostPaid)
    println("Average 80th %ile: " + averages.mobileCostUSPostpaid80thPercentile + "\n")

    println("Result: " + webPageMobileTestResults.speedIndex)
    println("Average: " + averages.mobileSpeedIndex)
    println("Average 80th %ile: " + averages.mobileSpeedIndex80thPercentile + "\n \n \n")



    if ((webPageDesktopTestResults.timeDocComplete / 1000 >= averages.desktopTimeDocComplete80thPercentile) ||
      (webPageDesktopTestResults.bytesInFullyLoaded / 1000 >= averages.desktopKBInFullyLoaded80thPercentile) ||
      (webPageDesktopTestResults.estUSPrePaidCost >= averages.desktopCostUSprepaid80thPercentile) ||
      (webPageDesktopTestResults.estUSPostpaidCost >= averages.desktopCostUSpostpaid80thPercentile) ||
      (webPageDesktopTestResults.speedIndex >= averages.desktopSpeedIndex80thPercentile)) {
      if ((webPageDesktopTestResults.timeDocComplete / 1000 >= averages.desktopTimeDocComplete) ||
        (webPageDesktopTestResults.bytesInFullyLoaded / 1000 >= averages.desktopKBInFullyLoaded) ||
        (webPageDesktopTestResults.estUSPrePaidCost >= averages.desktopCostUSPrepaid) ||
        (webPageDesktopTestResults.estUSPostpaidCost >= averages.desktopCostUSPostPaid) ||
        (webPageDesktopTestResults.speedIndex >= averages.desktopSpeedIndex)) {
        println("row should be red one of the items qualifies")
        simpleReturnString = simpleReturnString.concat("<tr bgcolor=" + alertColor + "><td>" + DateTime.now + "</td><td>Desktop</td>" + webPageDesktopTestResults.toHTMLSimpleTableCells() + "</tr>")
      }
      else {
        println("row should be yellow one of the items qualifies")
        simpleReturnString = simpleReturnString.concat("<tr bgcolor=" + warningColor + "><td>" + DateTime.now + "</td><td>Desktop</td>" + webPageDesktopTestResults.toHTMLSimpleTableCells() + "</tr>")
      }
    }
    else {
      println("all fields within size limits")
      simpleReturnString = simpleReturnString.concat("<tr><td>" + DateTime.now + "</td><td>Desktop</td>" + webPageDesktopTestResults.toHTMLSimpleTableCells() + "</tr>")
    }
    println(DateTime.now + " Adding results of mobile test to simple results string")
    if ((webPageMobileTestResults.timeDocComplete / 1000 >= averages.mobileTimeDocComplete80thPercentile) ||
      (webPageMobileTestResults.bytesInFullyLoaded / 1000 >= averages.mobileKBInFullyLoaded80thPercentile) ||
      (webPageMobileTestResults.estUSPrePaidCost >= averages.mobileCostUSPrepaid80thPercentile) ||
      (webPageMobileTestResults.estUSPostpaidCost >= averages.mobileCostUSPostpaid80thPercentile) ||
      (webPageMobileTestResults.speedIndex >= averages.mobileSpeedIndex80thPercentile)) {
      if ((webPageMobileTestResults.timeDocComplete / 1000 >= averages.mobileTimeDocComplete) ||
        (webPageMobileTestResults.bytesInFullyLoaded / 1000 >= averages.mobileKBInFullyLoaded) ||
        (webPageMobileTestResults.estUSPrePaidCost >= averages.mobileCostUSPrepaid) ||
        (webPageMobileTestResults.estUSPostpaidCost >= averages.mobileCostUSPostPaid) ||
        (webPageMobileTestResults.speedIndex >= averages.mobileSpeedIndex)) {
        println("row should be red one of the items qualifies")
        simpleReturnString = simpleReturnString.concat("<tr>bgcolor=" + alertColor + "<td>" + DateTime.now + "</td><td>Android/3G</td>" + webPageMobileTestResults.toHTMLSimpleTableCells() + "</tr>")
      }
      else {
        println("row should be yellow one of the items qualifies")
        simpleReturnString = simpleReturnString.concat("<tr>bgcolor=" + warningColor + "<td>" + DateTime.now + "</td><td>Android/3G</td>" + webPageMobileTestResults.toHTMLSimpleTableCells() + "</tr>")
      }
    }
    else {

      println("row should be yellow one of the items qualifies")
      simpleReturnString = simpleReturnString.concat("<tr><td>" + DateTime.now + "</td><td>Android/3G</td>" + webPageMobileTestResults.toHTMLSimpleTableCells() + "</tr>")
    }
    println(DateTime.now + " final results: \n")
    val resultList: List[String] = List(returnString, simpleReturnString)
    val firstString: String = resultList.head
    val secondString: String = resultList.tail.head
    println("full: \n" + firstString + "\n")
    println("Simple:" + secondString + "\n")
    resultList
  }

  "A resultpage" should "highlight in the proper colour when exceeding metric thresholds" in {
    val urlList: List[String] = List("Doesnt","Matter","Whats","in","Here")
    var firstString = ""
    var secondString = ""
    val testResults: List[List[String]] = urlList.map(url => returnResultsString(url))
    // Add results to a single string so that we only need to write to S3 once (S3 will only take complete objects).
    val firstStringList: List[String] = testResults.map(x => x.head)
    val secondStringList : List[String] = testResults.map(x => x.tail.head)
    firstString = firstString.concat(firstStringList.mkString)
    secondString = secondString.concat(secondStringList.mkString)
    println("\n*******************  Returned simple String  ***********************************************************\n")
    println(secondString)
    assert(secondString.contains(warningColor))
    assert(secondString.contains(alertColor))
  }
  }
