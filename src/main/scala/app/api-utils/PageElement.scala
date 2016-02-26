package app.apiutils

/**
 * Created by mmcnamara on 25/02/16.
 */
abstract  class PageElement {

  val resource: String
  val contentType: String
  val requestStart: Double
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

  def stringToMilliseconds(time: String): Int ={
    if(!time.contains("-")) {
      if (time.contains("ms")) {
        time.slice(0, time.indexOf(" ms")).toInt
      } else {
        time.slice(0, time.indexOf(" s")).toInt * 1000
      }
    } else{
      null
    }
  }

  def stringToBytes(size: String): Int ={
    if(!size.contains("-")) {
      if (size.contains("KB") || size.contains("MB")) {
        if (size.contains("MB")) {
          size.slice(0, size.indexOf(" MB")).toInt * 1024 * 1024
        } else {
          size.slice(0, size.indexOf(" KB")).toInt * 1000
        }
      } else {
        size.slice(0, size.indexOf(" ")).toInt
      }
    } else {
      null
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
  val cleanString: String = htmlTableRow.replaceAll("<tr>","").replaceAll("</tr>", "").replaceAll(" odd","").replaceAll(" even","").replaceAll("Render", "").replaceAll("Doc","")




  val resourceHTMLelement: String = cleanString.substring(cleanString.indexOf(requestNumberClassname+2),cleanString.indexOf("</td>", cleanString.indexOf(requestNumberClassname+1)))
  override val resource: String = resourceHTMLelement.substring(resourceHTMLelement.indexOf("http"),resourceHTMLelement.indexOf("\"",resourceHTMLelement.indexOf("http")))
  override val contentType: String = cleanString.substring(cleanString.indexOf(contentTypeClassname+2),cleanString.indexOf("</td>",cleanString.indexOf(contentTypeClassname+2)))
  val requestStartString: String = cleanString.substring(cleanString.indexOf(requestStartClassname+2),cleanString.indexOf("</td>",cleanString.indexOf(requestStartClassname+2)))
  override val requestStart: Int = stringToMilliseconds(requestStartString)

// todo - continue in the same manner as above
  override val dnsLookUp: Int = stringToMilliseconds(rowDNSLookUp)
  override val initialConnection: Int = stringToMilliseconds(rowInitialConnection)
  override val sslNegotiation: Int = stringToMilliseconds(rowSSLNegotiation)
  override val timeToFirstByte: Int = stringToMilliseconds(rowTimeToFirstByte)
  override val contentDownload: Int = stringToMilliseconds(rowContentDownload)
  override val bytesDownloaded: Int = stringToBytes(rowBytesDownloaded)
  override val errorStatusCode: Int = rowErrorStatusCode.toInt
  override val iP: String = rowIP
}


}

