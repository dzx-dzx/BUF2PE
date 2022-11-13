import cocotb
from cocotb.triggers import FallingEdge, Timer
from cocotb.clock import Clock
from cocotb.result import TestSuccess, TestFailure
from cocotb.triggers import RisingEdge

import numpy as np

@cocotb.test()
async def test(dut):
    await cocotb.start(Clock(dut.clk, 10, "ns").start())

    pof         = 4
    poy         = 4
    pox         = 4
    kernel_size = 5
    CHANNEL_N   = 4

    i_f  = 6
    size = 14

    o_f      = 16
    out_size = size - kernel_size + 1

    fmap = np.random.rand(i_f,size,size)
    w_map = np.random.rand(o_f,out_size,out_size)

    dut._id("io_poyInput_master_en",extended = False)