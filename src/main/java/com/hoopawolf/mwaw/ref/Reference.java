package com.hoopawolf.mwaw.ref;

import com.hoopawolf.mwaw.proxy.ClientProxy;
import com.hoopawolf.mwaw.proxy.IProxy;
import com.hoopawolf.mwaw.proxy.ServerProxy;
import net.minecraftforge.fml.DistExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reference
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";

    public static final String MOD_ID = "mwaw";
    public static final String MOD_NAME = "Myth & Monsters Mod";
    public static final String VERSION = "1.15.2-1.0.0";
    public static final String SERVER_PROXY_CLASS = "com.hoopawolf.mwaw.proxy.ServerProxy";
    public static final String CLIENT_PROXY_CLASS = "com.hoopawolf.mwaw.proxy.ClientProxy";
    public static final String GUI_FACTORY_CLASS = "com.hoopawolf.mwaw.gui.GUIConfigFactory";


}
