package BUF2PE

import spinal.core._
import spinal.lib._

class MAC(operandType: HardType[SFix]) extends Component {
  val io = new Bundle {
    val weight, activation = in(operandType)
    val output             = out(operandType)
    val reset_mac          = in(Bool)
  }
  val accumulator = Reg(operandType) init (0)
  when(io.reset_mac) {
    accumulator := (io.weight * io.activation).truncated
  } otherwise {
    accumulator := (accumulator + io.weight * io.activation).truncated
  }
  io.output := accumulator
}

object MAC {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new MAC(SFix(peak = 8 exp, width = 16 bits))
    )
  }
}
