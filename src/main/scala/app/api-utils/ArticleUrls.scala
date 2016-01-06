package app.apiutils

import com.gu.contentapi.client.GuardianContentClient
import com.gu.contentapi.client.model.SearchQuery
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ArticleUrls(key: String) {
  val testApi:String = key
  println("testApi = " + testApi)
  val contentApiClient = new GuardianContentClient(key)

  def getUrls: Future[List[String]] = {
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
      //      .tag("tone/minute-by-minute")
      .contentType("liveblog")

    contentApiClient.getResponse(searchQuery) map { response =>
      for (result <- response.results) yield { result.webUrl }
    }
  }
}


object Test {

  def test = {
    List(1,2,3,4).map { chicken => chicken.toString } //List("1","2","3","4")
  }

  val futureCat = Future { "cat" }.map { animal => println(animal)} // "cat"
  futureCat onSuccess {
    case cat => println( cat + "Mow")
  }

  val cat = Await.result(futureCat, 5 seconds)

}

