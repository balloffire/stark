package models.tables

import java.sql.Date
import java.util.UUID
import play.api.Play.current
import models.User
import play.api.db.DB
import utils.BCryptUtil

import scala.slick.driver.PostgresDriver.simple._

class UserTable(tag: Tag) extends Table[User](tag, "user") {
  def userID = column[UUID]("userID", O.NotNull)

  def email = column[String]("email", O.PrimaryKey)

  def password = column[String]("password", O.NotNull)

  def firstName = column[String]("firstName", O.NotNull)

  def lastName = column[String]("lastName", O.NotNull)

  def joinDate = column[Date]("joinDate", O.NotNull)

  def * = (userID, email, password, firstName, lastName, joinDate) <>(User.tupled, User.unapply)
}

/**
 * The companion object.
 */
object UserTable {
  val userTableQuery = TableQuery[UserTable]

  def create(user: User): User = {
    Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      userTableQuery.insert(user)
      user
    }
  }

  def findByEmail(email: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()) withSession { implicit session =>
      userTableQuery.filter(_.email === email).firstOption
    }
  }

  def findByEmailAndPassword(email: String, password: String): Option[User] = {
    Database.forDataSource(DB.getDataSource()) withSession { implicit session =>
      for {
        user <- userTableQuery.filter(user => user.email === email).firstOption
        if BCryptUtil.check(password, user.password)
      } yield user
    }
  }

  def findByUserID(userID: UUID): Option[User] = {
    Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      userTableQuery.filter(_.userID === userID).firstOption
    }
  }

  def lookupPassword(email: String): Option[String] = {
    Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      userTableQuery.filter(_.email === email).firstOption.map(_.password)
    }
  }

  def updatePasswordInfo(email: String, password: String): String = {
    val updatedUser: Option[User] = Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
      userTableQuery.filter(user =>
        user.email === email
      ).firstOption.map(_.copy(password = password))
    }
    updatedUser.foreach(user =>
      Database.forDataSource(DB.getDataSource()).withSession { implicit session =>
        userTableQuery.insert(user)
      }
    )
    password
  }
}
