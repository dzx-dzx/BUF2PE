package BUF2PE

import org.scalatest.funsuite.AnyFunSuite
import spinal.core._
import spinal.core.sim._
import spinal.lib._
import scala.collection.mutable.Queue
import org.scalatest.BeforeAndAfter
import scala.util.Random

class PoyTestbench extends AnyFunSuite with BeforeAndAfter {
  val poy         = 3
  val pox         = 3
  val kernel_size = 3

  var compiled: SimCompiled[Poy] = null
  before {
    compiled = SimConfig.withWave
      .withConfig(SpinalConfig(targetDirectory = "rtl"))
      .compile(
        new Poy(SFix(peak = 8 exp, width = 16 bits), poy = 3, pox = 3, kernel_size = 3)
      )
  }
  test("a") {
    compiled.doSim { dut =>
      {
        val clockDomain = dut.clockDomain
        val io          = dut.io

        // val activation_full = Seq.fill(poy + kernel_size - 1)(Seq.fill(pox + kernel_size - 1)(Random.nextInt()))

        io.clear #= true
        io.reset_mac #= true

        clockDomain.forkStimulus(5)
        clockDomain.waitSampling()
        io.clear #= false
        io.reset_mac #= false

        for (i <- 0 to kernel_size) {
          fork {
            io.activation.randomize()
          }
          fork {
            io.weight.randomize()
          }
        clockDomain.waitSampling()
        }

      }
    }
  }
}
