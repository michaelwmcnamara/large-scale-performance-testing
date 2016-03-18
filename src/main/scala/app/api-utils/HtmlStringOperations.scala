package app.apiutils

import org.joda.time.DateTime


/**
 * Created by mmcnamara on 10/02/16.
 */
class HtmlStringOperations(average: String, warning: String, alert: String, liveBlogResultsUrl: String, interactiveResultsUrl: String, frontsResultsUrl: String) {

  val averageColor = average
  val warningColor = warning
  val alertColor = alert

  val hTMLPageHeader: String = "<!DOCTYPE html>\n<html>\n<body>\n"
  val hTMLTitleLiveblog: String = "<h1>Currrent Performance of today's Liveblogs</h1>"
  val hTMLTitleInteractive: String = "<h1>Currrent Performance of today's Interactives</h1>"
  val hTMLTitleFronts: String = "<h1>Currrent Performance of today's Fronts</h1>"
  val hTMLJobStarted: String = "<p>Job started at: " + DateTime.now + "\n</p>"
  val hTMLFullTableHeaders: String = "<table border=\"1\">\n<tr bgcolor=" + averageColor + ">\n<th>Time Last Tested</th>\n<th>Test Type</th>\n<th>Article Url</th>\n<th>Time to First Paint</th>\n<th>Time to Document Complete</th>\n<th>MB transferred at Document Complete</th>\n<th>Time to Fully Loaded</th>\n<th>MB transferred at Fully Loaded</th>\n<th>US Prepaid Cost $US0.097 per MB</th>\n<th>US Postpaid Cost $US0.065 per MB</th>\n<th>Speed Index</th>\n<th>Status</th>\n</tr>\n"
  val hTMLSimpleTableHeaders: String = "<table border=\"1\">\n<tr bgcolor=" + averageColor + ">\n<th>Time Last Tested</th>\n<th>Test Type</th>\n<th>Article Url</th>\n<th>Time to Page Scrollable</th>\n<th>Time to rendering above the fold complete </th>\n<th>MB transferred</th>\n<th>US Prepaid Cost $US0.097 per MB</th>\n<th>US Postpaid Cost $US0.065 per MB</th>\n<th>Status</th>\n</tr>\n"
  val hTMLInteractiveTableHeaders: String = "<table border=\"1\">\n<tr bgcolor=" + averageColor + ">\n<th>Time Last Tested</th>\n<th>Test Type</th>\n<th>Article Url</th>\n<th>Time to Page Scrollable</th>\n<th>Time to rendering above the fold complete </th>\n<th>MB transferred</th>\n<th>Status</th>\n</tr>\n"
  val hTMLAlertTableHeaders: String = "<table border=\"1\">\n<tr bgcolor=" + averageColor + ">\n<th>Article Url</th>\n<th>Test Type</th>\n<th>Status</th>\n</tr>\n"
  val hTMLTableFooters: String = "</table>"
  val hTMLPageFooterStart: String = "\n<p><i>Job completed at: "
  val hTMLPageFooterEnd: String = "</i></p>\n</body>\n</html>"
  //  var results: String = hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted + hTMLTableHeaders
  //  var simplifiedResults: String = hTMLPageHeader + hTMLTitleLiveblog + hTMLJobStarted + hTMLSimpleTableHeaders
  val liveBlogResultsPage: String = liveBlogResultsUrl
  val interactiveResultsPage: String = interactiveResultsUrl
  val frontsResultsPage: String = frontsResultsUrl


  def generateHTMLRow(resultsObject: PerformanceResultsObject): String = {
    var returnString: String = ""
    //  Define new web-page-test API request and send it the url to test
    //  Add results to string which will eventually become the content of our results file

      if (resultsObject.warningStatus) {
        if (resultsObject.alertStatus) {
          println("row should be red one of the items qualifies")
          returnString = "<tr bgcolor=" + alertColor + ">" + resultsObject.toHTMLSimpleTableCells() + "</tr>"
        }
        else {
          println("row should be yellow one of the items qualifies")
          returnString = "<tr bgcolor=" + warningColor + ">" + resultsObject.toHTMLSimpleTableCells() + "</tr>"
        }
      }
      else {
        println("all fields within size limits")
        returnString = "<tr>" + resultsObject.toHTMLSimpleTableCells() + "</tr>"
      }
    println(DateTime.now + " returning results string to main thread")
    println(returnString)
    returnString

  }

  def interactiveHTMLRow(resultsObject: PerformanceResultsObject): String = {
    var returnString: String = ""
    //  Define new web-page-test API request and send it the url to test
    //  Add results to string which will eventually become the content of our results file

    if (resultsObject.warningStatus) {
      if (resultsObject.alertStatus) {
        println("row should be red one of the items qualifies")
        returnString = "<tr bgcolor=" + alertColor + ">" + resultsObject.toHTMLInteractiveTableCells() + "</tr>"
      }
      else {
        println("row should be yellow one of the items qualifies")
        returnString = "<tr bgcolor=" + warningColor + ">" + resultsObject.toHTMLInteractiveTableCells() + "</tr>"
      }
    }
    else {
      println("all fields within size limits")
      returnString = "<tr>" + resultsObject.toHTMLInteractiveTableCells() + "</tr>"
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

  def initialisePageForFronts: String = {
    hTMLPageHeader + hTMLTitleFronts + hTMLJobStarted
  }

  def initialiseTable: String = {
    hTMLSimpleTableHeaders
  }

  def interactiveTable: String = {
    hTMLSimpleTableHeaders
  }

  def closeTable: String = {
    hTMLTableFooters
  }

  def closePage: String = {
    hTMLPageFooterStart + DateTime.now() + hTMLPageFooterEnd
  }

  def generalAlertEmailHeadings(): String = {
    val messageString: String = hTMLPageHeader +
      "<h1>Page performance alerts</h1>"
    messageString
  }

  def interactiveAlertEmailHeadings(): String = {
    val messageString: String = hTMLPageHeader +
      "<h1>Interactive performance alerts</h1>\n"
    messageString
  }

  def generateLiveBlogAlertHeadings(): String = {
    "<h2>Live Blog Performance Alerts</h2>\n"
  }

  def generateLiveBlogAlertFooter(): String = {
    "<p>All alerts have been confirmed by retesting multiple times. Tests were run without ads so all page weight is due to content\n</p>" +
    "<p>Full results for LiveBlogs can be viewed <a href=" + liveBlogResultsPage + ">here</a></p>"
  }

  def generateInteractiveAlertHeadings(): String = {
    "<h2>Interactive Performance Alerts</h2>\n"
  }

  def generateInteractiveAlertFooter(): String = {
    "<p>All alerts have been confirmed by retesting multiple times. Tests were run without ads so all page weight is due to content\n</p>" +
    "<p>Full results for Interactives can be viewed <a href=" + interactiveResultsPage + ">here</a></p>"
  }

  def generateFrontsAlertHeadings(): String = {
    "<h2>Fronts Performance Alerts</h2>\n"
  }

  def generateFrontsAlertFooter(): String = {
    "<p>All alerts have been confirmed by retesting multiple times. Tests were run without ads so all page weight is due to content\n</p>" +
      "<p>Full results for Fronts can be viewed <a href=" + frontsResultsPage + ">here</a></p>"
  }



  def generateAlertEmailBodyElement(alertList: List[PerformanceResultsObject], averages: PageAverageObject): String = {
//    println("*\n \n \n **** \n \n \n averages.desktopHTMLResultString: \n" + averages.desktopHTMLResultString)
//    println("*\n \n \n **** \n \n \n averages.mobileHTMLResultString: \n" + averages.mobileHTMLResultString)
    if(alertList.nonEmpty) {
      val desktopMessageString: String =
        if (alertList.exists(test => test.typeOfTest == "Desktop")) {
          "<h2>Desktop Alerts</h2>" +
            "<p>The following items have been found to either take too long to load or cost too much to view on a desktop browser</p>\n" +
            this.hTMLAlertTableHeaders + "\n" +
            (for (test <- alertList if test.typeOfTest == "Desktop") yield "<tr>" + test.toHTMLAlertMessageCells() + "</tr>").mkString +
            this.hTMLTableFooters
        }
        else {
          ""
        }

      val mobileMessageString: String =
        if (alertList.exists(test => test.typeOfTest == "Android/3G")) {
          "<h2>Mobile Alerts</h2>" +
            "<p>The following items have been found to either take too long to load or cost too much to view on a mobile device</p>\n" +
            this.hTMLAlertTableHeaders + "\n" +
            (for (test <- alertList if test.typeOfTest == "Android/3G") yield test.toHTMLAlertMessageCells() + "</tr>").mkString +
            this.hTMLTableFooters

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


  def generalAlertFullEmailBody(liveBlogReport: String, interactiveReport: String, frontsReport: String): String = {

    val liveBlogElement: String = {
      if(liveBlogReport.contains("<tr")){
        generateLiveBlogAlertHeadings() +
          liveBlogReport+
        generateLiveBlogAlertFooter()}
      else {
        ""
      }
    }

    val frontsElement: String = {
      if(frontsReport.contains("<tr")){
        generateFrontsAlertHeadings() +
        frontsReport +
          generateFrontsAlertFooter()}
      else {
        ""
      }
    }


    val fullEmailBody: String = {
      generalAlertEmailHeadings +
      liveBlogElement +
      frontsElement +
      closePage
    }
    fullEmailBody
  }

  def interactiveAlertFullEmailBody(interactiveReport: String): String = {
    
    val interactiveElement: String = {
      if(interactiveReport.contains("<tr")){
        generateInteractiveAlertHeadings() +
          interactiveReport +
          generateInteractiveAlertFooter()}
      else {
        ""
      }
    }

    val fullEmailBody: String = {
      generalAlertEmailHeadings +
        interactiveElement +
        closePage
    }
    fullEmailBody
  }



}