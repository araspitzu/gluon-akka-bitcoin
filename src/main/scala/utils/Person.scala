package utils

import scala.beans.BeanProperty

case class Person( @BeanProperty var firstName: String,
                   @BeanProperty var lastName: String) {

    override def toString: String = s"Person.scala { firstName=$firstName lastName=$lastName }"

    lazy val name = firstName + " " + lastName

}
