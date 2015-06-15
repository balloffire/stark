package controllers

import java.sql.Date
import java.util.UUID, UUID.randomUUID
import javax.inject.Inject

import forms._
import models.User
import models.tables.UserTable
import play.api.i18n.{MessagesApi, Messages}
import play.api.mvc._
import utils.{SecuredController, BCryptUtil}

class ApplicationController @Inject()(val messagesApi: MessagesApi) extends SecuredController {

  def index = Action { implicit request =>
    Ok(views.html.signIn(SignInForm.form))
  }

  def signIn = Action { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      hasErrors   => BadRequest(views.html.signIn(hasErrors)),
      credentials => {
        UserTable.findByEmailAndPassword(credentials.email, credentials.password) match {
          case None => Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("invalid.credentials"))
          case Some(user) => Ok(views.html.home(user)).withToken(randomUUID -> user.userID)
        }
      }
    )
  }

  def signUp = Action { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      hasErrors  => BadRequest(views.html.signUp(hasErrors)),
      signupData => {
        UserTable.findByEmail(signupData.email) match {
          case None =>
            val user = User(
              userID    = randomUUID,
              email     = signupData.email,
              password  = BCryptUtil.hash(signupData.password),
              firstName = signupData.firstName,
              lastName  = signupData.lastName,
              joinDate  = new Date(new java.util.Date().getTime)
            )
            UserTable.create(user)
            Ok(views.html.home(user)).withToken(randomUUID -> user.userID)
          case Some(user) =>
            Redirect(routes.ApplicationController.signUp()).flashing("error" -> Messages("user.exists"))
        }
      }
    )
  }

  def signOut = Action { implicit request =>
    println(request.headers)
    request.headers.get(AuthTokenCookieKey) map { token =>
      Redirect(routes.ApplicationController.index()).discardingToken(token)
    } getOrElse BadRequest("No Token!").flashing("error" -> Messages("no.token"))
  }

  def ping = HasToken() { token => userId => implicit request =>
    UserTable.findByUserID(UUID.fromString(userId)) map { user =>
      Ok(userId.toString).withToken(token -> userId)
    } getOrElse Ok(views.html.signIn(SignInForm.form))
  }
}
