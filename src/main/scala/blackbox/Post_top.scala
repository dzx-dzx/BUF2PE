package BUF2PE

import spinal.core._
import spinal.lib._

class Post_top(val INT_BITS: Int = 4, val POX: Int = 3, val POY: Int = 3, val CHANNEL_N: Int = 2) extends BlackBox {
  addGeneric("INT_BITS", INT_BITS)
  addGeneric("POX", POX)
  addGeneric("POY", POY)
  addGeneric("CHANNEL_N", CHANNEL_N)
  val io = new Bundle {
    val clk               = in Bool ()
    val rst               = in Bool ()
    val mac_to_serializer = in Bits (CHANNEL_N * POY * POX * 16 bits)
    val mac_output_valid  = in Bool ()
    val mac_valid_number  = in UInt (log2Up(POY) bits)
    val K                 = in Bits (16 bits)
    val B                 = in Bits (16 bits)
    val bias              = in Bits (POX * 16 bits)
    val relu_out          = out Bits (POX * 16 bits)
    val relu_out_valid    = out Bool ()
    val post_out          = out Bits (POX * 16 bits)
    val post_out_valid    = out Bool ()
  }
  noIoPrefix()

  mapCurrentClockDomain(clock = io.clk, reset = io.rst)

  addRTLPath("../src/Post_top.v")
  addRTLPath("../src/Mux.v")
  addRTLPath("../src/PostProcess.v")
  addRTLPath("../src/Serializer.v")
}
class BlackBox_Post_top(val INT_BITS: Int = 4, val POX: Int = 3, val POY: Int = 3, val CHANNEL_N: Int = 2)
    extends Component {
  val io = new Bundle {
    val mac_to_serializer = in Bits (CHANNEL_N * POY * POX * 16 bits)
    val mac_output_valid  = in Bool ()
    val mac_valid_number  = in UInt (log2Up(POY) bits)
    val K                 = in Bits (16 bits)
    val B                 = in Bits (16 bits)
    val bias              = in Bits (POX * 16 bits)
  }
  val blackBox = new Post_top()
  io.mac_to_serializer <> blackBox.io.mac_to_serializer
  io.mac_output_valid  <> blackBox.io.mac_output_valid
  io.mac_valid_number  <> blackBox.io.mac_valid_number
  io.K                 <> blackBox.io.K
  io.B                 <> blackBox.io.B
  io.bias              <> blackBox.io.bias
}
object BlackBox_Post_top {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl")
      .generateVerilog(
        gen = new BlackBox_Post_top()
      )
      .mergeRTLSource()
  }
}
