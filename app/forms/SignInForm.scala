package forms

import play.api.data.Form
import play.api.data.Forms._

object SignInForm {
  case class SignIn(email: String, password: String)

  val form = Form(
    mapping(
      "email" -> email,
      "password" -> nonEmptyText
    )(SignIn.apply)(SignIn.unapply)
  )
}