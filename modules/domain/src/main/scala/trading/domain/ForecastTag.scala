package trading.domain

import cats.syntax.all.*
import cats.{ Eq, Show }
import io.circe.Codec

enum ForecastTag derives Codec.AsObject:
  case Long, Short, Unknown

object ForecastTag:
  def from(str: String): ForecastTag =
    Either.catchNonFatal(valueOf(str)).getOrElse(Unknown)

  given Eq[ForecastTag]   = Eq.fromUniversalEquals
  given Show[ForecastTag] = Show.fromToString
