package se.albin.steamcontroller;

@FunctionalInterface
public interface RawDataListener
{
	void receive(byte[] data);
}
