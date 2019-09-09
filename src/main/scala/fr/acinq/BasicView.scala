package fr.acinq

import akka.util.Timeout
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.{Label, ListCell, ListView}
import javafx.scene.layout.VBox
import com.gluonhq.charm.glisten.control.AppBar
import com.gluonhq.charm.glisten.mvc.View
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon

import scala.concurrent.duration._

class BasicView(startPinger: => Unit) extends View {

    implicit val timeout = Timeout(10 seconds) // for akka ask

    private val listView = new ListView[String]
    private val status = new ListView[String]

    listView.setPlaceholder(new Label("Empty"))

    val labelDB = new Label("Actor")
    labelDB.setStyle("-fx-text-fill: gray")
    val vListDB = new VBox(10, labelDB, listView)
    vListDB.setAlignment(Pos.CENTER_LEFT)
    val labelStatus = new Label("Log")
    labelStatus.setStyle("-fx-text-fill: gray")
    val vListStatus = new VBox(10, labelStatus, status)
    vListStatus.setAlignment(Pos.CENTER_LEFT)
    val controls = new VBox(20, vListDB, vListStatus)
    controls.setPadding(new Insets(10))
    controls.setAlignment(Pos.CENTER)
    setCenter(controls)

    override protected def updateAppBar(appBar: AppBar) {

        import MaterialDesignIcon._

        appBar.setTitleText("Actors")
        appBar.setNavIcon(PLAY_ARROW.button { _ =>
            startPinger
        })
    }

}
