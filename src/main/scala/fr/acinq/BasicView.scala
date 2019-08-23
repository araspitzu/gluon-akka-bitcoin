package fr.acinq

import fr.acinq.sqlite._
import java.io.{File, IOException}
import java.sql._
import java.util

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern._
import akka.util.Timeout
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.{Label, ListCell, ListView}
import javafx.scene.layout.VBox
import com.gluonhq.charm.down.{Platform, Services}
import com.gluonhq.charm.down.plugins.StorageService
import com.gluonhq.charm.glisten.control.AppBar
import com.gluonhq.charm.glisten.mvc.View
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon
import utils.Person

import scala.compat.java8.OptionConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class BasicView(infoActor: ActorRef) extends View {

    import Platform._

    try {
        Platform.getCurrent match {
            case ANDROID => Class.forName("org.sqldroid.SQLDroidDriver")
            case IOS => Class.forName("SQLite.JDBCDriver")
            case DESKTOP => Class.forName("org.sqlite.JDBC")
            case _ if System.getProperty("os.arch").toUpperCase.contains("ARM") =>
                Class.forName("org.sqlite.JDBC")
        }
    } catch {
        case ex: Throwable => println(s"Error:  Driver not found $ex")
    }


    private val DB_NAME: String = "sample.db"

    private val listView = new ListView[Person]
    private val status = new ListView[String]

    listView.setPlaceholder(new Label("No items yet"))

    listView.setCellFactory { listView: ListView[Person] =>
        new ListCell[Person]() {
            override protected def updateItem(person: Person, empty: Boolean): Unit = {
                super.updateItem(person, empty)
                setText(if (person != null && !empty) person.name else null)
            }
        }
    }


    val labelDB = new Label("DB Items")
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

        appBar.setNavIcon(PERSON_PIN.button)
        appBar.setTitleText("SQLite")
        appBar.getActionItems.addAll(

            MOVIE_CREATION.button(getInfo()),
            CREATE_NEW_FOLDER.button(createDB()),
            ATTACH_FILE.button(readDB()),
            REMOVE.button {
                listView.getItems.clear()
                status.getItems.clear()
            }
        )
    }

    private def setStatus( text: String ): Unit = {
        status.getItems.add(text)
    }

    private def getInfo(): Unit = {
        implicit val timeout = Timeout(10 seconds)
        (infoActor ? 'info).mapTo[String].map { resp =>
           status.getItems.add(resp)
        }
    }

    private def createDB(): Unit = {

        status.getItems.add("Creating a Database with SQLite")
        val dbUrl = try {
            val dir = Services.get(classOf[StorageService]).asScala
              .flatMap(s => s.getPrivateStorage).get
            val db: File = new File(dir, DB_NAME)
            "jdbc:sqlite:" + db.getAbsolutePath
        }
        catch {
            case ex: IOException =>
                setStatus(s"Error: ${ex.getMessage}")
                ""
        }
        using(DriverManager.getConnection(dbUrl)) { connection =>
            val list = new util.ArrayList[Person]
            using(connection.createStatement) { stmt =>
                stmt.setQueryTimeout(30)
                status.getItems.add("Creating table 'person'...")
                stmt.executeUpdate("drop table if exists person")
                stmt.executeUpdate("create table person (id integer, firstname string, lastname string)")
                stmt.executeUpdate("insert into person values(1, 'Johan', 'Vos')")
                stmt.executeUpdate("insert into person values(2, 'Eugene', 'Ryzhikov')")
                stmt.executeUpdate("insert into person values(3, 'Joeri', 'Sykora')")
                stmt.executeUpdate("insert into person values(4, 'Erwin', 'Morrhey')")
                setStatus("Retrieving records from table 'person'...")
                using(stmt.executeQuery("select * from person")) { rs =>
                    while (rs.next) {
                        list.add(Person(rs.getString("firstname"), rs.getString("lastname")))
                    }
                    setStatus("End creating table and retrieving records")
                }
            }
            listView.getItems.setAll(list)
        }


    }

    private def readDB(): Unit = {
        setStatus("Reading an existing Database with SQLite")
        val dbUrl = "jdbc:sqlite:" + ( if (Platform.isDesktop) {
            ":resource:" + classOf[AppEntryPoint].getResource(s"/databases/$DB_NAME").toExternalForm
        } else {
            try {
                val dir = Services.get(classOf[StorageService]).asScala
                  .flatMap(s => s.getPrivateStorage).get
                val db: File = new File(dir, DB_NAME)
                setStatus(s"Copying database $DB_NAME to private storage")
                DBUtils.copyDatabase("/databases/", dir.getAbsolutePath, DB_NAME)
                db.getAbsolutePath
            }
            catch {
                case ex: IOException =>
                    setStatus(s"IO error: ${ex.getMessage}")
                    ""
            }
        })

        using( DriverManager.getConnection(dbUrl) ) { connection =>
            val list = new util.ArrayList[Person]
            val md: DatabaseMetaData = connection.getMetaData
            using(md.getTables(null, null, "%", null)) { rs =>
                status.getItems.add(s"Tables in Database $DB_NAME")
                while (rs.next) {
                    setStatus(" * " + rs.getString(3))
                }
            }
            status.getItems.add("Reading table 'person'")
            using(connection.createStatement) { stmt =>
                using(stmt.executeQuery("select * from person")) { rs =>
                    while (rs.next) {
                        list.add(Person(rs.getString("firstname"), rs.getString("lastname")))
                    }
                    setStatus("End reading table and retrieving records")
                }
            }
            listView.getItems.setAll(list)
        }


    }
}
