package BUF2PE

import spinal.core._
import spinal.lib._

class TopLevel(
    operandType: HardType[SFix],
    val pof: Int = 4,
    val poy: Int = 3,
    val pox: Int = 3,
    val kernel_size: Int = 3
) extends Component {
  val io   = new Bundle {}
  val Conv = Seq.fill(pof)(new Poy(operandType))
}

object TopLevel {}
