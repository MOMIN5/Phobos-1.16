package me.earth.phobos;

import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import net.fabricmc.api.ModInitializer;

public class Phobos implements ModInitializer {

	public static final EventBus EVENT_BUS = new EventManager();

	@Override
	public void onInitialize() {
		System.out.println("My cock is HUGE");
	}

}
