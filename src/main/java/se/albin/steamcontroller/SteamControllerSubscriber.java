package se.albin.steamcontroller;

@FunctionalInterface
public interface SteamControllerSubscriber
{
	void update(SteamController state, SteamController last);
}
