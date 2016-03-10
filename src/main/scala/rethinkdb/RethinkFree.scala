package rethinkdb

import scalaz._, Scalaz._
import com.rethinkscala.ast.Produce
import com.rethinkscala.ResultExtractor
import com.rethinkscala.Blocking.functional._

// goal: create a free monad over Produce[A]
// be able to run that later, by providing a BlockingConnection to it
object RethinkFree {

  def query[A](a: Produce[A])(implicit m: Manifest[A]): ConnectionIO[A] = {
    Free.liftFC[ProduceWithClassTag, A](ProduceWithClassTag(a, m))
  }

  type ConnectionIO[A] = Free.FreeC[ProduceWithClassTag, A]

  // wrapper class is required to get the ClassTag into the natural transformation
  case class ProduceWithClassTag[A](produce: Produce[A], tag: Manifest[A])


  val interp: ProduceWithClassTag ~> Reader[BlockingConnection, ?] = new (ProduceWithClassTag ~> Reader[BlockingConnection, ?]) {

    def toBlockingResultExtractor[T: Manifest](connection: BlockingConnection): ResultExtractor[T] = connection
      .resultExtractorFactory
      .create[T]

    override def apply[A](a: ProduceWithClassTag[A]): Reader[BlockingConnection, A] = {
      Reader { implicit con =>
        implicit val extr = toBlockingResultExtractor[A](con)(a.tag)
        a.produce.run.get
      }

    }

  }

  implicit class ConnectionIOExt[A](c: ConnectionIO[A]) {
    def run1(con: BlockingConnection): A = {
      Free.runFC[ProduceWithClassTag, Reader[BlockingConnection, ?], A](c)(interp).run(con)
    }
  }

}
