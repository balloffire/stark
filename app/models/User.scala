package models

import java.util.UUID
import java.sql.Date

case class User(
  userID: UUID,
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  joinDate: Date)