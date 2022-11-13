import cocotb
from cocotb.triggers import FallingEdge, Timer
from cocotb.clock import Clock
from cocotb.result import TestSuccess, TestFailure
from cocotb.triggers import RisingEdge
from cocotb.binary import BinaryValue

import numpy as np
from fxpmath import Fxp
import torch

# import debugpy

# debugpy.listen(4000)
# print("Waiting for debugger attach")
# debugpy.wait_for_client()


@cocotb.test()
async def test(dut):
    await cocotb.start(Clock(dut.clk, 10, "ns").start())

    pof = 4
    poy = 4
    pox = 4
    kernel_size = 5
    CHANNEL_N = 4

    Qtype = "Q4.12"

    i_f = 6
    size = 14

    o_f = 16
    out_size = size - kernel_size + 1

    fmap = np.random.rand(i_f, size, size)
    wmap = np.random.rand(o_f, out_size, out_size)

    dut._id("io_poyInput_master_en", extended=False).value = 1

    for x in range(0, out_size, kernel_size):
        for y in range(0, out_size, kernel_size):
            fmap_buffer = fmap[
                :, y : y + poy + kernel_size - 1, x : x + pox + kernel_size - 1
            ]

            for fmap_single_channel in fmap_buffer:
                for j in range(poy):
                    for i in range(pox):
                        dut._id(
                            f"io_poyInput_activation_buffer_{i}_{j}", extended=False
                        ).value = BinaryValue(
                            Fxp(fmap_single_channel[j][i], dtype=Qtype).bin()
                        )

                i = pox
                for j in range(poy):
                    dut._id(
                        f"io_poyInput_activation_buffer_standby_{j}", extended=False
                    ).value = BinaryValue(
                        Fxp(fmap_single_channel[j][i], dtype=Qtype).bin()
                    )
                    await RisingEdge(dut.clk)
                    i = i + 1
