package pokemon

import cats.effect.{IO, IOApp}
import com.typesafe.scalalogging.LazyLogging

object Application extends IOApp.Simple with LazyLogging {

  override def run: IO[Unit] = IO { logger.info("Test") }

}
