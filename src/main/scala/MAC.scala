package BUF2PE

import spinal.core._
import spinal.lib._

class MAC(peak: Int, width: Int) extends Component {
  def fixType = SFix(
    peak = peak exp,
    width = width bits
  )
  val io = new Bundle {
    val weight, activation = in(fixType)
    val output             = out(fixType)
  }
  val accumulator = Reg(fixType) init (0)
  accumulator := (accumulator + io.weight * io.activation).truncated
  io.output   := accumulator
}

object MAC {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new MAC(peak = 8, width = 16)
    )
  }
}
