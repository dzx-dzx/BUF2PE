package BUF2PE

import spinal.core._
import spinal.lib._

case class PEGenerics(
    val pixelBitWidth: Int,
    val activationSide: Int,
    val kernelSide: Int
) {
  def Pixel = Bits(pixelBitWidth bits)
}

case class Activation(g: PEGenerics) extends Bundle {
  val pixels = Vec(g.Pixel, g.activationSide)
}

case class Weight(g: PEGenerics) extends Bundle {
  val pixels = Vec(g.Pixel, g.kernelSide + 2)
}

case class Out(g: PEGenerics) extends Bundle {
  val pixels = Vec(g.Pixel, g.kernelSide)
}

class PE(g: PEGenerics) extends Component {
  import g._
  val io = new Bundle {
    // val activation = slave Stream (Activation(g))
    // val weight     = slave Stream (Weight(g))
    // val out        = master Stream (Out(g))
    val input  = slave Stream (Pixel)
    val output = master Stream (Vec(Pixel, 3))
  }
  val shift = Vec(Stream(Pixel), 3)
  shift.last <-< io.input
  for (i <- 0 until (shift.length - 1)) {
    shift(i) <-< shift(i + 1)
  }
  io.output << shift(0).haltWhen(~shift.map(_.valid).andR).translateWith {
    Vec(shift.map(_.payload))
  }
}
