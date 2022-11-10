package BUF2PE

import org.scalatest.funsuite.AnyFunSuite
import spinal.core._
import spinal.core.sim._
import spinal.lib._
import scala.collection.mutable.Queue
import org.scalatest.BeforeAndAfter
import scala.util.Random
import scala.math._

class PoyTestbench extends AnyFunSuite with BeforeAndAfter {
  val poy         = 3
  val pox         = 3
  val kernel_size = 3

  var compiled: SimCompiled[Poy] = null
  before {
    compiled = SimConfig.withWave
      .withConfig(SpinalConfig(targetDirectory = "rtl"))
      .compile(
        new Poy(SFix(peak = 2 exp, width = 16 bits), poy = 3, pox = 3, kernel_size = 3)
      )
  }
  test("a") {
    compiled.doSim { dut =>
      {
        val clockDomain = dut.clockDomain
        val io          = dut.io

        // val activation_full = Seq.fill(poy + kernel_size - 1)(Seq.fill(pox + kernel_size - 1)(Random.nextInt()))

        clockDomain.forkStimulus(5)
        io.clear     #= true
        io.reset_mac #= true

        clockDomain.waitSampling(10)
        io.clear     #= false
        io.reset_mac #= false
        io.weight    #= pow(2, -5)
        io.activation.buffer.zipWithIndex.foreach { case (v, i) =>
          v.zipWithIndex.foreach { case (s, j) => s #= i * pow(2, -5) + j * pow(2, -7) }
        }
        io.activation.buffer_standby.zipWithIndex.foreach { case (s, i) => s #= i * pow(2, -6) }

        print(SFix(peak = 2 exp, width = 16 bits).maxValue)

        for (i <- 0 until 18) {
          clockDomain.waitSampling()
          fork {
            io.activation.buffer.foreach(_.foreach(s => s #= s.toBigDecimal + pow(2, -10)))
            io.activation.buffer_standby.foreach(s => s #= s.toBigDecimal + pow(2, -10))
          }
          fork {
            io.weight #= io.weight.toBigDecimal + pow(2, -5)
          }
        }

      }
    }
  }
}
