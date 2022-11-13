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

    val row, column      = in UInt (log2Up(kernel_size) bits)
    val clear, reset_mac = in(Bool)
  }
  val areaWithReset = new ClockingArea(ClockDomain.current.copy(softReset = io.clear)) {
    val pox_array: Seq[Pox] = Seq.fill(poy)(new Pox(operandType, pox, kernel_size))

    for (i <- 0 until poy) {
      pox_array(i).io.weight                    := io.weight
      pox_array(i).io.activation.buffer         := io.activation.buffer(i)
      pox_array(i).io.activation.buffer_standby := io.activation.buffer_standby(i)

      if (i == poy - 1) {
        pox_array(i).io.activation.fifo_in := pox_array(i).io.activation.fifo_in.getZero
      } else {
        pox_array(i).io.activation.fifo_in := pox_array(i + 1).io.activation.fifo_out
      }

      when(io.row === 0 || Bool(i == poy - 1)) {
        when(io.column === 0) {
          pox_array(i).io.activation.source_from := ActivationSource.BUFFER
        } otherwise {
          pox_array(i).io.activation.source_from := ActivationSource.SHIFT
        }
      } otherwise {
        if (i == poy - 1) {
          when(io.column === 0) {
            pox_array(i).io.activation.source_from := ActivationSource.BUFFER
          } otherwise {
            pox_array(i).io.activation.source_from := ActivationSource.SHIFT
          }
        } else {
          pox_array(i).io.activation.source_from := ActivationSource.FIFO
        }
      }

      pox_array(i).io.reset_mac := io.reset_mac

      io.output(i) := pox_array(i).io.output
    }
  }
}
object Poy {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl")
      .generateVerilog(
        gen = new Poy(SFix(peak = 8 exp, width = 16 bits))
      )
      .printPruned()
  }
}
