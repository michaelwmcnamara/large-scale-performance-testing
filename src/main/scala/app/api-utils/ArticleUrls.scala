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
    val until = DateTime.now
    val from = until.minusHours(24)

    val searchQuery = new SearchQuery()
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

    val apiResponse = contentApiClient.getResponse(searchQuery)
    val returnedResponse = Await.result(apiResponse, (20, SECONDS))
    val urlString: List[String] = for (result <- returnedResponse.results) yield {
        println(result.webUrl)
        result.webUrl }
    urlString
  }
}

