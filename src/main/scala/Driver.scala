package myapp

import slick.driver.PostgresDriver

trait MyPostgresDriver extends PostgresDriver
    with PgForUpdateSupport
{
  override val simple = new SimpleQLPlus {}

  trait SimpleQLPlus extends SimpleQL
      with ForUpdateTableImplicits
}

object MyPostgresDriver extends MyPostgresDriver

trait PgForUpdateSupport extends PostgresDriver { driver =>
  import scala.slick.ast.{Comprehension, Node, Symbol, TableNode}
  import scala.slick.compiler.CompilerState

  trait ForUpdateTable

  trait ForUpdateTableImplicits {
    type ForUpdateTable = driver.ForUpdateTable
  }

  class QueryBuilder(tree: Node, state: CompilerState) extends super.QueryBuilder(tree, state) {
    import scala.slick.util.MacroSupport.macroSupportInterpolation

    override protected def buildComprehension(c: Comprehension): Unit = {
      super.buildComprehension(c)
      buildForUpdateClause(c.from)
    }

    protected def buildForUpdateClause(from: Seq[(Symbol, Node)]) = building(FromPart) {
      val forUpdateFrom = from.filter { case (_, n) =>
        n match {
          case TableNode(_, _, _, driverTable, _) =>
            driverTable.isInstanceOf[ForUpdateTable]
          case _ => false
        }
      }
      if(!forUpdateFrom.isEmpty) {
        b" for update of "
        b.sep(forUpdateFrom , ", ") { case (sym, n) =>
          b += symbolName(sym)
        }
      }
    }
  }
  override def createQueryBuilder(n: Node, state: CompilerState): QueryBuilder = new QueryBuilder(n, state)
}

