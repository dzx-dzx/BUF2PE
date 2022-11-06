package BUF2PE

import spinal.core._
import spinal.lib._

class Poy(operandType: HardType[SFix], poy: Int = 3, pox: Int = 3, kernel_size: Int = 3) extends Component {
  val io = new Bundle {
    val weight = in(operandType)
    val activation = new Bundle {
      val buffer         = in(Vec(Vec(operandType, pox), poy))
      val buffer_standby = in(Vec(operandType, poy))
    }
    val output = out(Vec(Vec(operandType, pox), poy))
    // val en     = in(Bool)
    val clear = in(Bool)
  }
  val pox_array: Seq[Pox] = Seq.fill(poy)(new Pox(operandType))
  val counter             = Counter(0 until kernel_size * kernel_size)

  for (i <- 0 until poy) {
    pox_array(i).io.weight                    := io.weight
    pox_array(i).io.activation.buffer         := io.activation.buffer(i)
    pox_array(i).io.activation.buffer_standby := io.activation.buffer_standby(i)

    if (i == poy - 1) {
      pox_array(i).io.activation.source_from := ActivationSource.BUFFER
      pox_array(i).io.activation.fifo_in := pox_array(i).io.activation.fifo_in.getZero
    } else {
      pox_array(
        i
      ).io.activation.source_from        := (counter < kernel_size) ? ActivationSource.BUFFER | ActivationSource.FIFO
      pox_array(i).io.activation.fifo_in := pox_array(i + 1).io.activation.fifo_out
    }

    pox_array(i).io.clear := io.clear

    io.output(i) := pox_array(i).io.output
  }
}
object Poy {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new Poy(SFix(peak = 8 exp, width = 16 bits))
    )
  }
}