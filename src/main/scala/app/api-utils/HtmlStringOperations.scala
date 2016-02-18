package app.apiutils

import org.joda.time.DateTime


/**
 * Created by mmcnamara on 10/02/16.
 */
class HtmlStringOperations(average: String, warning: String, alert: String, liveBlogResultsUrl: String, interactiveResultsUrl: String) {

  val averageColor = average
  val warningColor = warning
  val alertColor = alert

  val hTMLPageHeader: String = "<!DOCTYPE html>\n<html>\n<body>\n"
  val hTMLTitleLiveblog: String = "<h1>Currrent Performance of today's Liveblogs</h1>"
  val hTMLTitleInteractive: String = "<h1>Currrent Performance of today's Interactives</h1>"
  val hTMLJobStarted: String = "<p>Job started at: " + DateTime.now + "\n</p>"
  val hTMLFullTableHeaders: String = "<table border=\"1\">\n<tr bgcolor=" + averageColor + ">\n<th>Time Last Tested</th>\n<th>Test Type</th>\n<th>Article Url</th>\n<th>Time to First Paint</th>\n<th>Time to Document Complete</th>\n<th>MB transferred at Document Complete</th>\n<th>Time to Fully Loaded</th>\n<th>MB transferred at Fully Loaded</th>\n<th>US Prepaid Cost $US0.097 per MB</th>\n<th>US Postpaid Cost $US0.065 per MB</th>\n<th>Speed Index</th>\n<th>Status</th>\n</tr>\n"
  val hTMLSimpleTableHeaders: String = "<table border=\"1\">\n<tr bgcolor=" + averageColor + ">\n<th>Time Last Tested</th>\n<th>Test Type</th>\n<th>Article Url</th>\n<th>Time to Document Complete</th>\n<th>MB transferred</th>\n<th>US Prepaid Cost $US0.097 per MB</th>\n<th>US Postpaid Cost $US0.065 per MB</th>\n<th>Speed Index</th>\n<th>Status</th>\n</tr>\n"
  val hTMLTableFooters: String = "</table>"
  val hTMLPageFooterStart: String = "\n<p><i>Job completed at: "
  val hTMLPageFooterEnd: String = "</i></p>\n</body>\n</html>"
  //  var results: String = hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted + hTMLTableHeaders
  //  var simplifiedResults: String = hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted + hTMLSimpleTableHeaders
  val liveBlogResultsPage: String = liveBlogResultsUrl
  val interactiveResultsPage: String = interactiveResultsUrl



  def generateHTMLRow(resultsObject: PerformanceResultsObject): String = {
    var returnString: String = ""
    //  Define new web-page-test API request and send it the url to test
    //  Add results to string which will eventually become the content of our results file

    if (resultsObject.typeOfTest == "Desktop") {
      if (resultsObject.warningStatus) {
        if (resultsObject.alertStatus) {
          println("row should be red one of the items qualifies")
          returnString = returnString.concat("<tr bgcolor=" + alertColor + "><td>" + DateTime.now + "</td><td>Desktop</td>" + resultsObject.toHTMLSimpleTableCells() + "</tr>")
        }
        else {
          println("row should be yellow one of the items qualifies")
          returnString = returnString.concat("<tr bgcolor=" + warningColor + "><td>" + DateTime.now + "</td><td>Desktop</td>" + resultsObject.toHTMLSimpleTableCells() + "</tr>")
        }
      }
      else {
        println("all fields within size limits")
        returnString = returnString.concat("<tr><td>" + DateTime.now + "</td><td>Desktop</td>" + resultsObject.toHTMLSimpleTableCells() + "</tr>")
      }
    }
    else {
      if (resultsObject.warningStatus) {
        if (resultsObject.alertStatus) {
          println("row should be red one of the items qualifies")
          returnString = returnString.concat("<tr bgcolor=" + alertColor + "><td>" + DateTime.now + "</td><td>Android/3G</td>" + resultsObject.toHTMLSimpleTableCells() + "</tr>")
        }
        else {
          println("row should be yellow one of the items qualifies")
          returnString = returnString.concat("<tr bgcolor=" + warningColor + "><td>" + DateTime.now + "</td><td>Android/3G</td>" + resultsObject.toHTMLSimpleTableCells() + "</tr>")
        }
      }
      else {
        println("no alerts")
        returnString = returnString.concat("<tr><td>" + DateTime.now + "</td><td>Android/3G</td>" + resultsObject.toHTMLSimpleTableCells() + "</tr>")
      }
    }
    println(DateTime.now + " returning results string to main thread")
    println(returnString)
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


  def generateAlertEmailHeadings(): String = {
    val messageString: String = hTMLPageHeader +
      "<h1>Page performance alerts</h1><p>\nThe following items have been found to either take too long to load or cost too much to view on either a desktop or mobile browser</p>\n"
    messageString
  }

  def generateLiveBlogAlertHeadings(): String = {
    "<h2>Live Blob Performance Alerts</h2>\n"
  }

  def generateLiveBlogAlertFooter(): String = {
    "<p>All alerts have been confirmed by retesting multiple times. Tests were run without ads so all page weight is due to content\n</p>" +
    "<p>Full results can be viewed <a href=" + liveBlogResultsPage + ">here</a></p>"
  }

  def generateInteractiveAlertHeadings(): String = {
    "<h2>Interactive Performance Alerts</h2>\n"
  }

  def generateInteractiveAlertFooter(): String = {
    "<p>All alerts have been confirmed by retesting multiple times. Tests were run without ads so all page weight is due to content\n</p>" +
    "<p>Full results can be viewed <a href=" + interactiveResultsPage + ">here</a></p>"
  }


  def generateAlertEmailBodyElement(alertList: List[PerformanceResultsObject], averages: PageAverageObject): String = {
    if(alertList.nonEmpty) {
      val desktopMessageString: String =
        if (alertList.exists(test => test.typeOfTest == "Desktop")) {
          "<h2>Desktop Alerts</h2>" +
            "<p>The following items have been found to either take too long to load or cost too much to view on a desktop browser</p>\n" +
            this.hTMLSimpleTableHeaders + "\n" +
            averages.desktopHTMLResultString + "\n" +
            (for (test <- alertList if test.typeOfTest == "Desktop") yield "<tr>" + test.toHTMLSimpleTableCells() + "</tr>") +
            this.hTMLTableFooters
        }
        else {
          ""
        }

      val mobileMessageString: String =
        if (alertList.exists(test => test.typeOfTest == "Android/3G")) {
          "<h2>Mobile Alerts</h2>" +
            "<p>The following items have been found to either take too long to load or cost too much to view on a mobile device</p>\n" +
            this.hTMLSimpleTableHeaders + "\n" +
            averages.mobileHTMLResultString + "\n" +
            (for (test <- alertList if test.typeOfTest == "Android/3G") yield test.toHTMLSimpleTableCells()) + this.hTMLTableFooters +
            this.closeTable +
            "<p>All alerts have been confirmed by retesting multiple times. Tests were run without ads so all page weight is due to content</p>"
        }
        else {
          ""
        }
      val returnString: String = desktopMessageString + mobileMessageString
      returnString
    } else {
        ""
      }
  }


  def generateFullAlertEmailBody(liveBlogReport: String, interactiveReport: String): String = {

    val liveBlogElement: String = {
      if(liveBlogReport.length > 0){
        generateLiveBlogAlertHeadings() +
          liveBlogReport+
        generateLiveBlogAlertFooter()}
      else {
        "<p> All LiveBlog pages are performing within acceptable bounds.\n</p>" +
        generateLiveBlogAlertFooter()
      }
    }

    val interactiveElement: String = {
      if(interactiveReport.length > 0){
        generateInteractiveAlertHeadings() +
          interactiveReport +
        generateInteractiveAlertFooter()}
      else {
        "<p> All Interactive pages are performing within acceptable bounds.\n</p>" +
        generateInteractiveAlertFooter()
      }
    }

    val fullEmailBody: String = {
      generateAlertEmailHeadings +
      liveBlogElement +
      interactiveElement +
      closePage
    }
    fullEmailBody
  }
}