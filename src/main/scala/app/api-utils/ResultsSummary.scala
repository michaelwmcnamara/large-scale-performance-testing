package app.api

import app.apiutils.PerformanceResultsObject


/**
 * Created by mmcnamara on 31/03/16.
 */
class ResultsSummary(resultsList: List[PerformanceResultsObject]) {

  val desktopAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Desktop") && !element.brokenTest && element.adsDisplayed) yield element
  val mobileAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Android") && !element.brokenTest && element.adsDisplayed) yield element
  val desktopNoAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Desktop") && !element.brokenTest && !element.adsDisplayed) yield element
  val mobileNoAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Android") && !element.brokenTest && !element.adsDisplayed) yield element

  println("\ndesktopAdsResultsList Length = " + desktopAdsResultsList.length + "\n")
  println("\nmobileAdsResultsList Length = " + mobileAdsResultsList.length + "\n")
  println("\ndesktopNoAdsResultsList Length = " + desktopNoAdsResultsList.length + "\n")
  println("\nmobileNoAdsResultsList Length = " + mobileNoAdsResultsList.length + "\n")

  val desktopAdsTimeToFirstByte: Int = desktopAdsResultsList.toSeq.map(_.timeToFirstByte).sum
  val desktopAdsResultArray = Array(
    desktopAdsResultsList.map(_.timeToFirstByte).sum.toDouble/desktopAdsResultsList.length.toDouble,
    (desktopAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/desktopAdsResultsList.length.toDouble),
    (desktopAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/desktopAdsResultsList.length.toDouble),
    (desktopAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/desktopAdsResultsList.length.toDouble),
    (desktopAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/desktopAdsResultsList.length.toDouble),
    (desktopAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/desktopAdsResultsList.length.toDouble),
    (desktopAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/desktopAdsResultsList.length.toDouble),
    (desktopAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/desktopAdsResultsList.length.toDouble),
    (desktopAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/desktopAdsResultsList.length.toDouble))

  val desktopNoAdsResultArray = Array(
    (desktopNoAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/desktopNoAdsResultsList.length.toDouble),
    (desktopNoAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/desktopNoAdsResultsList.length.toDouble))

  val mobileAdsResultArray = Array(
    (mobileAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/mobileAdsResultsList.length.toDouble),
    (mobileAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/mobileAdsResultsList.length.toDouble))

  val mobileNoAdsResultArray = Array(
    (mobileNoAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble/mobileNoAdsResultsList.length.toDouble),
    (mobileNoAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble/mobileNoAdsResultsList.length.toDouble),
    (mobileNoAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble/mobileNoAdsResultsList.length.toDouble),
    (mobileNoAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble/mobileNoAdsResultsList.length.toDouble),
    (mobileNoAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble/mobileNoAdsResultsList.length.toDouble),
    (mobileNoAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble/mobileNoAdsResultsList.length.toDouble),
    (mobileNoAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum/mobileNoAdsResultsList.length.toDouble),
    (mobileNoAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum/mobileNoAdsResultsList.length.toDouble),
    mobileNoAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble/mobileNoAdsResultsList.length.toDouble)


  val diffDesktopAdsVNoAds = Array(
  desktopAdsResultArray(0) - desktopNoAdsResultArray(0),
    desktopAdsResultArray(1) - desktopNoAdsResultArray(1),
    desktopAdsResultArray(2) - desktopNoAdsResultArray(2),
    desktopAdsResultArray(3) - desktopNoAdsResultArray(3),
    desktopAdsResultArray(4) - desktopNoAdsResultArray(4),
    desktopAdsResultArray(5) - desktopNoAdsResultArray(5),
    desktopAdsResultArray(6) - desktopNoAdsResultArray(6),
    desktopAdsResultArray(7) - desktopNoAdsResultArray(7),
    desktopAdsResultArray(8) - desktopNoAdsResultArray(8))

  val diffMobileAdsVNoAds = Array(
    mobileAdsResultArray(0) - mobileNoAdsResultArray(0),
    mobileAdsResultArray(1) - mobileNoAdsResultArray(1),
    mobileAdsResultArray(2) - mobileNoAdsResultArray(2),
    mobileAdsResultArray(3) - mobileNoAdsResultArray(3),
    mobileAdsResultArray(4) - mobileNoAdsResultArray(4),
    mobileAdsResultArray(5) - mobileNoAdsResultArray(5),
    mobileAdsResultArray(6) - mobileNoAdsResultArray(6),
    mobileAdsResultArray(7) - mobileNoAdsResultArray(7),
    mobileAdsResultArray(8) - mobileNoAdsResultArray(8))


  def generateCSVResultsTable(contentType: String): String = {
    val summaryHeaders: String = "Content Type, Result Type, Avg Time to First Byte, Avg Time to First Paint (ms), Avg Time to Doc Complete (ms), Avg Bytes In Doc Complete (ms), Avg timeFullyLoaded (ms), Avg Bytes In Fully Loaded (ms), Avg US Prepaid Cost, Avg Postpaid Cost, Avg Speed Index (ms)\n"
    val desktopAdsRow: String = contentType + "," +
      "Desktop Ads Displayed" + "," +
      desktopAdsResultArray(0) + "," +
      desktopAdsResultArray(1) + "," +
      desktopAdsResultArray(2) + "," +
      desktopAdsResultArray(3) + "," +
      desktopAdsResultArray(4) + "," +
      desktopAdsResultArray(5) + "," +
      desktopAdsResultArray(6) + "," +
      desktopAdsResultArray(7) + "," +
      desktopAdsResultArray(8) + "\n"

    val desktopNoAdsRow: String = contentType + "," +
      "Desktop No Ads" + "," +
      desktopNoAdsResultArray(0) + "," +
      desktopNoAdsResultArray(1) + "," +
      desktopNoAdsResultArray(2) + "," +
      desktopNoAdsResultArray(3) + "," +
      desktopNoAdsResultArray(4) + "," +
      desktopNoAdsResultArray(5) + "," +
      desktopNoAdsResultArray(6) + "," +
      desktopNoAdsResultArray(7) + "," +
      desktopNoAdsResultArray(8) + "\n"

    val mobileAdsRow: String = contentType + "," +
      "Mobile Ads Displayed" + "," +
      mobileAdsResultArray(0) + "," +
      mobileAdsResultArray(1) + "," +
      mobileAdsResultArray(2) + "," +
      mobileAdsResultArray(3) + "," +
      mobileAdsResultArray(4) + "," +
      mobileAdsResultArray(5) + "," +
      mobileAdsResultArray(6) + "," +
      mobileAdsResultArray(7) + "," +
      mobileAdsResultArray(8) + "\n"

    val mobileNoAdsRow: String = contentType + "," +
      "Mobile No Ads" + "," +
      mobileNoAdsResultArray(0) + "," +
      mobileNoAdsResultArray(1) + "," +
      mobileNoAdsResultArray(2) + "," +
      mobileNoAdsResultArray(3) + "," +
      mobileNoAdsResultArray(4) + "," +
      mobileNoAdsResultArray(5) + "," +
      mobileNoAdsResultArray(6) + "," +
      mobileNoAdsResultArray(7) + "," +
      mobileNoAdsResultArray(8) + "\n"

    val diffDesktopAdsVNoAdsRow: String = contentType + "," +
    "Desktop diff Ads v No-Ads" + "," +
      diffDesktopAdsVNoAds(0) + "," +
      diffDesktopAdsVNoAds(1) + "," +
      diffDesktopAdsVNoAds(2) + "," +
      diffDesktopAdsVNoAds(3) + "," +
      diffDesktopAdsVNoAds(4) + "," +
      diffDesktopAdsVNoAds(5) + "," +
      diffDesktopAdsVNoAds(6) + "," +
      diffDesktopAdsVNoAds(7) + "," +
      diffDesktopAdsVNoAds(8) + "\n"

    val diffMobileAdsVNoAdsRow: String = contentType + "," +
      "Mobile diff Ads v No-Ads" + "," +
      diffMobileAdsVNoAds(0) + "," +
      diffMobileAdsVNoAds(1) + "," +
      diffMobileAdsVNoAds(2) + "," +
      diffMobileAdsVNoAds(3) + "," +
      diffMobileAdsVNoAds(4) + "," +
      diffMobileAdsVNoAds(5) + "," +
      diffMobileAdsVNoAds(6) + "," +
      diffMobileAdsVNoAds(7) + "," +
      diffMobileAdsVNoAds(8) + "\n"



    summaryHeaders +
      desktopAdsRow +
      desktopNoAdsRow +
      mobileAdsRow +
      mobileNoAdsRow +
      diffDesktopAdsVNoAdsRow +
      diffMobileAdsVNoAdsRow



  }

  /*def generateHTMLResultsTable(contentType: String) = {

  }*/


}
