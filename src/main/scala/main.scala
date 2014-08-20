package myapp

import MyPostgresDriver.simple._
import scala.util.control.Exception._

object MyApp extends App{
  // ref. http://slick.typesafe.com/doc/2.1.0/gettingstarted.html
  // Definition of the SUPPLIERS table
  class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, street, city, state, zip)
  }
  val suppliers = TableQuery[Suppliers]

  // Definition of the COFFEES table
  class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = (name, supID, price, sales, total)
    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("SUP_FK", supID, suppliers)(_.id)
  }
  val coffees = TableQuery[Coffees]

  class CoffeesForUpdate(tag: Tag) extends Coffees(tag) with ForUpdateTable
  val coffeesForUpdate = TableQuery[CoffeesForUpdate]

  Database.forURL("jdbc:postgresql:test1", driver = "org.postgresql.Driver") withSession {
    implicit session =>

      allCatch opt (suppliers.ddl ++ coffees.ddl).create
      val q = for {
        c <- coffeesForUpdate if c.price < 9.0
        s <- suppliers if s.id === c.supID
      } yield (c.name, s.name)
      q.list
  }
}

