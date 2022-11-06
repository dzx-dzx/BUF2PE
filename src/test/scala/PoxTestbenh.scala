package BUF2PE

import org.scalatest.funsuite.AnyFunSuite
import spinal.core._
import spinal.core.sim._
import spinal.lib._
import scala.collection.mutable.Queue
import org.scalatest.BeforeAndAfter

class PoxTestbench extends AnyFunSuite with BeforeAndAfter {
  var compiled: SimCompiled[Pox] = null
  before {
    compiled = SimConfig.withWave
      .withConfig(SpinalConfig(targetDirectory = "rtl"))
      .compile(
        new Pox(SFix(peak = 8 exp, width = 16 bits))
      )
  }
  test("a") {
    compiled.doSim { dut =>
      {
        val clockDomain = dut.clockDomain
        val io = dut.io
        clockDomain.forkStimulus(10)
        io.activation.source_from #= ActivationSource.BUFFER
        io.activation.buffer.randomize()
        io.activation.buffer_standby.randomize()
        clockDomain.waitSampling()
        io.activation.source_from #= ActivationSource.SHIFT
        io.activation.buffer.randomize()
        io.activation.buffer_standby.randomize()
        clockDomain.waitSampling(10)

        
      }
    }
  }
}
