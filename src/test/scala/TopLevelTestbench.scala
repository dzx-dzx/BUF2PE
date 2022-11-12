package BUF2PE

import io.circe._, io.circe.parser._
import spinal.core._
import spinal.core.sim._
import spinal.lib._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import scala.io.Source
import me.shadaj.scalapy.py
import scala.util.Random
import scala.Array

class TopLevelTestbench extends AnyFunSuite with BeforeAndAfter {
  val pof         = 4
  val poy         = 4
  val pox         = 4
  val kernel_size = 5
  val CHANNEL_N   = 4

  var compiled: SimCompiled[TopLevel] = null
  before {
    compiled = SimConfig.withWave
      .withConfig(SpinalConfig(targetDirectory = "rtl"))
      .compile(
        new TopLevel(
          SFix(peak = 4 exp, width = 16 bits),
          pof = pof,
          poy = poy,
          pox = pox,
          kernel_size = kernel_size,
          CHANNEL_N = CHANNEL_N
        )
      )
  }
  test("a") {
    compiled.doSim { dut =>
      {
        val clockDomain = dut.clockDomain
        val io          = dut.io

        clockDomain.forkStimulus(5)

        // val model = parse(Source.fromFile("../../../net/bin_param_Q4.12.json").mkString)
        // val input = parse(Source.fromFile("../../../net/bin_test_data_Q4.12.json").mkString)

        // val pt = py.module("pytorch")

        val i_f  = 6
        val size = 14

        val fmap = Seq.fill(i_f)(Seq.fill(size)(Seq.fill(size)(Random.nextFloat())))

        val o_f      = 16
        val out_size = size - kernel_size + 1

        val weight = Seq.fill(o_f)(Seq.fill(size)(Seq.fill(size)(Random.nextFloat())))

        for (y <- 0 to size) {
          for (x <- 0 to size) {
            val kernel_act = Seq.fill(kernel_size)(Seq.fill(kernel_size)(0))
            for (ky <- 0 to kernel_size)
              for (kx <- 0 to kernel_size)
                kernel_act(ky)(kx) = fmap(y + ky)(x + kx)
            
          }
        }
      }
    }
  }
}
