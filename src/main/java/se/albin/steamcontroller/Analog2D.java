package se.albin.steamcontroller;

public final class Analog2D
{
	double x, y;
	
	public double x()
	{
		return x;
	}
	
	public double y()
	{
		return y;
	}
	
	public static double transformSignedShort(short s)
	{
		if(s < 0)
			return s / 32768.0;
		else
			return s / 32767.0;
	}
}
