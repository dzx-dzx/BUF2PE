package BUF2PE

import spinal.core._

object PEMain {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new StreamIndexedFIFO(
        StreamIndexedFIFOGenerics(
          pixelBitWidth = 8,
          slidingWindowDepth = 3
        )
      )
    )
  }
}
