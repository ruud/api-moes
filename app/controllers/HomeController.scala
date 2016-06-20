package controllers

import javax.inject._

import play.api.{Logger, Play}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(ws: WSClient)(implicit exec: ExecutionContext) extends Controller {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action {
    Ok("Hello")
  }

  /**
    * Text to be analyzed by TextRazor
    */
  case class AnalyzeText(text: String)

  val analyzeTextForm = Form(
    mapping(
      "text" -> nonEmptyText
    )(AnalyzeText.apply)(AnalyzeText.unapply)
  )

  val analyze = Action.async(parse.form(analyzeTextForm)) { implicit request =>
    val analyzeTextData = request.body
    val text = analyzeTextData.text
    Logger.info("Text to be analyzed: " + text)
    ws.url("http://api.textrazor.com")
      .withHeaders("x-textrazor-key" -> "62f5a302677fb1a5cef6b0834c45a16ae20a3be22683ada5f9cb5313")
      .post(Map("text" -> Seq(text),
        "extractors" -> Seq("entities")))
      .map { response =>
        Ok(Json.parse(response.body))
      }
  }

}
