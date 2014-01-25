package akka.persistence.journal.mapdb

import akka.persistence.journal.AsyncRecovery
import scala.concurrent.Future
import akka.persistence.PersistentRepr

import scala.concurrent.future
import scala.collection.JavaConverters._

trait MapDBRecovery extends AsyncRecovery { this: MapDBJournal =>

  import context.dispatcher

  def asyncReplayMessages(processorId: String, fromSequenceNr: Long, toSequenceNr: Long, max: Long)(replayCallback: (PersistentRepr) => Unit): Future[Unit] = future {
    val deleted = deletionsSet(processorId)
    val confirmed = confirmationsSet(processorId)
    val messages = messagesMap(processorId)

    val it = messages.entrySet().iterator()
    var returned = 0L

    while (it.hasNext && returned < max) {
      val entry = it.next()
      val seqNr = entry.getKey
      if (!deleted.contains(seqNr) && !confirmed.contains(seqNr) && fromSequenceNr <= seqNr) {
        replayCallback(entry.getValue)
        returned += 1L
      }

      if (seqNr >= toSequenceNr) {
        returned = max
      }
    }
  }

  def asyncReadHighestSequenceNr(processorId: String, fromSequenceNr: Long): Future[Long] = future {
    val map = messagesMap(processorId)
    if (map.isEmpty) {
      0L
    } else {
      map.keySet().asScala.max
    }
  }
}
