package akka.persistence.journal.mapdb

import akka.persistence.journal.SyncWriteJournal
import scala.collection.immutable.Seq
import akka.persistence.{PersistentRepr, PersistentConfirmation, PersistentId}
import org.mapdb.DBMaker
import java.io.File

class MapDBJournal extends SyncWriteJournal with MapDBRecovery {

  val config = context.system.settings.config.getConfig("mapdb-journal")

  val file = new File(config.getString("dir"))
  file.mkdirs()

  val db = {
    val maker = DBMaker.newFileDB(new File(s"${config.getString("dir")}/store"))
      .cacheLRUEnable()
      .closeOnJvmShutdown()
      .mmapFileEnableIfSupported()

    if (config.getBoolean("async-writes")) {
      maker.asyncWriteEnable().make()
    } else {
      maker.make()
    }
  }

  def writeMessages(messages: Seq[PersistentRepr]): Unit = {
    messages foreach { msg =>
      messagesMap(msg.processorId).put(msg.sequenceNr, msg)
    }

    db.commit()
  }

  def writeConfirmations(confirmations: Seq[PersistentConfirmation]): Unit = {
    confirmations foreach { confirmation =>
      confirmationsSet(confirmation.processorId).add(confirmation.sequenceNr)
    }

    db.commit()
  }

  def deleteMessages(messageIds: Seq[PersistentId], permanent: Boolean): Unit = {
    if (permanent) {
      messageIds foreach { msgId =>
        messagesMap(msgId.processorId).remove(msgId.sequenceNr)
      }
    } else {
      messageIds foreach { msgId =>
        deletionsSet(msgId.processorId).add(msgId.sequenceNr)
      }
    }

    db.commit()
  }

  def deleteMessagesTo(processorId: String, toSequenceNr: Long, permanent: Boolean): Unit = {
    if (permanent) {
      val map = messagesMap(processorId)
      for {
        i <- 0L to toSequenceNr
      } map.remove(i)
    } else {
      val set = deletionsSet(processorId)
      for {
        i <- 0L to toSequenceNr
      } set.add(i)
    }

    db.commit()
  }

  protected def messagesMap(processorId: String) = {
    db.getTreeMap[Long, PersistentRepr](s"messages-$processorId")
  }

  protected def confirmationsSet(processorId: String) = {
    db.getTreeSet[Long](s"confirmations-$processorId")
  }

  protected def deletionsSet(processorId: String) = {
    db.getTreeSet[Long](s"deletions-$processorId")
  }

  override def postStop() {
    super.postStop()
    db.close()
  }
}
