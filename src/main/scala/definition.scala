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