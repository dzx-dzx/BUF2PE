package BUF2PE

import spinal.core._

object PEMain {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new PE(
        g = PEGenerics(
          pixelBitWidth = 8,
          activationSide = 7,
          kernelSide = 3
        )
      )
    )
  }
}
