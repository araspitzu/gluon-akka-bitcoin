package utils

import java.security.SecureRandom

import akka.actor.Actor
import fr.acinq.bitcoin.Crypto.{PrivateKey, PublicKey}
import scodec.bits.ByteVector

class InfoActor extends Actor {

  val random = new SecureRandom()

  override def receive: Receive = {
    case 'makePubKey =>
      sender() ! PrivateKey(ByteVector(random.generateSeed(32))).publicKey
    case 'info =>
      val actorSystemSettings = context.system.settings.toString()
      sender() ! actorSystemSettings
  }

}
