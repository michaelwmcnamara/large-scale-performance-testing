package app.apiutils

/**
 * Created by mmcnamara on 25/02/16.
 */
abstract  class PageElement {

  val elementName: String
  val elementLink: String
  val elementType: String
  val sizeInBytes: Int
  lazy val sizeInKB: Double = roundAt(2)(sizeInBytes/1024)
  lazy val sizeInMB: Double = roundAt(2)(sizeInBytes/(1024 * 1024))

  def roundAt(p: Int)(n: Double): Double = { val s = math pow (10, p); (math round n * s) / s }

}

class ImageElement(name: String, link: String, size: Int) extends PageElement {
  val elementName: String = name
  val elementLink: String = link
  val elementType: String = "Image"
  val sizeInBytes: Int = size
}

class VideoElement(name: String, link: String, size: Int) extends PageElement {
  val elementName: String = name
  val elementLink: String = link
  val elementType: String = "Video"
  val sizeInBytes: Int = size
}

class documentElement(name: String, link: String, size: Int) extends PageElement {
  val elementName: String = name
  val elementLink: String = link
  val elementType: String = "Document"
  val sizeInBytes: Int = size
}

class GifElement(name: String, link: String, size: Int) extends PageElement {
  val elementName: String = name
  val elementLink: String = link
  val elementType: String = "Gif"
  val sizeInBytes: Int = size
}

class OtherElement(name: String, link: String, size: Int) extends PageElement {
  val elementName: String = name
  val elementLink: String = link
  val elementType: String = "Other"
  val sizeInBytes: Int = size
}


