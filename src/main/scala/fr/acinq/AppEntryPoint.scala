package fr.acinq

import java.util.function.Supplier

import akka.actor.{ActorSystem, Props}
import com.gluonhq.charm.down.Platform
import com.gluonhq.charm.glisten.application.MobileApplication
import com.gluonhq.charm.glisten.mvc.View
import com.gluonhq.charm.glisten.visual.Swatch
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import utils.InfoActor

class AppEntryPoint extends MobileApplication {

  override def init(): Unit = {
    val system = ActorSystem()
    val infoActor = system.actorOf(Props(new InfoActor))


    val view = new Supplier[View]{
      def get(): View = new BasicView(infoActor)
    }
    addViewFactory(MobileApplication.HOME_VIEW, view)

  }

  override def postInit(scene: Scene) {
    Swatch.RED.assignTo(scene)
    val stage = scene.getWindow.asInstanceOf[Stage]
    stage.getIcons.add(new Image(classOf[AppEntryPoint].getResourceAsStream("/icon.png")))
    if (System.getProperty("os.arch").toUpperCase.contains("ARM") && !Platform.isIOS && !Platform.isAndroid) {
      stage.setFullScreen(true)
      stage.setFullScreenExitHint("")
    }
  }

}
