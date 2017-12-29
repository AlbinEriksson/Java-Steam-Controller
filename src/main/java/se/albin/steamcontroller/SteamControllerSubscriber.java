package se.albin.steamcontroller;

public interface SteamControllerSubscriber
{
	void update(SteamController state, SteamController last);
}
