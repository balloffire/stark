package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The form which handles the sign up process.
 */
object SignUpForm {
  case class SignUp(firstName: String,
                    lastName: String,
                    email: String,
                    password: String)
  /**
   * A play framework form.
   */
  val form = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(SignUp.apply)(SignUp.unapply)
  )
}
