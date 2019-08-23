package fr.acinq

import java.io.FileOutputStream
import fr.acinq.sqlite._

object DBUtils {

    def copyDatabase(pathIni: String, pathEnd: String, name: String): Unit = {

        using(classOf[AppEntryPoint].getResourceAsStream(pathIni + name)) { in =>
            using(new FileOutputStream(s"$pathEnd/$name")) { out =>
                val buffer = new Array[Byte](1024)
                Stream.continually(in.read(buffer)).takeWhile(_ != -1).foreach(out.write(buffer, 0, _))
                out.flush()
            }
        }

    }

}
