package BUF2PE

import spinal.core._
import spinal.lib._

class TopLevel(
    operandType: HardType[SFix],
    val pof: Int = 4,
    val poy: Int = 4,
    val pox: Int = 4,
    val kernel_size: Int = 3,
    val CHANNEL_N: Int = 2
) extends Component {
  def PoyType      = new Poy(operandType, poy = poy, pox = pox, kernel_size = kernel_size)
  def Post_topType = new Post_top(INT_BITS = operandType().maxExp, POX = pox, POY = poy)
  val io = new Bundle {
    val poyInput = new Bundle {
      val master_en = in Bool ()
      val weight    = in(Vec(operandType, pof))
      val activation = new Bundle {
        val buffer         = in(Vec(Vec(operandType, pox), poy))
        val buffer_standby = in(Vec(operandType, poy))
      }
      val clear = in(Vec(Bool, pof))
    }

    val mac_valid_number = in UInt (log2Up(poy) bits)
    val post = Vec(
      new Bundle {
        val K              = in Bits (16 bits)
        val B              = in Bits (16 bits)
        val bias           = in Bits (pox * 16 bits)
        val relu_out       = out Bits (pox * 16 bits)
        val relu_out_valid = out Bool ()
        val post_out       = out Bits (pox * 16 bits)
        val post_out_valid = out Bool ()
      },
      pof / CHANNEL_N
    )

  }
  val conv = Seq.fill(pof)(PoyType)
  val post = Seq.fill(pof / CHANNEL_N)(Post_topType)
  assert(pof % CHANNEL_N == 0)

  val column_counter        = Counter(0 until kernel_size, io.poyInput.master_en)
  val row_counter           = Counter(0 until kernel_size, column_counter.willOverflow)
  val input_channel_counter = Counter(0 until pof, row_counter.willOverflow)

  for (f <- 0 until pof) {
    conv(f).io.weight := io.poyInput.weight(f)
    conv(f).io.activation.assignAllByName(io.poyInput.activation)
    conv(f).io.row    := row_counter
    conv(f).io.column := column_counter
    conv(f).io.clear  := io.poyInput.clear(f)

  }
  for (i <- 0 until pof / CHANNEL_N) {
    for (j <- 0 until CHANNEL_N)
      for (y <- 0 until poy)
        for (x <- 0 until pox)
          post(i).io.mac_to_serializer(
            16 * ((j) * pox * poy + y * pox + x) + 15 downto 16 * ((j) * pox * poy + y * pox + x)
          ) := conv(i * CHANNEL_N + j).io.output(y)(x).asBits

    post(i).io.K                := io.post(i).K
    post(i).io.B                := io.post(i).B
    post(i).io.bias             := io.post(i).bias
    post(i).io.mac_output_valid := input_channel_counter.willOverflow
    post(i).io.mac_valid_number := io.mac_valid_number
    io.post(i).relu_out         := post(i).io.relu_out
    io.post(i).relu_out_valid   := post(i).io.relu_out_valid
    io.post(i).post_out         := post(i).io.post_out
    io.post(i).post_out_valid   := post(i).io.post_out_valid

  }

}

object TopLevel {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl")
      .generateVerilog(
        gen = new TopLevel(SFix(peak = 4 exp, width = 16 bits))
      )
      .mergeRTLSource()
  }
}
