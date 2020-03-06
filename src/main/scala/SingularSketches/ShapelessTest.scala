/*package SingularSketches

import shapeless.PolyDefns.~>
import shapeless._

object ShapelessTest {
  case class A(nameA: String, b: B) extends Fields
  case class B(nameB: String, c: C, nameB2: String) extends Fields
  case class B2(name2B: String, c2: C2, name2B2: String)
  case class C(nameC: String)
  case class C2(name2C: String)

  trait Fields {
    val cool = "head"
  }

  val testHList = "way" :: "past" :: "cool" :: HNil
  val testHList2 = "way" :: B("pa", C("s"), "t") :: "cool" :: HNil
  val a = A("1", B("2",C("3"),"4"))
  val aGeneric = Generic[A]
  val bGeneric = Generic[B]
  val cGeneric = Generic[C]
  val aLGeneric = LabelledGeneric[A]

  object mapper1 extends LowPriorityWrapCases {
    implicit val caseString= at[String]("s"+_)
    implicit val caseInt= at[Int]("i"+_.toString)
  }
  trait LowPriorityWrapCases extends Poly1 {
    implicit def caseOther[T]: Case.Aux[T, Any] = at[T](identity(_) match {
      case b: B => bGeneric.to(b) map mapper1
      case c: C => cGeneric.to(c) map mapper1
      case x => x
    })
  }

  def main(args: Array[String]): Unit = {
    println(
      aLGeneric
    )
    println(
      aLGeneric.to(a) map mapper1
    )
    println(
      aGeneric.to(a) map mapper1
    )
    println(
      //aGeneric.to(a) map mapper1
      testHList map mapper1
    )
    println(
      HList(B) map mapper1
    )
  }

  object FieldUtils {
    import shapeless._
    import shapeless.ops.record._
    import shapeless.ops.hlist.ToTraversable

    object getNestedFields extends LowPriorityWrapCases {
      //implicit val caseEmb= at[Embeddable](_)
    }
    object LowPriorityWrapCases extends Poly1 {
      implicit def caseOther[T]: Case.Aux[T, Any] = at[T](identity(_) match {
        case x: T => if(getNestedFields[T]) Generic[T].to(x) map getNestedFields else List.empty[String]
      })
    }

    trait FieldNames[T] {
      def apply(): List[String]
    }
    /*trait Embeddable

    // TODO Make this work with nested case classes that have been tagged by a marker trait
    implicit def toNames[T, Repr <: HList, LRepr <: HList, KeysRepr <: HList](implicit
                                                               gen: Generic.Aux[T, Repr],
                                                               lGen: LabelledGeneric.Aux[T, LRepr],
                                                               keys: Keys.Aux[LRepr, KeysRepr],
                                                               traversable: ToTraversable.Aux[KeysRepr, List, Symbol]
                                                             ): FieldNames[T] = new FieldNames[T] {
      def apply() = {
        gen.to(_) map getNestedFields
        keys().toList.map(_.name.toLowerCase)
      }
    }


    private def getNestedFields[A]: Boolean ={
      import reflect.runtime.universe._
      import com.sun.tools.javac.code.TypeTag
      def x [B <: A : TypeTag]: Boolean = typeOf[B].baseClasses.contains(typeOf[Embeddable].typeSymbol)
      x[A]
    }
    def fieldNames[T](implicit h : FieldNames[T]) = h()*/
  }
}
*/
