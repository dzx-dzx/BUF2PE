package BUF2PE

import spinal.core._
import spinal.lib._
class Pox(operandType: HardType[SFix]) extends Component {
  val io = new Bundle {
    val weight = in(operandType)
    val activation = new Bundle {
      val buffer, fifo, shift_in = in(operandType)
      val source                 = in(ActivationSource())
    }
    val output, shift_out = out(operandType)
  }
  val mac = new MAC(operandType)
  switch(io.activation.source) {
    is(ActivationSource.BUFFER) {
      mac.io.activation := io.activation.buffer
    }
    is(ActivationSource.FIFO) {
      mac.io.activation := io.activation.fifo
    }
    is(ActivationSource.SHIFT) {
      mac.io.activation := io.activation.shift_in
    }
  }
  mac.io.weight := io.weight
  io.output     := mac.io.output
  io.shift_out  := RegNext(io.activation.shift_in)
}
object Pox {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new Pox(SFix(peak = 8 exp, width = 16 bits))
    )
  }
}
