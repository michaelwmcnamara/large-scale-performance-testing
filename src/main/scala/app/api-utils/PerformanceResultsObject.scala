package app.apiutils

import scala.xml.Elem


/**
 * Created by mmcnamara on 10/02/16.
 */
class PerformanceResultsObject(url:String, testType: String, tTFB: Int, tFP:Int, tDC: Int, bDC: Int, tFL: Int, bFL: Int, sI: Int, status: String, warning: Boolean, alert: Boolean) {
  val testUrl: String = url
  val typeOfTest: String = testType
  val timeToFirstByte: Int = tTFB
  val timeFirstPaint: Int = tFP
  val timeDocComplete: Int = tDC
  val bytesInDocComplete: Int = bDC
  val kBInDocComplete: Double = roundAt(2)(bytesInDocComplete/1024)
  val mBInDocComplete: Double = roundAt(2)(bytesInDocComplete/1048576)
  val timeFullyLoaded: Int = tFL
  val bytesInFullyLoaded: Int = bFL
  val kBInFullyLoaded: Double = roundAt(2)(bytesInFullyLoaded/1024)
  val mBInFullyLoaded: Double = roundAt(2)(bytesInFullyLoaded/1048576)
  val estUSPrePaidCost: Double = roundAt(2)((bytesInFullyLoaded.toDouble/1048576)*0.10)
  val estUSPostPaidCost: Double = roundAt(2)((bytesInFullyLoaded.toDouble/1048576)*0.06)
  val speedIndex: Int = sI
  val resultStatus:String = status
  var warningStatus: Boolean = warning
  var alertStatus: Boolean = alert

  def toStringList(): List[String] = {
    List(testUrl.toString + ", " + timeFirstPaint.toString + "ms", timeDocComplete.toString + "ms", mBInDocComplete + "MB" , timeFullyLoaded.toString + "ms", mBInFullyLoaded + "MB", speedIndex.toString, resultStatus)
  }

  def toHTMLTableCells(): String = {
    "<th>" + testUrl + " </th>" + "<td>" + timeFirstPaint.toString + "ms </td><td>" +  timeDocComplete.toString + "ms </td><td>" + mBInDocComplete + "MB </td><td>" + timeFullyLoaded.toString + "ms </td><td>" + mBInFullyLoaded + "MB </td><td> $(US)" + estUSPrePaidCost + "</td><td> $(US)" + estUSPrePaidCost + "</td><td>" + speedIndex.toString + " </td><td> " + resultStatus + "</td>"
  }

  def toHTMLSimpleTableCells(): String = {
    "<th>" + testUrl + " </th><td>" +  (timeDocComplete/1000).toString + "s </td><td>" + mBInFullyLoaded + "MB </td><td> $(US)" + estUSPrePaidCost + "</td><td> $(US)" + estUSPrePaidCost + "</td><td>" + speedIndex.toString + " </td><td> " + resultStatus + "</td>"
  }

  override def toString(): String = {
    testUrl + ", " + timeFirstPaint.toString + "ms, " + timeDocComplete.toString + "ms, " + mBInDocComplete + "MB, " + timeFullyLoaded.toString + "ms, " + mBInFullyLoaded + "MB, " + speedIndex.toString + ", " + resultStatus
  }

  def roundAt(p: Int)(n: Double): Double = { val s = math pow (10, p); (math round n * s) / s }

}