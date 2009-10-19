/**
 * Copyright 2009 pepr Framework
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.peprframework.core;

import java.util.concurrent.CopyOnWriteArrayList;

import org.jetlang.channels.Channel;
import org.jetlang.channels.ChannelSubscription;
import org.jetlang.channels.Subscribable;
import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.DisposingExecutor;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 * @param <T>
 */
public class RoundRobinMemoryChannel<T> implements Channel<T> {

	private final CopyOnWriteArrayList<Callback<T>> _subscribers = new CopyOnWriteArrayList<Callback<T>>();

	private int latestSubscriber = 0;
	
    public int subscriberCount() {
        return _subscribers.size();
    }

    public void publish(T s) {
    	latestSubscriber = (latestSubscriber + 1) % _subscribers.size();
    	_subscribers.get(latestSubscriber).onMessage(s);
    }

    public Disposable subscribe(DisposingExecutor queue, Callback<T> onReceive) {
        ChannelSubscription<T> subber = new ChannelSubscription<T>(queue, onReceive);
        return subscribe(subber);
    }

    public Disposable subscribe(Subscribable<T> sub) {
        return subscribeOnProducerThread(sub.getQueue(), sub);
    }

    public Disposable subscribeOnProducerThread(final DisposingExecutor queue, final Callback<T> callbackOnQueue) {
        Disposable unSub = new Disposable() {
            public void dispose() {
                remove(callbackOnQueue);
                queue.remove(this);
            }
        };
        queue.add(unSub);
        //finally add subscription to start receiving events.
        _subscribers.add(callbackOnQueue);
        return unSub;
    }

    private void remove(Callback<T> callbackOnQueue) {
        _subscribers.remove(callbackOnQueue);
    }

    public void clearSubscribers() {
        _subscribers.clear();
    }

}
