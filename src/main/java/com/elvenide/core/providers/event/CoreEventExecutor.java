package com.elvenide.core.providers.event;

import java.util.function.Consumer;

interface CoreEventExecutor extends Consumer<CoreEvent> {

    CoreEventHandler getData();

    CoreListener getListener();

}
