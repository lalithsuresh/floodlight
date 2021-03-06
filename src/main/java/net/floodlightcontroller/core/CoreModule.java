package net.floodlightcontroller.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.floodlightcontroller.core.internal.Controller;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.counter.CounterStore;
import net.floodlightcontroller.counter.ICounterStoreService;
import net.floodlightcontroller.perfmon.IPktInProcessingTimeService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.storage.IStorageSourceService;

public class CoreModule implements IFloodlightModule {
    Controller controller;
    
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> services =
                new ArrayList<Class<? extends IFloodlightService>>(2);
        services.add(IFloodlightProviderService.class);
        services.add(ICounterStoreService.class);
        return services;
    }

    @Override
    public Map<Class<? extends IFloodlightService>,
               IFloodlightService> getServiceImpls() {
        controller = new Controller();
        ICounterStoreService counterStore = new CounterStore();
        controller.setCounterStore(counterStore);
        
        Map<Class<? extends IFloodlightService>,
            IFloodlightService> m = 
                new HashMap<Class<? extends IFloodlightService>,
                            IFloodlightService>();
        m.put(IFloodlightProviderService.class, controller);
        m.put(ICounterStoreService.class, counterStore);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> dependencies =
            new ArrayList<Class<? extends IFloodlightService>>(4);
        dependencies.add(IStorageSourceService.class);
        dependencies.add(IOFMessageFilterManagerService.class);
        dependencies.add(IPktInProcessingTimeService.class);
        dependencies.add(IRestApiService.class);
        return dependencies;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
       controller.setStorageSourceService(
           context.getServiceImpl(IStorageSourceService.class));
       controller.setPktInProcessingService(
           context.getServiceImpl(IPktInProcessingTimeService.class));
       controller.setCounterStore(
           context.getServiceImpl(ICounterStoreService.class));
       controller.setMessageFilterManagerService(
           context.getServiceImpl(IOFMessageFilterManagerService.class));         
       controller.setRestApiService(
           context.getServiceImpl(IRestApiService.class));
       controller.init();
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        controller.startupComponents();
    }
}
