package models.tables

import java.sql.Date
import java.util.UUID
import models.Place
import play.api.Play.current
import play.api.db.DB

import scala.slick.driver.PostgresDriver.simple._

class PlaceTable(tag: Tag) extends Table[Place](tag, "place") {
  def id = column[UUID]("id")

  def primaryName = column[String]("primaryName", O.PrimaryKey)

  def otherNames = column[String]("otherNames")

  def locationBlob = column[String]("locationBlob")

  def lastModified = column[Date]("lastModified")

  def created = column[Date]("created")

  def * = (id, primaryName, otherNames, locationBlob, lastModified, created) <>(Place.tupled, Place.unapply)
}

object PlaceTable {

  val placeTableQuery = TableQuery[PlaceTable]

  def create(place: Place): Place = {
    Database.forDataSource(DB.getDataSource()) withSession { implicit session =>
      placeTableQuery.insert(place)
      place
    }
  }

  def findByFuzzyName(fuzzyName: String): Option[Place] = {
    Database.forDataSource(DB.getDataSource()) withSession { implicit session =>
      placeTableQuery.filter(_.primaryName === fuzzyName).firstOption
    }
  }
}
