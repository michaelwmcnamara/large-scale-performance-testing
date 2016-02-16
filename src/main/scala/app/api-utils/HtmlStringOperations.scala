package app.apiutils

import org.joda.time.DateTime


/**
 * Created by mmcnamara on 10/02/16.
 */
class HtmlStringOperations(average: String, warning: String, alert: String) {

  val averageColor = average
  val warningColor = warning
  val alertColor = alert

  val hTMLPageHeader:String = "<!DOCTYPE html>\n<html>\n<body>\n"
  val hTMLTitleLiveblog:String = "<h1>Currrent Performance of today's Liveblogs</h1>"
  val hTMLTitleInteractive:String = "<h1>Currrent Performance of today's Interactives</h1>"
  val hTMLJobStarted: String = "<p>Job started at: " + DateTime.now + "\n</p>"
  val hTMLFullTableHeaders:String = "<table border=\"1\">\n<tr bgcolor=" +averageColor +">\n<th>Time Last Tested</th>\n<th>Test Type</th>\n<th>Article Url</th>\n<th>Time to First Paint</th>\n<th>Time to Document Complete</th>\n<th>MB transferred at Document Complete</th>\n<th>Time to Fully Loaded</th>\n<th>MB transferred at Fully Loaded</th>\n<th>US Prepaid Cost $US0.097 per MB</th>\n<th>US Postpaid Cost $US0.065 per MB</th>\n<th>Speed Index</th>\n<th>Status</th>\n</tr>\n"
  val hTMLSimpleTableHeaders:String = "<table border=\"1\">\n<tr bgcolor="+ averageColor +">\n<th>Time Last Tested</th>\n<th>Test Type</th>\n<th>Article Url</th>\n<th>Time to Document Complete</th>\n<th>MB transferred</th>\n<th>US Prepaid Cost $US0.097 per MB</th>\n<th>US Postpaid Cost $US0.065 per MB</th>\n<th>Speed Index</th>\n<th>Status</th>\n</tr>\n"
  val hTMLTableFooters:String = "</table>"
  val hTMLPageFooterStart: String =  "\n<p><i>Job completed at: "
  val hTMLPageFooterEnd: String = "</i></p>\n</body>\n</html>"
//  var results: String = hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted + hTMLTableHeaders
//  var simplifiedResults: String = hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted + hTMLSimpleTableHeaders


  def generateHTMLRow(resultsObject: PerformanceResultsObject): String = {
    var returnString: String = ""
    //  Define new web-page-test API request and send it the url to test
    //  Add results to string which will eventually become the content of our results file

    if (resultsObject.typeOfTest == "Desktop") {
      if (resultsObject.warningStatus) {
        if (resultsObject.alertStatus) {
          println ("row should be red one of the items qualifies")
          returnString = returnString.concat ("<tr bgcolor=" + alertColor + "><td>" + DateTime.now + "</td><td>Desktop</td>" + resultsObject.toHTMLSimpleTableCells () + "</tr>")
        }
        else {
          println ("row should be yellow one of the items qualifies")
          returnString = returnString.concat ("<tr bgcolor=" + warningColor + "><td>" + DateTime.now + "</td><td>Desktop</td>" + resultsObject.toHTMLSimpleTableCells () + "</tr>")
        }
      }
      else {
        println ("all fields within size limits")
        returnString = returnString.concat ("<tr><td>" + DateTime.now + "</td><td>Desktop</td>" + resultsObject.toHTMLSimpleTableCells () + "</tr>")
      }
    }
    else {
      if (resultsObject.warningStatus) {
        if (resultsObject.alertStatus) {
          println ("row should be red one of the items qualifies")
          returnString = returnString.concat ("<tr bgcolor=" + alertColor + "><td>" + DateTime.now + "</td><td>Android/3G</td>" + resultsObject.toHTMLSimpleTableCells () + "</tr>")
        }
        else {
          println ("row should be yellow one of the items qualifies")
          returnString = returnString.concat ("<tr bgcolor=" + warningColor + "><td>" + DateTime.now + "</td><td>Android/3G</td>" + resultsObject.toHTMLSimpleTableCells () + "</tr>")
        }
      }
      else {
        println ("no alerts")
        returnString = returnString.concat ("<tr><td>" + DateTime.now + "</td><td>Android/3G</td>" + resultsObject.toHTMLSimpleTableCells () + "</tr>")
      }
    }
    println (DateTime.now + " returning results string to main thread")
    println (returnString)
    returnString

  }

  def initialisePageForLiveblog: String = {
    hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted
  }

  def initialisePageForInteractive: String = {
    hTMLPageHeader + hTMLTitleInteractive + hTMLJobStarted
  }

  def initialiseTable: String = {
    hTMLSimpleTableHeaders
  }

  def closeTable: String = {
    hTMLTableFooters
  }

  def closePage: String = {
    hTMLPageFooterStart + DateTime.now() + hTMLPageFooterEnd
  }
}
