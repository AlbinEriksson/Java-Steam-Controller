package se.albin.steamcontroller;

import se.albin.jbinary.BitArrayInputStream;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.util.UsbUtil;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "WeakerAccess", "unused", "UnusedReturnValue" })
public final class SteamControllerListener
{
	private final List<SteamControllerSubscriber> subscribers = new ArrayList<>();
	private final List<RawDataListener> rawDataListeners = new ArrayList<>();
	
	private final SteamController controller;
	
	private boolean isRunning;
	
	private UsbDevice device;
	private UsbInterface iface;
	private UsbEndpoint endpoint;
	private UsbPipe pipe;
	
	public SteamControllerListener(SteamController controller)
	{
		this.controller = controller;
		
		device = controller.getDevice();
	}
	
	public boolean open()
	{
		if(isRunning)
			return false;
		
		UsbConfiguration config = device.getActiveUsbConfiguration();
		@SuppressWarnings("unchecked")
		List<UsbInterface> interfaceList = (List<UsbInterface>)config.getUsbInterfaces();
		iface = interfaceList.get(controller.isWired() ? 2 : 1);
		endpoint = (UsbEndpoint)iface.getUsbEndpoints().get(0);
		
		try
		{
			iface.claim(forceClaim -> true);
		}
		catch(UsbException e)
		{
			e.printStackTrace();
			return false;
		}
		
		pipe = endpoint.getUsbPipe();
		
		try
		{
			pipe.open();
		}
		catch(UsbException e)
		{
			e.printStackTrace();
			
			try
			{
				iface.release();
			}
			catch(UsbException e1)
			{
				e1.printStackTrace();
			}
			
			return false;
		}
		
		new Thread(
			() ->
			{
				isRunning = true;
				
				byte[] buffer = new byte[UsbUtil.unsignedInt(
					endpoint.getUsbEndpointDescriptor().wMaxPacketSize())];
				BitArrayInputStream input = new BitArrayInputStream(buffer, ByteOrder.LITTLE_ENDIAN);
				
				SteamController last = null;
				boolean skipCopy = false;
				
				while(isRunning)
				{
					try
					{
						pipe.syncSubmit(buffer);
					}
					catch(UsbException e)
					{
						e.printStackTrace();
						close();
					}
					
					if(buffer[2] == 0x01)
					{
						input.goToByte(8);
						
						boolean a = input.readAsBoolean();
						boolean x = input.readAsBoolean();
						boolean b = input.readAsBoolean();
						boolean y = input.readAsBoolean();
						boolean lb = input.readAsBoolean();
						boolean rb = input.readAsBoolean();
						boolean lt = input.readAsBoolean();
						boolean rt = input.readAsBoolean();
						
						boolean lg = input.readAsBoolean();
						boolean cr = input.readAsBoolean();
						boolean home = input.readAsBoolean();
						boolean cl = input.readAsBoolean();
						boolean dpadDown = input.readAsBoolean();
						boolean dpadLeft = input.readAsBoolean();
						boolean dpadRight = input.readAsBoolean();
						boolean dpadUp = input.readAsBoolean();
						
						boolean leftPadAnalogStickConjunction = input.readAsBoolean();
						boolean analogStickPress = input.readAsBoolean();
						input.readAsBoolean(); //TODO: What is this?
						boolean rightPadTouch = input.readAsBoolean();
						boolean leftPadTouch = input.readAsBoolean();
						boolean rightPadPress = input.readAsBoolean();
						boolean leftPadPress = input.readAsBoolean() && leftPadTouch;
						boolean rg = input.readAsBoolean();
						
						double analogLT = input.readAsShort(8) / 255.0;
						double analogRT = input.readAsShort(8) / 255.0;
						input.goToByte(16);
						double analogStickOrLeftPadX = Analog2D.transformSignedShort(input.readAsShort(16));
						double analogStickOrLeftPadY = Analog2D.transformSignedShort(input.readAsShort(16));
						double rightPadX = Analog2D.transformSignedShort(input.readAsShort(16));
						double rightPadY = Analog2D.transformSignedShort(input.readAsShort(16));
						
						controller.updateLeftPadX(0)
						          .updateLeftPadY(0);
						
						if(leftPadAnalogStickConjunction)
						{
							if(leftPadTouch)
							{
								controller.updateLeftPadX(analogStickOrLeftPadX)
								          .updateLeftPadY(analogStickOrLeftPadY);
							}
							else
							{
								controller.updateAnalogX(analogStickOrLeftPadX)
								          .updateAnalogY(analogStickOrLeftPadY);
								
								skipCopy = true;
							}
						}
						else if(leftPadTouch)
						{
							controller.updateLeftPadX(analogStickOrLeftPadX)
							          .updateLeftPadY(analogStickOrLeftPadY);
						}
						else
						{
							controller.updateAnalogX(analogStickOrLeftPadX)
							          .updateAnalogY(analogStickOrLeftPadY);
						}
						
						controller.updateBtnA(a)
						          .updateBtnB(b)
						          .updateBtnX(x)
						          .updateBtnY(y)
						          .updateBtnLB(lb)
						          .updateBtnRB(rb)
						          .updateBtnLT(lt)
						          .updateBtnRT(rt)
						          .updateBtnLG(lg)
						          .updateBtnRight(cr)
						          .updateBtnHome(home)
						          .updateBtnLeft(cl)
						          .updateBtnAnalog(analogStickPress)
						          .updateTouchRight(rightPadTouch)
						          .updateTouchLeft(leftPadTouch)
						          .updateBtnTouchRight(rightPadPress)
						          .updateBtnTouchLeft(leftPadPress)
						          .updateBtnRG(rg)
						          .updateTriggerLeft(analogLT)
						          .updateTriggerRight(analogRT)
						          .updateRightPadX(rightPadX)
						          .updateRightPadY(rightPadY);
						
						for(RawDataListener listener : rawDataListeners)
						{
							byte[] bufferCopy = new byte[buffer.length];
							System.arraycopy(buffer, 0, bufferCopy, 0, buffer.length);
							
							listener.receive(bufferCopy);
						}
						
						if(!skipCopy)
						{
							if(last != null)
								for(SteamControllerSubscriber subscriber : subscribers)
									subscriber.update(controller, last);
							
							last = new SteamController(controller);
						}
						else
							skipCopy = false;
					}
				}
				
				try
				{
					pipe.close();
					iface.release();
				}
				catch(UsbException e)
				{
					e.printStackTrace();
				}
				
			}, "SteamController").start();
		
		return true;
	}
	
	public void close() { isRunning = false; }
	
	public void addSubscriber(SteamControllerSubscriber subscriber) { subscribers.add(subscriber); }
	
	public void removeSubscriber(SteamControllerSubscriber subscriber) { subscribers.remove(subscriber); }
	
	public void addRawDataListener(RawDataListener listener) { rawDataListeners.add(listener); }
	
	public void removeRawDataListener(RawDataListener listener) { rawDataListeners.remove(listener); }
}
