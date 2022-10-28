package BUF2PE

import spinal.core._
import spinal.lib._

case class StreamIndexedFIFOGenerics(
    val pixelBitWidth: Int,
    val slidingWindowDepth: Int
) {
  def Pixel = Bits(pixelBitWidth bits)
}

class StreamIndexedFIFO(g: StreamIndexedFIFOGenerics) extends Component {
  import g._
  val io = new Bundle {
    // val activation = slave Stream (Activation(g))
    // val weight     = slave Stream (Weight(g))
    // val out        = master Stream (Out(g))
    val input  = slave Stream (Pixel)
    val output = master Stream (Vec(Pixel, slidingWindowDepth))
  }
  val shift = Vec(Stream(Pixel), slidingWindowDepth)
  shift.last <-< io.input
  for (i <- 0 until (shift.length - 1)) {
    shift(i) <-< shift(i + 1)
  }
  io.output << shift(0).haltWhen(~shift.map(_.valid).andR).translateWith {
    Vec(shift.map(_.payload))
  }
}
