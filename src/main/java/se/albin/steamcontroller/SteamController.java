package se.albin.steamcontroller;

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public final class SteamController
{
	private final Analog2D analogTouchLeft = new Analog2D();
	private final Analog2D analogTouchRight = new Analog2D();
	private final Analog2D analogStick = new Analog2D();
	
	private final UsbDevice device;
	
	private boolean isWired;
	
	private boolean btnA, btnB, btnX, btnY, btnLB, btnRB, btnLT, btnRT, btnLG, btnRG, btnLeft, btnRight, btnHome,
		btnTouchLeft, btnTouchRight, btnAnalog, touchLeft, touchRight;
	
	private double triggerLeft, triggerRight;
	
	private SteamController(UsbDevice device, boolean isWired)
	{
		this.isWired = isWired;
		this.device = device;
	}
	
	public SteamController(SteamController other)
	{
		isWired = other.isWired;
		device = other.device;
		
		btnA = other.btnA;
		btnB = other.btnB;
		btnX = other.btnX;
		btnY = other.btnY;
		btnLB = other.btnLB;
		btnRB = other.btnRB;
		btnLT = other.btnLT;
		btnRT = other.btnRT;
		btnLG = other.btnLG;
		btnRG = other.btnRG;
		btnLeft = other.btnLeft;
		btnRight = other.btnRight;
		btnHome = other.btnHome;
		btnTouchLeft = other.btnTouchLeft;
		btnTouchRight = other.btnTouchRight;
		btnAnalog = other.btnAnalog;
		touchLeft = other.touchLeft;
		touchRight = other.touchRight;
		triggerLeft = other.triggerLeft;
		triggerRight = other.triggerRight;
		
		analogStick.x = other.analogStick.x;
		analogStick.y = other.analogStick.y;
		analogTouchLeft.x = other.analogTouchLeft.x;
		analogTouchLeft.y = other.analogTouchLeft.y;
		analogTouchRight.x = other.analogTouchRight.x;
		analogTouchRight.y = other.analogTouchRight.y;
	}
	
	public static List<SteamController> getConnectedControllers()
	{
		List<SteamController> controllers = new ArrayList<>();
		
		try
		{
			UsbHub rootHub = UsbHostManager.getUsbServices().getRootUsbHub();
			
			//noinspection unchecked
			for(UsbDevice device : (List<UsbDevice>)rootHub.getAttachedUsbDevices())
			{
				UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
				short vid = descriptor.idVendor();
				short pid = descriptor.idProduct();
				
				if(vid == 0x28DE && (pid == 0x1102 || pid == 0x1142))
				{
					controllers.add(new SteamController(device, pid == 0x1102));
				}
			}
		}
		catch(UsbException e)
		{
			e.printStackTrace();
		}
		
		return controllers;
	}
	
	public boolean isWired() { return isWired; }
	
	public boolean isAHeld()
	{
		return btnA;
	}
	
	public boolean isBHeld()
	{
		return btnB;
	}
	
	public boolean isXHeld()
	{
		return btnX;
	}
	
	public boolean isYHeld()
	{
		return btnY;
	}
	
	public boolean isLBHeld()
	{
		return btnLB;
	}
	
	public boolean isRBHeld()
	{
		return btnRB;
	}
	
	public boolean isLTHeld()
	{
		return btnLT;
	}
	
	public boolean isRTHeld()
	{
		return btnRT;
	}
	
	public boolean isLGHeld()
	{
		return btnLG;
	}
	
	public boolean isRGHeld()
	{
		return btnRG;
	}
	
	public boolean isCenterLeftHeld()
	{
		return btnLeft;
	}
	
	public boolean isCenterRightHeld()
	{
		return btnRight;
	}
	
	public boolean isHomeHeld()
	{
		return btnHome;
	}
	
	public boolean isLeftPadPressed()
	{
		return btnTouchLeft;
	}
	
	public boolean isRightPadPressed()
	{
		return btnTouchRight;
	}
	
	public boolean isAnalogStickPressed()
	{
		return btnAnalog;
	}
	
	public boolean isLeftPadTouched()
	{
		return touchLeft;
	}
	
	public boolean isRightPadTouched()
	{
		return touchRight;
	}
	
	public double getLeftTrigger()
	{
		return triggerLeft;
	}
	
	public double getRightTrigger()
	{
		return triggerRight;
	}
	
	public Analog2D getLeftTouchPosition()
	{
		return analogTouchLeft;
	}
	
	public Analog2D getRightTouchPosition()
	{
		return analogTouchRight;
	}
	
	public Analog2D getAnalogStickPosition()
	{
		return analogStick;
	}
	
	public UsbDevice getDevice()
	{
		return device;
	}
	
	SteamController updateBtnA(boolean btnA)
	{
		this.btnA = btnA;
		return this;
	}
	
	SteamController updateBtnB(boolean btnB)
	{
		this.btnB = btnB;
		return this;
	}
	
	SteamController updateBtnX(boolean btnX)
	{
		this.btnX = btnX;
		return this;
	}
	
	SteamController updateBtnY(boolean btnY)
	{
		this.btnY = btnY;
		return this;
	}
	
	SteamController updateBtnLB(boolean btnLB)
	{
		this.btnLB = btnLB;
		return this;
	}
	
	SteamController updateBtnRB(boolean btnRB)
	{
		this.btnRB = btnRB;
		return this;
	}
	
	SteamController updateBtnLT(boolean btnLT)
	{
		this.btnLT = btnLT;
		return this;
	}
	
	SteamController updateBtnRT(boolean btnRT)
	{
		this.btnRT = btnRT;
		return this;
	}
	
	SteamController updateBtnLG(boolean btnLG)
	{
		this.btnLG = btnLG;
		return this;
	}
	
	SteamController updateBtnRG(boolean btnRG)
	{
		this.btnRG = btnRG;
		return this;
	}
	
	SteamController updateBtnLeft(boolean btnLeft)
	{
		this.btnLeft = btnLeft;
		return this;
	}
	
	SteamController updateBtnRight(boolean btnRight)
	{
		this.btnRight = btnRight;
		return this;
	}
	
	SteamController updateBtnHome(boolean btnHome)
	{
		this.btnHome = btnHome;
		return this;
	}
	
	SteamController updateBtnTouchLeft(boolean btnTouchLeft)
	{
		this.btnTouchLeft = btnTouchLeft;
		return this;
	}
	
	SteamController updateBtnTouchRight(boolean btnTouchRight)
	{
		this.btnTouchRight = btnTouchRight;
		return this;
	}
	
	SteamController updateBtnAnalog(boolean btnAnalog)
	{
		this.btnAnalog = btnAnalog;
		return this;
	}
	
	SteamController updateTouchLeft(boolean touchLeft)
	{
		this.touchLeft = touchLeft;
		return this;
	}
	
	SteamController updateTouchRight(boolean touchRight)
	{
		this.touchRight = touchRight;
		return this;
	}
	
	SteamController updateTriggerLeft(double triggerLeft)
	{
		this.triggerLeft = triggerLeft;
		return this;
	}
	
	SteamController updateTriggerRight(double triggerRight)
	{
		this.triggerRight = triggerRight;
		return this;
	}
	
	SteamController updateLeftPadX(double x)
	{
		analogTouchLeft.x = x;
		return this;
	}
	
	SteamController updateLeftPadY(double y)
	{
		analogTouchLeft.y = y;
		return this;
	}
	
	SteamController updateRightPadX(double x)
	{
		analogTouchRight.x = x;
		return this;
	}
	
	SteamController updateRightPadY(double y)
	{
		analogTouchRight.y = y;
		return this;
	}
	
	SteamController updateAnalogX(double x)
	{
		analogStick.x = x;
		return this;
	}
	
	SteamController updateAnalogY(double y)
	{
		analogStick.y = y;
		return this;
	}
}
