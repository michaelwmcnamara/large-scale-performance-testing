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

  def getUrls: List[String] = {
    getLiveBlogUrls ++ getMinByMinUrls
  }

  def getLiveBlogUrls: List[String] = {
    val until = DateTime.now
    val from = until.minusHours(24)

    val liveBlogSearchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showBlocks("all")
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(20)
      .orderBy("oldest")
      .contentType("liveblog")

    val apiResponse = contentApiClient.getResponse(liveBlogSearchQuery)
    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    val liveBlogUrlString: List[String] = for (result <- returnedResponse.results) yield {
        println(result.webUrl)
        result.webUrl }
    liveBlogUrlString
  }

  def getMinByMinUrls: List[String] = {
    val until = DateTime.now
    val from = until.minusHours(24)

    val liveBlogSearchQuery = new SearchQuery()
      .fromDate(from)
      .toDate(until)
      .showBlocks("all")
      .showElements("all")
      .showFields("all")
      .showTags("all")
      .page(1)
      .pageSize(20)
      .orderBy("oldest")
      .tag("minutebyminute")

    val apiResponse = contentApiClient.getResponse(liveBlogSearchQuery)
    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    val liveBlogUrlString: List[String] = for (result <- returnedResponse.results) yield {
      println(result.webUrl)
      result.webUrl }
    liveBlogUrlString
  }


}

