package app.api

import app.apiutils.PerformanceResultsObject


/**
 * Created by mmcnamara on 31/03/16.
 */
class ResultsSummary(resultsList: List[PerformanceResultsObject]) {


def generateMeanValues(resultsList: List[PerformanceResultsObject]):Array[Array[Double]] = {

  val desktopAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Desktop") && !element.brokenTest && element.adsDisplayed) yield element
  val mobileAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Android") && !element.brokenTest && element.adsDisplayed) yield element
  val desktopNoAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Desktop") && !element.brokenTest && !element.adsDisplayed) yield element
  val mobileNoAdsResultsList = for (element <- resultsList if element.typeOfTest.contains("Android") && !element.brokenTest && !element.adsDisplayed) yield element

  println("\ndesktopAdsResultsList Length = " + desktopAdsResultsList.length + "\n")
  println("\nmobileAdsResultsList Length = " + mobileAdsResultsList.length + "\n")
  println("\ndesktopNoAdsResultsList Length = " + desktopNoAdsResultsList.length + "\n")
  println("\nmobileNoAdsResultsList Length = " + mobileNoAdsResultsList.length + "\n")

  //  val desktopAdsTimeToFirstByte: Int = desktopAdsResultsList.toSeq.map(_.timeToFirstByte).sum
  val meanDesktopAdsResultArray = Array(
    desktopAdsResultsList.map(_.timeToFirstByte).sum.toDouble / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum / desktopAdsResultsList.length.toDouble,
    desktopAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble / desktopAdsResultsList.length.toDouble)

  val meanDesktopNoAdsResultArray = Array(
    desktopNoAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum / desktopNoAdsResultsList.length.toDouble,
    desktopNoAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble / desktopNoAdsResultsList.length.toDouble)

  val meanMobileAdsResultArray = Array(
    mobileAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum / mobileAdsResultsList.length.toDouble,
    mobileAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble / mobileAdsResultsList.length.toDouble)

  val meanMobileNoAdsResultArray = Array(
    mobileNoAdsResultsList.toSeq.map(_.timeToFirstByte).sum.toDouble / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.timeFirstPaintInMs).sum.toDouble / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.timeDocCompleteInMs).sum.toDouble / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.bytesInDocComplete).sum.toDouble / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.timeFullyLoadedInMs).sum.toDouble / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.bytesInFullyLoaded).sum.toDouble / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.estUSPrePaidCost).sum / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.estUSPostPaidCost).sum / mobileNoAdsResultsList.length.toDouble,
    mobileNoAdsResultsList.toSeq.map(_.speedIndex).sum.toDouble / mobileNoAdsResultsList.length.toDouble)


  val diffMeanDesktopAdsVNoAds = Array(
    meanDesktopAdsResultArray(0) - meanDesktopNoAdsResultArray(0),
    meanDesktopAdsResultArray(1) - meanDesktopNoAdsResultArray(1),
    meanDesktopAdsResultArray(2) - meanDesktopNoAdsResultArray(2),
    meanDesktopAdsResultArray(3) - meanDesktopNoAdsResultArray(3),
    meanDesktopAdsResultArray(4) - meanDesktopNoAdsResultArray(4),
    meanDesktopAdsResultArray(5) - meanDesktopNoAdsResultArray(5),
    meanDesktopAdsResultArray(6) - meanDesktopNoAdsResultArray(6),
    meanDesktopAdsResultArray(7) - meanDesktopNoAdsResultArray(7),
    meanDesktopAdsResultArray(8) - meanDesktopNoAdsResultArray(8))

  val diffMeanMobileAdsVNoAds = Array(
    meanMobileAdsResultArray(0) - meanMobileNoAdsResultArray(0),
    meanMobileAdsResultArray(1) - meanMobileNoAdsResultArray(1),
    meanMobileAdsResultArray(2) - meanMobileNoAdsResultArray(2),
    meanMobileAdsResultArray(3) - meanMobileNoAdsResultArray(3),
    meanMobileAdsResultArray(4) - meanMobileNoAdsResultArray(4),
    meanMobileAdsResultArray(5) - meanMobileNoAdsResultArray(5),
    meanMobileAdsResultArray(6) - meanMobileNoAdsResultArray(6),
    meanMobileAdsResultArray(7) - meanMobileNoAdsResultArray(7),
    meanMobileAdsResultArray(8) - meanMobileNoAdsResultArray(8))


    Array(meanDesktopAdsResultArray, meanDesktopNoAdsResultArray, meanMobileAdsResultArray, meanMobileNoAdsResultArray, diffMeanDesktopAdsVNoAds, diffMeanMobileAdsVNoAds)
    }

  def zeroInnOnMean(resultsList: List[PerformanceResultsObject]): Array[Double] = {
    val initialMeans = generateMeanValues(resultsList)
    val meanDiffs: Array[Double] = generateMeanDiffs(resultsList, initialMeans)
    val initialThresholds = meanDiffs.map(threshold => threshold * 2)
    val listMinusOutliers = for (result <- resultsList if
    result.timeToFirstByte < initialThresholds(0) &&
      result.timeFirstPaintInMs < initialThresholds(1) &&
      result.timeDocCompleteInMs < initialThresholds(2) &&
      result.bytesInDocComplete < initialThresholds(3) &&
      result.timeFullyLoadedInMs < initialThresholds(4) &&
      result.bytesInFullyLoaded < initialThresholds(5) &&
      result.estUSPrePaidCost < initialThresholds(6) &&
      result.estUSPostPaidCost < initialThresholds(7) &&
      result.speedIndex < initialThresholds(8)) yield result

    val revisedMean = generateMeanValues(listMinusOutliers)
    val revisedMeanDiffs: Array[Double] = generateMeanDiffs(listMinusOutliers, revisedMean)
    val revisedThresholds = revisedMeanDiffs.map(threshold => threshold * 2)
    revisedThresholds
  }


  def generateMeanDiffs(resultsList: List[PerformanceResultsObject], means: Array[Array[Double]]): Array[Double] = {
    resultsList.map(result => {
      if(result.typeOfTest.contains("Desktop") && result.adsDisplayed) {
        val diffArray: Array[Double] = 
      }
    })
  }
  

  def generateCSVResultsTable(contentType: String): String = {
    val summaryHeaders: String = "Content Type, Result Type, Avg Time to First Byte, Avg Time to First Paint (ms), Avg Time to Doc Complete (ms), Avg Bytes In Doc Complete (ms), Avg timeFullyLoaded (ms), Avg Bytes In Fully Loaded (ms), Avg US Prepaid Cost, Avg Postpaid Cost, Avg Speed Index (ms)\n"
    val desktopAdsRow: String = contentType + "," +
      "Desktop Ads Displayed" + "," +
      meanDesktopAdsResultArray(0) + "," +
      meanDesktopAdsResultArray(1) + "," +
      meanDesktopAdsResultArray(2) + "," +
      meanDesktopAdsResultArray(3) + "," +
      meanDesktopAdsResultArray(4) + "," +
      meanDesktopAdsResultArray(5) + "," +
      meanDesktopAdsResultArray(6) + "," +
      meanDesktopAdsResultArray(7) + "," +
      meanDesktopAdsResultArray(8) + "\n"

    val desktopNoAdsRow: String = contentType + "," +
      "Desktop No Ads" + "," +
      meanDesktopNoAdsResultArray(0) + "," +
      meanDesktopNoAdsResultArray(1) + "," +
      meanDesktopNoAdsResultArray(2) + "," +
      meanDesktopNoAdsResultArray(3) + "," +
      meanDesktopNoAdsResultArray(4) + "," +
      meanDesktopNoAdsResultArray(5) + "," +
      meanDesktopNoAdsResultArray(6) + "," +
      meanDesktopNoAdsResultArray(7) + "," +
      meanDesktopNoAdsResultArray(8) + "\n"

    val mobileAdsRow: String = contentType + "," +
      "Mobile Ads Displayed" + "," +
      meanMobileAdsResultArray(0) + "," +
      meanMobileAdsResultArray(1) + "," +
      meanMobileAdsResultArray(2) + "," +
      meanMobileAdsResultArray(3) + "," +
      meanMobileAdsResultArray(4) + "," +
      meanMobileAdsResultArray(5) + "," +
      meanMobileAdsResultArray(6) + "," +
      meanMobileAdsResultArray(7) + "," +
      meanMobileAdsResultArray(8) + "\n"

    val mobileNoAdsRow: String = contentType + "," +
      "Mobile No Ads" + "," +
      meanMobileNoAdsResultArray(0) + "," +
      meanMobileNoAdsResultArray(1) + "," +
      meanMobileNoAdsResultArray(2) + "," +
      meanMobileNoAdsResultArray(3) + "," +
      meanMobileNoAdsResultArray(4) + "," +
      meanMobileNoAdsResultArray(5) + "," +
      meanMobileNoAdsResultArray(6) + "," +
      meanMobileNoAdsResultArray(7) + "," +
      meanMobileNoAdsResultArray(8) + "\n"

    val diffDesktopAdsVNoAdsRow: String = contentType + "," +
    "Desktop diff Ads v No-Ads" + "," +
      diffMeanDesktopAdsVNoAds(0) + "," +
      diffMeanDesktopAdsVNoAds(1) + "," +
      diffMeanDesktopAdsVNoAds(2) + "," +
      diffMeanDesktopAdsVNoAds(3) + "," +
      diffMeanDesktopAdsVNoAds(4) + "," +
      diffMeanDesktopAdsVNoAds(5) + "," +
      diffMeanDesktopAdsVNoAds(6) + "," +
      diffMeanDesktopAdsVNoAds(7) + "," +
      diffMeanDesktopAdsVNoAds(8) + "\n"

    val diffMobileAdsVNoAdsRow: String = contentType + "," +
      "Mobile diff Ads v No-Ads" + "," +
      diffMeanMobileAdsVNoAds(0) + "," +
      diffMeanMobileAdsVNoAds(1) + "," +
      diffMeanMobileAdsVNoAds(2) + "," +
      diffMeanMobileAdsVNoAds(3) + "," +
      diffMeanMobileAdsVNoAds(4) + "," +
      diffMeanMobileAdsVNoAds(5) + "," +
      diffMeanMobileAdsVNoAds(6) + "," +
      diffMeanMobileAdsVNoAds(7) + "," +
      diffMeanMobileAdsVNoAds(8) + "\n"



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
