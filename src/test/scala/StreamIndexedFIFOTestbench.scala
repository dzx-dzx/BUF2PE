package BUF2PE

import org.scalatest.funsuite.AnyFunSuite
import spinal.core._
import spinal.core.sim._
import spinal.lib._
import scala.collection.mutable.Queue

class BUF2PETestbench extends AnyFunSuite {
  var compiled: SimCompiled[StreamIndexedFIFO] = null
  test("compile") {
    compiled = SimConfig.withWave
      .withConfig(SpinalConfig(targetDirectory = "rtl"))
      .compile(
        new StreamIndexedFIFO(
          StreamIndexedFIFOGenerics(
            pixelBitWidth = 8,
            slidingWindowDepth = 3
          )
        )
      )
  }
  test("a") {
    compiled.doSim { dut =>
      {
        import SimStreamUtils._
        dut.clockDomain.forkStimulus(period = 10)
        streamMasterRandomizer(dut.io.input, dut.clockDomain)
        streamSlaveRandomizer(dut.io.output, dut.clockDomain)
        onStreamFire(dut.io.input, dut.clockDomain) {
          println(s"Input: ${dut.io.input.payload.toInt}")
        }
        onStreamFire(dut.io.output, dut.clockDomain) {
          println(s"Output:${dut.io.output.payload.toSeq.map(_.toInt)}")
        }
        for (_ <- 0 until 50) {
          dut.clockDomain.waitSampling()
        }
      }
    }
  }
}

object SimStreamUtils {
  // Fork a thread to constantly randomize the valid/payload signals of the given stream
  def streamMasterRandomizer[T <: Data](stream: Stream[T], clockDomain: ClockDomain): Unit = fork {
    stream.valid #= false
    while (true) {
      if (!stream.valid.toBoolean || stream.ready.toBoolean) {
        stream.valid.randomize()
        stream.payload.randomize()
      }
      clockDomain.waitSampling()
    }
  }

  // Fork a thread to constantly randomize the ready signal of the given stream
  def streamSlaveRandomizer[T <: Data](stream: Stream[T], clockDomain: ClockDomain): Unit = fork {
    while (true) {
      stream.ready.randomize()
      clockDomain.waitSampling()
    }
  }

  // Fork a thread which will call the body function each time a transaction is consumed on the given stream
  def onStreamFire[T <: Data](stream: Stream[T], clockDomain: ClockDomain)(body: => Unit): Unit = fork {
    while (true) {
      clockDomain.waitSampling()
      var dummy = if (stream.valid.toBoolean && stream.ready.toBoolean) {
        body
      }
    }
  }
}
