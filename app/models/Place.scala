package models

import java.sql.Date
import java.util.UUID

case class Place(id: UUID,
                 primaryName: String,
                 otherNames: String,
                 locationBlob: String,
                 lastModified: Date,
                 created: Date)

