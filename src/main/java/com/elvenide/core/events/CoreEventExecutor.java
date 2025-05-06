package com.elvenide.core.events;

import java.util.function.Consumer;

interface CoreEventExecutor extends Consumer<CoreEvent> {

    CoreEventHandler getData();

    CoreListener getListener();

}
