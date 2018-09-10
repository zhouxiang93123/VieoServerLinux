package com.wspn.pcap4j;

import com.github.ffalcinelli.jdivert.Packet;
import com.github.ffalcinelli.jdivert.WinDivert;
import com.github.ffalcinelli.jdivert.exceptions.WinDivertException;

public class MecTest {
public static void main(String[] args) throws WinDivertException {
	// Capture only TCP packets to port 80, i.e. HTTP requests.
	WinDivert w = new WinDivert("ip.SrcAddr == 10.108.147.75");

	w.open(); // packets will be captured from now on

	Packet packet = w.recv();  // read a single packet
	System.out.println(packet);
	w.send(packet);  // re-inject the packet into the network stack

	w.close();  // stop capturing packets
}
}
