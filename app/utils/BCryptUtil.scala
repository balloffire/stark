package utils

import org.mindrot.jbcrypt.BCrypt

object BCryptUtil {

  def hash(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt(12))
  }

  def check(candidate: String, hashed: String) = {
    BCrypt.checkpw(candidate, hashed)
  }
}