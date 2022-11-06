package BUF2PE

import spinal.core._
import spinal.lib._

class Pox(operandType: HardType[SFix], pox: Int = 3, kernel_size: Int = 3) extends Component {
  val io = new Bundle {
    val weight = in(operandType)
    val activation = new Bundle {
      val buffer, fifo_in = in(Vec(operandType, pox))
      val buffer_standby  = in(operandType)
      val source_from     = in(ActivationSource())
      val fifo_out        = out(Vec(operandType, pox))
    }
    val clear  = in(Bool)
    val output = out(Vec(operandType, pox))
  }
  val pe_array: Seq[PE] = Seq.fill(pox)(new PE(operandType))
  for (i <- 0 until pox) {
    pe_array(i).io.weight := io.weight

    pe_array(i).io.activation.source_from := io.activation.source_from
    pe_array(i).io.activation.buffer      := io.activation.buffer(i)
    pe_array(i).io.activation.fifo        := io.activation.fifo_in(i)

    pe_array(i).io.reset_mac := io.clear

    if (i == pox - 1) {
      pe_array(i).io.activation.shift_in := io.activation.buffer_standby
    } else { pe_array(i).io.activation.shift_in := pe_array(i + 1).io.activation.shift_out }

    io.activation
      .fifo_out(i) := History(pe_array(i).io.activation.shift_out, kernel_size + 1, init = operandType().getZero).last
    io.output(i)   := pe_array(i).io.output

  }
}

object Pox {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new Pox(SFix(peak = 8 exp, width = 16 bits))
    )
  }
}
