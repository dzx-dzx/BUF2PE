package BUF2PE

import spinal.core._
import spinal.lib._
class Pox(peak: Int=8, width: Int=16 ) extends Component{
  
}
object Pox {
  def main(args: Array[String]): Unit = {
    SpinalConfig(targetDirectory = "rtl").generateVerilog(
      gen = new Pox(peak = 8, width = 16)
    )
  }
}
