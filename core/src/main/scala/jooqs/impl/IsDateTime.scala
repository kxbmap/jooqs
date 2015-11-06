package jooqs.impl

import java.time.{LocalDate, LocalDateTime, LocalTime, OffsetDateTime, OffsetTime}

/**
  * @see [[org.jooq.DataType#isDateTime]]
  */
sealed trait IsDateTime[A]

object IsDateTime {

  implicit object sqlDate extends IsDateTime[java.sql.Date]

  implicit object sqlTime extends IsDateTime[java.sql.Time]

  implicit object sqlTimestamp extends IsDateTime[java.sql.Timestamp]

  implicit object localDate extends IsDateTime[LocalDate]

  implicit object localTime extends IsDateTime[LocalTime]

  implicit object localDateTime extends IsDateTime[LocalDateTime]

  implicit object offsetTime extends IsDateTime[OffsetTime]

  implicit object offsetDateTime extends IsDateTime[OffsetDateTime]

}
