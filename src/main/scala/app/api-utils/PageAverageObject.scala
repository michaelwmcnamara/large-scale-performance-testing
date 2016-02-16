package app.apiutils

import org.joda.time.DateTime


/**
 * Created by mmcnamara on 10/02/16.
 */

class PageAverageObject(dtfp: Int, dtdc: Int, dsdc: Int, dtfl: Int, dsfl: Int, dcflprepaid: Double, dcflpostpaid: Double, dsi: Int, dsc: Int, mtfp: Int, mtdc: Int, msdc: Int, mtfl: Int, msfl: Int, mcflprepaid: Double, mcflpostpaid: Double, msi: Int, msc: Int, resultString: String) {

  val desktopTimeFirstPaint: Int = dtfp
  val desktopTimeDocComplete: Int = dtdc
  val desktopKBInDocComplete: Int = dsdc
  val desktopTimeFullyLoaded: Int = dtfl
  val desktopKBInFullyLoaded: Int = dsfl
  val desktopEstUSPrePaidCost: Double = dcflprepaid
  val desktopEstUSPostPaidCost: Double = dcflpostpaid
  val desktopSpeedIndex: Int = dsi
  val desktopSuccessCount = dsc

  val mobileTimeFirstPaint: Int = mtfp
  val mobileTimeDocComplete: Int = mtdc
  val mobileKBInDocComplete: Int = msdc
  val mobileTimeFullyLoaded: Int = mtfl
  val mobileKBInFullyLoaded: Int = msfl
  val mobileEstUSPrePaidCost: Double = mcflprepaid
  val mobileEstUSPostPaidCost: Double = mcflpostpaid
  val mobileSpeedIndex: Int = msi
  val mobileSuccessCount = msc

  val formattedHTMLResultString: String = resultString


  val desktopTimeFirstPaint80thPercentile: Int = (desktopTimeFirstPaint * 80) / 100
  val desktopTimeDocComplete80thPercentile: Int = (desktopTimeDocComplete * 80) / 100
  val desktopKBInDocComplete80thPercentile: Int = (desktopKBInDocComplete * 80) / 100
  val desktopTimeFullyLoaded80thPercentile: Int = (desktopTimeFullyLoaded * 80) / 100
  val desktopKBInFullyLoaded80thPercentile: Int = (desktopKBInFullyLoaded * 80) / 100
  val desktopEstUSPrePaidCost80thPercentile: Double = (desktopEstUSPrePaidCost * 80) / 100
  val desktopEstUSPostPaidCost80thPercentile: Double = (desktopEstUSPostPaidCost * 80) / 100
  val desktopSpeedIndex80thPercentile: Int = (desktopSpeedIndex * 80) / 100
  val mobileTimeFirstPaint80thPercentile: Int = (mobileTimeFirstPaint * 80) / 100
  val mobileTimeDocComplete80thPercentile: Int = (mobileTimeDocComplete * 80) / 100
  val mobileKBInDocComplete80thPercentile: Int = (mobileKBInDocComplete * 80) / 100
  val mobileTimeFullyLoaded80thPercentile: Int = (mobileTimeFullyLoaded * 80) / 100
  val mobileKBInFullyLoaded80thPercentile: Int = (mobileKBInFullyLoaded * 80) / 100
  val mobileEstUSPrePaidCost80thPercentile: Double = (mobileEstUSPrePaidCost * 80) / 100
  val mobileEstUSPostPaidCost80thPercentile: Double = (mobileEstUSPostPaidCost * 80) / 100
  val mobileSpeedIndex80thPercentile: Int = (mobileSpeedIndex * 80) / 100


  def this() {
    this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "")
  }

  def toHTMLString: String = formattedHTMLResultString

  def roundAt(p: Int)(n: Double): Double = { val s = math pow (10, p); (math round n * s) / s}
}


class LiveBlogDefaultAverages extends PageAverageObject() {
  override val desktopTimeFirstPaint: Int = 1
  override val desktopTimeDocComplete: Int = 15
  override val desktopKBInDocComplete: Int = 10000
  override val desktopTimeFullyLoaded: Int = 20
  override val desktopKBInFullyLoaded: Int = 15000
  override val desktopEstUSPrePaidCost: Double = 60.0
  override val desktopEstUSPostPaidCost: Double = 50.0
  override val desktopSpeedIndex: Int = 5000
  override val desktopSuccessCount = 1

  override val mobileTimeFirstPaint: Int = 1
  override val mobileTimeDocComplete: Int = 15
  override val mobileKBInDocComplete: Int = 6000
  override val mobileTimeFullyLoaded: Int = 20
  override val mobileKBInFullyLoaded: Int = 6000
  override val mobileEstUSPrePaidCost: Double = 40.0
  override val mobileEstUSPostPaidCost: Double = 30.0
  override val mobileSpeedIndex: Int = 5000
  override val mobileSuccessCount = 1

  override val formattedHTMLResultString: String = "\"<tr bgcolor=\"A9BCF5\">" +
    "<td>" + DateTime.now + "</td>" +
    "<td>Desktop</td>" +
    "<td> Alerting thresholds determined by past liveblogs we have migrated</td>" +
    "<td>" + desktopTimeDocComplete + "</td>" +
    "<td>" + desktopKBInDocComplete + "</td>" +
    "<td>" + desktopEstUSPrePaidCost + "</td>" +
    "<td>" + desktopEstUSPostPaidCost + "</td>" +
    "<td>" + desktopSpeedIndex + "</td>" +
    "<td>Predefined standards</td></tr>" +
    "\"<tr bgcolor=\"A9BCF5\">" +
    "<td>" + DateTime.now + "</td>" +
    "<td>Mobile</td>" +
    "<td> Yellow indicates within danger zone of threshold. Red indicates threshold has been crossed </td>" +
    "<td>" + mobileTimeDocComplete + "</td>" +
    "<td>" + mobileKBInDocComplete + "</td>" +
    "<td>" + mobileEstUSPrePaidCost + "</td>" +
    "<td>" + mobileEstUSPostPaidCost + "</td>" +
    "<td>" + mobileSpeedIndex + "</td>" +
    "<td>Predefined standards</td></tr>"
}

class GeneratedPageAverages(resultsList: List[Array[PerformanceResultsObject]]) extends PageAverageObject{
  var accumulatorDesktopTimeFirstPaint: Int = 0
  var accumulatorDesktopTimeDocComplete: Int = 0
  var accumulatorDesktopKBInDocComplete: Int = 0
  var accumulatorDesktopTimeFullyLoaded: Int = 0
  var accumulatorDesktopKBInFullyLoaded: Int = 0
  var accumulatorDesktopEstUSPrePaidCost: Double = 0
  var accumulatorDesktopEstUSPostPaidCost: Double = 0
  var accumulatorDesktopSpeedIndex: Int = 0
  var accumulatorDesktopSuccessCount = 0

  var accumulatorMobileTimeFirstPaint: Int = 0
  var accumulatorMobileTimeDocComplete: Int = 0
  var accumulatorMobileKBInDoccomplete: Int = 0
  var accumulatorMobileTimeFullyLoaded: Int = 0
  var accumulatorMobileKBInFullyLoaded: Int = 0
  var accumulatorMobileEstUSPrePaidCost: Double = 0
  var accumulatorMobileEstUSPostPaidCost: Double = 0
  var accumulatorMobileSpeedIndex: Int = 0
  var accumulatorMobileSuccessCount = 0

  var accumulatorString: String = ""

  val multipleLiveBlogs:String = "liveblogs that were migrated due to size"
  val singleLiveBlog: String = "Example of a liveblog migrated due to size"
  val noLiveBlogs: String = "All tests of migrated liveblogs failed"

  val multipleInteractives:String = "interactives with known size or performance issues"
  val singleInteractive: String = "Example of an interactive with known size or performance issues"
  val noInteractives: String = "All tests of interactives with size or performance issues have failed"

  //process result objects
  resultsList.foreach(result => {
  if (result(0).resultStatus == "Test Success") {
    accumulatorDesktopTimeFirstPaint += result(0).timeFirstPaint / 1000
    accumulatorDesktopTimeDocComplete += result(0).timeDocComplete / 1000
    accumulatorDesktopKBInDocComplete += result(0).bytesInDocComplete / 1024
    accumulatorDesktopTimeFullyLoaded += result(0).timeFullyLoaded / 1000
    accumulatorDesktopKBInFullyLoaded += result(0).bytesInFullyLoaded / 1024
    accumulatorDesktopEstUSPrePaidCost += result(0).estUSPrePaidCost
    accumulatorDesktopEstUSPostPaidCost += result(0).estUSPostPaidCost
    accumulatorDesktopSpeedIndex += result(0).speedIndex
    accumulatorDesktopSuccessCount += 1
  }
  if (result(1).resultStatus == "Test Success") {
    accumulatorMobileTimeFirstPaint += result(1).timeFirstPaint/1000
    accumulatorMobileTimeDocComplete += result(1).timeDocComplete/1000
    accumulatorMobileKBInDoccomplete += result(1).bytesInDocComplete/1024
    accumulatorMobileTimeFullyLoaded += result(1).timeFullyLoaded/1000
    accumulatorMobileKBInFullyLoaded += result(1).bytesInFullyLoaded/1024
    accumulatorMobileEstUSPrePaidCost += result(0).estUSPrePaidCost
    accumulatorMobileEstUSPostPaidCost += result(0).estUSPostPaidCost
    accumulatorMobileSpeedIndex += result(1).speedIndex
    accumulatorMobileSuccessCount += 1
  }
  })



  override val desktopTimeFirstPaint: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopTimeFirstPaint/accumulatorDesktopSuccessCount} else 1
  override val desktopTimeDocComplete: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopTimeDocComplete/accumulatorDesktopSuccessCount} else 15
  override val desktopKBInDocComplete: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopKBInDocComplete/accumulatorDesktopSuccessCount} else 10000
  override val desktopTimeFullyLoaded: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopTimeFullyLoaded/accumulatorDesktopSuccessCount} else 20
  override val desktopKBInFullyLoaded: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopKBInFullyLoaded/accumulatorDesktopSuccessCount} else 15000
  override val desktopEstUSPrePaidCost: Double = if (accumulatorDesktopSuccessCount > 0) {roundAt(2)(accumulatorDesktopEstUSPrePaidCost/accumulatorDesktopSuccessCount)} else 60.00
  override val desktopEstUSPostPaidCost: Double = if (accumulatorDesktopSuccessCount > 0) {roundAt(2)(accumulatorDesktopEstUSPostPaidCost/accumulatorDesktopSuccessCount)} else 50.00
  override val desktopSpeedIndex: Int = if (accumulatorDesktopSuccessCount > 0) {accumulatorDesktopSpeedIndex/accumulatorDesktopSuccessCount} else 5000
  override val desktopSuccessCount: Int = accumulatorDesktopSuccessCount

  override val mobileTimeFirstPaint: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileTimeFirstPaint/accumulatorMobileSuccessCount} else 1
  override val mobileTimeDocComplete: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileTimeDocComplete/accumulatorMobileSuccessCount} else 15
  override val mobileKBInDocComplete: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileKBInDoccomplete/accumulatorMobileSuccessCount} else 6000
  override val mobileTimeFullyLoaded: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileTimeFullyLoaded/accumulatorMobileSuccessCount} else 20
  override val mobileKBInFullyLoaded: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileKBInFullyLoaded/accumulatorMobileSuccessCount} else 6000
  override val mobileEstUSPrePaidCost: Double = if (accumulatorMobileSuccessCount > 0) {roundAt(2)(accumulatorMobileEstUSPrePaidCost/accumulatorMobileSuccessCount)} else 40.00
  override val mobileEstUSPostPaidCost: Double = if (accumulatorMobileSuccessCount > 0) {roundAt(2)(accumulatorMobileEstUSPostPaidCost/accumulatorMobileSuccessCount)} else 30.00
  override val mobileSpeedIndex: Int = if (accumulatorMobileSuccessCount > 0) {accumulatorMobileSpeedIndex/accumulatorMobileSuccessCount} else 5000
  override val mobileSuccessCount: Int = accumulatorMobileSuccessCount

  accumulatorString = accumulatorString.concat("<tr bgcolor=\"#A9BCF5\"><td>" + DateTime.now + "</td><td>Desktop</td>")

  //add desktop averaages to return string
  if(accumulatorDesktopSuccessCount > 1){
    accumulatorString = accumulatorString.concat("<td>" + "Average of " + accumulatorDesktopSuccessCount + "pages  with recognised size issues</td>"
      + "<td>" + desktopTimeDocComplete + "s</td>"
      + "<td>" + desktopKBInFullyLoaded + "kB</td>"
      + "<td> $(US)" + desktopEstUSPrePaidCost + "</td>"
      + "<td> $(US)" + desktopEstUSPostPaidCost + "</td>"
      + "<td>" + desktopSpeedIndex + "</td>"
      + "<td>" + desktopSuccessCount + " urls Tested Successfully</td></tr>"
    )}
  else{
    if (accumulatorDesktopSuccessCount == 1) {
      accumulatorString = accumulatorString.concat("<td> Results from 1 page with recognised size issues</td>"
        + "<td>" + desktopTimeDocComplete + "s</td>"
        + "<td>" + desktopKBInFullyLoaded + "kB</td>"
        + "<td> $(US)" + desktopEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + desktopEstUSPostPaidCost + "</td>"
        + "<td>" + desktopSpeedIndex + "</td>"
        + "<td>" + desktopSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
    else {
      accumulatorString = accumulatorString.concat("<td> Standard values to be used for judging page size</td>"
        + "<td>" + desktopTimeDocComplete + "s</td>"
        + "<td>" + desktopKBInFullyLoaded + "kB</td>"
        + "<td> $(US)" + desktopEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + desktopEstUSPostPaidCost + "</td>"
        + "<td>" + desktopSpeedIndex + "</td>"
        + "<td>" + desktopSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
  }
  
  //add mobile averages to return string
  accumulatorString = accumulatorString.concat("<tr bgcolor=\"#A9BCF5\"><td>" + DateTime.now + "</td><td>Android/3G</td>")
  if(accumulatorMobileSuccessCount > 1){
    accumulatorString = accumulatorString.concat("<td>" + "Average of " + accumulatorDesktopSuccessCount + "pages  with recognised size issues</td>"
      + "<td>" + mobileTimeDocComplete + "s</td>"
      + "<td>" + mobileKBInFullyLoaded + "kB</td>"
      + "<td> $(US)" + mobileEstUSPrePaidCost + "</td>"
      + "<td> $(US)" + mobileEstUSPostPaidCost + "</td>"
      + "<td>" + mobileSpeedIndex + "</td>"
      + "<td>" + mobileSuccessCount + " urls Tested Successfully</td></tr>"
    )}
  else{
    if (accumulatorMobileSuccessCount == 1) {
      accumulatorString = accumulatorString.concat("<td> Results from 1 page with recognised size issues</td>"
        + "<td>" + mobileTimeDocComplete + "s</td>"
        + "<td>" + mobileKBInFullyLoaded + "kB</td>"
        + "<td> $(US)" + mobileEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + mobileEstUSPostPaidCost + "</td>"
        + "<td>" + mobileSpeedIndex + "</td>"
        + "<td>" + mobileSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
    else {
      accumulatorString = accumulatorString.concat("<td> Standard values to be used for judging page size</td>"
        + "<td>" + mobileTimeDocComplete + "s</td>"
        + "<td>" + mobileKBInFullyLoaded + "kB</td>"
        + "<td> $(US)" + mobileEstUSPrePaidCost + "</td>"
        + "<td> $(US)" + mobileEstUSPostPaidCost + "</td>"
        + "<td>" + mobileSpeedIndex + "</td>"
        + "<td>" + mobileSuccessCount + " urls Tested Successfully</td></tr>"
      )
    }
  }

  override val formattedHTMLResultString: String = accumulatorString
}
