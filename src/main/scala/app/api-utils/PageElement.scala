package app.apiutils

/**
 * Created by mmcnamara on 25/02/16.
 */
abstract  class PageElement {

  val resource: String
  val contentType: String
  val requestStart: Int
  val dnsLookUp: Int
  val initialConnection: Int
  val sslNegotiation: Int
  val timeToFirstByte: Int 
  val contentDownload: Int
  val bytesDownloaded: Int
  val errorStatusCode: Int
  val iP: String
  lazy val sizeInKB: Double = roundAt(2)(bytesDownloaded/1024)
  lazy val sizeInMB: Double = roundAt(2)(bytesDownloaded/(1024 * 1024))

  override def toString():String = {
  "Resource: " + resource + ", \n" +
  "Content Type: " + contentType + ", \n" +
  "Request Start Time: " + requestStart.toString + "ms, \n" +
  "DNS Look-up Time: " + dnsLookUp + "ms, \n" +
  "Initial Connection Time: " + initialConnection + "ms, \n" +
  "SSL Negotiation Time: " + sslNegotiation + "ms, \n" +
  "Time to First Byte: " + timeToFirstByte + "ms, \n" +
  "Content Download Time: " + contentDownload + "ms, \n" +
  "Bytes Downloaded: " + bytesDownloaded + "bytes, \n" +
  "Error Status Code: " + errorStatusCode + ", \n" +
  "IP Address: " + iP
  }

  def toHTMLTableRow(): String = {
    "<tr>" +
      "<td>" + resource + "</td>" +
      "<td>" + contentType + "</td>" +
      "<td>" + requestStart.toString + "ms</td>" +
      "<td>" + dnsLookUp + "ms</td>" +
      "<td>" + initialConnection + "ms</td>" +
      "<td>" + sslNegotiation + "ms</td>" +
      "<td>" + timeToFirstByte + "ms</td>" +
      "<td>" + contentDownload + "ms</td>" +
      "<td>" + bytesDownloaded + "bytes</td>" +
      "<td>" + errorStatusCode + "</td>" +
      "<td>" + iP + "</td>" +
      "</tr>"
  }

  def stringToMilliseconds(time: String): Int ={
    if(!time.contains("-")) {
      if (time.contains("ms")) {
        time.slice(0, time.indexOf(" ms")).toInt
      } else {
        if (time.contains(" s")) {
          (time.slice(0, time.indexOf(" s")).toDouble * 1000).toInt
        } else {
          0
        }
      }
    } else
      0
  }

  def stringToBytes(size: String): Int ={
    if(!size.contains("-")) {
      if (size.contains("KB") || size.contains("MB")) {
        if (size.contains("MB")) {
          (size.slice(0, size.indexOf(" MB")).toDouble * 1024 * 1024).toInt
        } else {
          (size.slice(0, size.indexOf(" KB")).toDouble * 1000).toInt
        }
      } else {
        size.slice(0, size.indexOf(" ")).toInt
      }
    } else {
      0
    }
  }


  def roundAt(p: Int)(n: Double): Double = { val s = math pow (10, p); (math round n * s) / s }

}


class PageElementFromString(rowResource: String,
                  rowContentType: String,
                  rowRequestStart: String,
                  rowDNSLookUp: String,
                  rowInitialConnection: String,
                  rowSSLNegotiation: String,
                  rowTimeToFirstByte: String,
                  rowContentDownload: String,
                  rowBytesDownloaded: String,
                  rowErrorStatusCode: String,
                  rowIP: String) extends PageElement{

  override val resource: String = rowResource
  override val contentType: String = rowContentType
  override val requestStart: Int = stringToMilliseconds(rowRequestStart)
  override val dnsLookUp: Int = stringToMilliseconds(rowDNSLookUp)
  override val initialConnection: Int = stringToMilliseconds(rowInitialConnection)
  override val sslNegotiation: Int = stringToMilliseconds(rowSSLNegotiation)
  override val timeToFirstByte: Int = stringToMilliseconds(rowTimeToFirstByte)
  override val contentDownload: Int = stringToMilliseconds(rowContentDownload)
  override val bytesDownloaded: Int = stringToBytes(rowBytesDownloaded)
  override val errorStatusCode: Int = rowErrorStatusCode.toInt
  override val iP: String = rowIP
}

class PageElementFromHTMLTableRow(htmlTableRow: String) extends PageElement{
  //assumes string starts with "<tr>" and ends with "</tr>" and that elements are encapsulated by <td class=["Some kind of class"]> .... </td>
  val requestNumberClassname: String = "reqNum"
  val resourceClassname: String = "reqUrl"
  val contentTypeClassname: String = "reqMime"
  val requestStartClassname: String = "reqStart"
  val dnsLookupClassname: String = "reqDNS"
  val initialConnectionClassname: String = "reqSocket"
  val sslNegotiationClassname: String = "reqSSL"
  val timeToFirstByteClassname: String = "reqTTFB"
  val contentDownloadClassname: String = "reqDownload"
  val bytesDownloadedClassname: String = "reqBytes"
  val errorStatusCodeClassname: String = "reqResult"
  val iPClassname: String = "ReqIP"

//  val tableElementString: String = htmlTableRow.substring(htmlTableRow.indexOf("<",2),htmlTableRow.indexOf("</tr>"))
  val cleanString: String = htmlTableRow.replaceAll("<tr>","").replaceAll("</tr>", "").replaceAll(" odd","").replaceAll(" even","").replaceAll("Render", "").replaceAll("Doc","").replaceAll(" warning", "").replaceAll(" error", "")
  println("\n \n \n  ************  cleanString  **************\n" + cleanString + "\n************************************** \n \n \n")

  println("resource element start index: " + cleanString.indexOf(resourceClassname)+2)
  println("resource element end index: " + cleanString.indexOf("</td>", cleanString.indexOf(resourceClassname)+2))

  val resourceHTMLElement: String = getDataFromHTMLTableElement(cleanString, resourceClassname)
  println("resourceHtml: " + resourceHTMLElement)
  override val resource: String = resourceHTMLElement.substring(resourceHTMLElement.indexOf("http"),resourceHTMLElement.indexOf("\"",resourceHTMLElement.indexOf("http")))
  println("resource: " + resource)

  override val contentType: String = getDataFromHTMLTableElement(cleanString, contentTypeClassname)

  val requestStartString: String = getDataFromHTMLTableElement(cleanString, requestStartClassname)
    //cleanString.substring(cleanString.indexOf(requestStartClassname)+2,cleanString.indexOf("</td>",cleanString.indexOf(requestStartClassname)+2))
  println(requestStartString)
  override val requestStart: Int = stringToMilliseconds(requestStartString)

  val dnsLookUpString: String = getDataFromHTMLTableElement(cleanString, dnsLookupClassname)
    //cleanString.substring(cleanString.indexOf(dnsLookupClassname)+dnsLookupClassname.length+2,cleanString.indexOf("</td>", cleanString.indexOf(dnsLookupClassname)+dnsLookupClassname.length+2))
  override val dnsLookUp: Int = stringToMilliseconds(dnsLookUpString)

  val initialConnectionString: String = getDataFromHTMLTableElement(cleanString, initialConnectionClassname)
    //cleanString.substring(cleanString.indexOf(initialConnectionClassname)+2,cleanString.indexOf("</td>", cleanString.indexOf(initialConnectionClassname)+2))
  override val initialConnection: Int = stringToMilliseconds(initialConnectionString)

  val sslNegotiationString: String = getDataFromHTMLTableElement(cleanString, sslNegotiationClassname)
    //cleanString.substring(cleanString.indexOf(sslNegotiationClassname)+2,cleanString.indexOf("</td>", cleanString.indexOf(sslNegotiationClassname)+2))
  override val sslNegotiation: Int = stringToMilliseconds(sslNegotiationString)

  val timeToFirstByteString: String = getDataFromHTMLTableElement(cleanString, timeToFirstByteClassname)
    //cleanString.substring(cleanString.indexOf(timeToFirstByteClassname)+2,cleanString.indexOf("</td>", cleanString.indexOf(timeToFirstByteClassname)+2))
  override val timeToFirstByte: Int = stringToMilliseconds(timeToFirstByteString)

  val contentDownloadString: String = getDataFromHTMLTableElement(cleanString, contentDownloadClassname)
  println("ContentDownloadString = " + contentDownloadString)
    //cleanString.substring(cleanString.indexOf(contentDownloadClassname)+2,cleanString.indexOf("</td>", cleanString.indexOf(contentDownloadClassname)+2))
  override val contentDownload: Int = stringToMilliseconds(contentDownloadString)

  val bytesDownloadedString: String = getDataFromHTMLTableElement(cleanString, bytesDownloadedClassname)
    //cleanString.substring(cleanString.indexOf(bytesDownloadedClassname)+2,cleanString.indexOf("</td>", cleanString.indexOf(bytesDownloadedClassname)+2))
  override val bytesDownloaded: Int = stringToBytes(bytesDownloadedString)

  val errorStatusCodeString: String = getDataFromHTMLTableElement(cleanString, errorStatusCodeClassname)
    //cleanString.substring(cleanString.indexOf(errorStatusCodeClassname)+2,cleanString.indexOf("</td>", cleanString.indexOf(errorStatusCodeClassname)+2))
  override val errorStatusCode: Int = errorStatusCodeString.toInt

  override val iP: String = getDataFromHTMLTableElement(cleanString, iPClassname)
    //cleanString.substring(cleanString.indexOf(iPClassname)+2,cleanString.indexOf("</td>", cleanString.indexOf(iPClassname)+2))

  def getDataFromHTMLTableElement(tableRow: String, classname: String): String = {
    val returnString: String = tableRow.substring(tableRow.indexOf(classname)+ classname.length + 2,tableRow.indexOf("</td>", tableRow.indexOf(classname)+ classname.length + 2))
    returnString
  }

}




