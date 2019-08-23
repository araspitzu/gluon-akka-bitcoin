package utils

import java.security.SecureRandom

import akka.actor.Actor
import fr.acinq.bitcoin.Crypto.PublicKey
import org.bitcoin.Secp256k1Context
import scodec.bits.ByteVector

class InfoActor extends Actor {

  val random = new SecureRandom()

  override def receive: Receive = {
    case 'makePubKey =>
      sender() ! PublicKey(ByteVector(random.generateSeed(32)))
    case 'info =>
      val actorSystemSettings = context.system.settings.toString()
      val bitcoinLibNativeEnabled = Secp256k1Context.isEnabled
      sender() ! (actorSystemSettings + "\n" + "Native LibSecp256k1 enabled = "+bitcoinLibNativeEnabled)
  }

}
