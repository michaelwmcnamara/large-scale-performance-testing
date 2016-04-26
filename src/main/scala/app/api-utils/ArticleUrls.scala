package app.apiutils

import com.gu.contentapi.client.GuardianContentClient
import com.gu.contentapi.client.model.SearchQuery
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ArticleUrls(key: String) {
  val testApi:String = key
  println("testApi = " + testApi)
  val contentApiClient = new GuardianContentClient(key)

  def getUrlsForContentType(contentType: String): List[String] = {
     contentType match {
      case("Article") => getArticles
      case ("LiveBlog") =>  getMinByMins
      case ("Interactive") => getInteractives
      case ("Video") => getVideoPages
      case ("Audio") => getAudioPages
      case("Front") => getFronts
      case (_) => {
        val empty: List[String] = List()
        empty
      }
    }
  }

  def shutDown = {
    println("Closing connection to Content API")
    contentApiClient.shutdown()
  }

  def getArticles: List[String] = {
    val until = DateTime.now
    val from = until.minusHours(48)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(100)
      .orderBy("newest")
      .contentType("article")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    for (result <- returnedResponse.results) yield {
      result.webUrl}
  }

  def getMinByMins: List[String] = {
    val until = DateTime.now
    val from = until.minusDays(7)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(100)
      .orderBy("newest")
      .tag("tone/minutebyminute")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    for (result <- returnedResponse.results) yield {
      result.webUrl}
  }


  def getInteractives: List[String] = {
    val until = DateTime.now
    val from = until.minusDays(15)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(100)
      .orderBy("newest")
      .contentType("interactive")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))

    for (result <- returnedResponse.results) yield {
      result.webUrl}
  }

  def getFronts: List[String] = {
    val listofFronts: List[String] = List("http://www.theguardian.com/uk",
      "http://www.theguardian.com/us",
      "http://www.theguardian.com/au",
      "http://www.theguardian.com/uk-news",
      "http://www.theguardian.com/world",
      "http://www.theguardian.com/politics",
      "http://www.theguardian.com/uk/sport",
      "http://www.theguardian.com/football",
      "http://www.theguardian.com/uk/commentisfree",
      "http://www.theguardian.com/uk/culture",
      "http://www.theguardian.com/uk/business",
      "http://www.theguardian.com/uk/lifeandstyle",
      "http://www.theguardian.com/fashion",
      "http://www.theguardian.com/uk/environment",
      "http://www.theguardian.com/uk/technology",
      "http://www.theguardian.com/travel")
      listofFronts
  }


  def getVideoPages: List[String] = {
    val until = DateTime.now
    val from = until.minusDays(2)

    val searchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(100)
      .orderBy("newest")
      .contentType("video")
    println("Sending query to CAPI: \n" + searchQuery.toString)

    val apiResponse = contentApiClient.getResponse(searchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    for (result <- returnedResponse.results) yield {
      result.webUrl}
  }

  def getAudioPages: List[String] = {
    val until = DateTime.now
    val from = until.minusDays(4)

    val liveBlogSearchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(100)
      .orderBy("newest")
      .contentType("audio")
    println("Sending query to CAPI: \n" + liveBlogSearchQuery.toString)

    val apiResponse = contentApiClient.getResponse(liveBlogSearchQuery)

    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    for (result <- returnedResponse.results) yield {
      result.webUrl}
  }


  /*def getContentTypeFronts: List[String] = {
    println("Creating CAPI query")
    val until = DateTime.now
    val from = until.minusHours(24)
    val FrontsSearchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showBlocks("all")
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(20)
      .orderBy("newest")
      .contentType("front")
    println("Sending query to CAPI: \n" + FrontsSearchQuery.toString)
    val apiResponse = contentApiClient.getResponse(FrontsSearchQuery)
    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    println("CAPI has returned response")
    val liveBlogUrlString: List[String] = for (result <- returnedResponse.results) yield {
      println("liveBlog result: " + result.webUrl)
      result.webUrl }
    liveBlogUrlString
  }
*/
}

