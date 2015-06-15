package utils

import controllers.routes
import play.api.cache.Cache
import play.api.i18n.{I18nSupport, Messages}
import play.api.mvc._
import play.api._

trait SecuredController extends Controller with I18nSupport {
  implicit val app: Application = Play.current

  val AuthTokenCookieKey = "XSRF-TOKEN"

  /** Checks that a token is either in the header or in the query string */
  def HasToken[A](p: BodyParser[A] = parse.anyContent)(f: String => String => Request[A] => Result): Action[A] =
    Action(p) { implicit request =>
      request.headers.get(AuthTokenCookieKey) flatMap { token =>
        Cache.getAs[String](token) map { userid =>
          f(token)(userid)(request)
        }
      } getOrElse Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("access.denied"))
    }

  private lazy val CacheExpiration = Play.current.configuration.getInt("cache.expiration").getOrElse(60 * 2) // 120 seconds

  implicit class ResultWithToken(result: Result) {
    def withToken(token: (Any, Any)): Result = {
      Cache.set(token._1.toString, token._2.toString, CacheExpiration)
      result.withCookies(Cookie(AuthTokenCookieKey, token._1.toString, None, httpOnly = false))
    }

    def discardingToken(token: String): Result = {
      Cache.remove(token)
      result.discardingCookies(DiscardingCookie(name = AuthTokenCookieKey))
    }
  }
}
