package com.billion.async.processing.monitor;

import com.billion.async.processing.AbstractAsyncHookRegister;
import com.billion.async.processing.executor.AsyncThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.*;

/**
 * @author xuliang
 * @since 2019/05/13
 */
public class RegisterMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterMonitor.class);

    private static final RegisterMonitor INSTANCE = new RegisterMonitor();

    private final ConcurrentMap<String, AbstractAsyncHookRegister> registerMap;

    private final ScheduledExecutorService executorService;

    public static RegisterMonitor getInstance() {
        return INSTANCE;
    }

    private RegisterMonitor() {
        executorService = Executors.newScheduledThreadPool(1,
                new AsyncThreadFactory(AsyncThreadFactory.THREAD_PREFIX + "-monitor"));
        registerMap = new ConcurrentHashMap<>();
        initTask();
    }

    private void initTask() {
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                Set<String> keySet = registerMap.keySet();
                if (!keySet.isEmpty()) {
                    for (String key : keySet) {
                        AbstractAsyncHookRegister register = registerMap.get(key);
                        if (register.getCurrentState().isFinalState()) {
                            registerMap.remove(key);
                        } else {
                            if ((System.currentTimeMillis() - register.createTime()) > register.timeout()) {
                                LOGGER.error("checked there is a register not triggered. the uniqueId is: [{}]", key);
                                //registerMap.remove(key);
                            }
                        }
                    }
                }
            }
        }, 500L, TimeUnit.MILLISECONDS);
    }

    public void register(String uniqueId, AbstractAsyncHookRegister register) {
        if (uniqueId != null && register != null) {
            registerMap.put(uniqueId, register);
        }
    }


}
