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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jetlang.channels.Channel;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;

/**
 * @author Volker Fritzsch
 * @version 1.0
 *
 */
public class Engine {

	private List<Process> processes = new LinkedList<Process>();
	
	private ExecutorService service;
	
	private PoolFiberFactory factory;
	
	public Engine() {
		service = Executors.newCachedThreadPool();
		factory = new PoolFiberFactory(service);
	}
	
	@SuppressWarnings("unchecked")
	public void startProcess(final Process process) {
		Map<String,Activity> activityLookup = new HashMap<String,Activity>();
		Map<Activity,Fiber> activityFiberLookup = new HashMap<Activity,Fiber>();
		Map<Transition,Channel<Context>> transitionChannelLookup = new HashMap<Transition,Channel<Context>>();
		Map<Activity,Set<Channel<Context>>> publisherChannelLookup = new HashMap<Activity, Set<Channel<Context>>>();
		Map<Activity,Set<Channel<Context>>> subscriberChannelLookup = new HashMap<Activity, Set<Channel<Context>>>();
		
		// initialize fibers and activities, inject process reference
		Set<Activity> activities = process.getActivities();
		for (Activity activity : activities) {
			activity.setProcess(process);
			activity.initialize();
			
			Fiber fiber = factory.create();
			fiber.start();
			
			activityLookup.put(activity.getName(), activity);
			activityFiberLookup.put(activity, fiber);
			
//			System.out.println("Created fiber for activity: " + activity.getName());
		}
		
		// initialize channels
		Set<Transition> transitions = process.getTransitions();
		for (Transition transition : transitions) {
			Activity publisher = transition.getSource();
			Activity subscriber = transition.getTarget();
			Channel<Context> channel = new MemoryChannel<Context>();
			transitionChannelLookup.put(transition, channel);
			
			Set<Channel<Context>> publishToChannels = publisherChannelLookup.get(publisher);
			if (publishToChannels == null) {
				publishToChannels = new HashSet<Channel<Context>>();
				publisherChannelLookup.put(publisher, publishToChannels);
			}
			publishToChannels.add(channel);
			
			Set<Channel<Context>> subscribeToChannels = subscriberChannelLookup.get(subscriber);
			if (subscribeToChannels == null) {
				subscribeToChannels = new HashSet<Channel<Context>>();
				subscriberChannelLookup.put(subscriber, subscribeToChannels);
			}
			subscribeToChannels.add(channel);
			
//			System.out.println("created channel " + publisher.getName() + " -> " + subscriber.getName());
		}
		
		// wire
		for (Transition transition : transitions) {
			Activity subscriber = transition.getTarget();
//			Set<Channel<Context>> subscribeToChannels = subscriberChannelLookup.get(subscriber);
			final Set<Channel<Context>> publishToChannels = publisherChannelLookup.get(subscriber);
			System.out.println("Setting callback on " + subscriber.getName());
			subscriber.setCallback(new EngineActivityCallback() {
			
				public void publish(Context ctx) {
					if (publishToChannels != null) {
						if (publishToChannels.size() == 1) {
							publishToChannels.iterator().next().publish(ctx);
						} else {
							for (Channel<Context> channel : publishToChannels) {
								channel.publish(new Context(ctx));
							}
						}
					}
				}
			});
			
			
			Fiber fiber = activityFiberLookup.get(subscriber);
//			for (Channel<Context> channel : subscribeToChannels) {
			Channel<Context> channel = transitionChannelLookup.get(transition);
			channel.subscribe(fiber, subscriber);
//			}
			
//			System.out.println("wired subscriber " + subscriber.getName() + " to:");
//				if (publishToChannels == null) {
//					System.out.println("\t" + channel);
//				} else {
//				for (Channel<Context> pubChannel : publishToChannels) {
//				System.out.println("\t" + channel + " -> " + pubChannel);
//				}
//				}
		}
		
		// initialize starters
		Set<Starter> starters = process.getStarters();
//		int possibleThreads = process.getActivities().size() + process.getStarters().size();
//		process.setSharedState(new ConcurrentHashMap<String, Object>(possibleThreads));
		for (final Starter starter : starters) {
			final Set<Channel<Context>> pubChannels = publisherChannelLookup.get(starter);
			starter.initialize();
			starter.setEngineCallback(new EngineStarterCallback() {
			
				private short number = 0;
				
				public void startProcess(Object output) {
					number = (short) ((number + 1) % 255);
					Context ctx = new Context(number, process.getSharedState());
					ctx.set(starter.name, output);
					ctx.setLatestActivity(starter.name);
					for (Channel<Context> channel : pubChannels) {
						channel.publish(new Context(ctx));
					}
				}
				
				public void startProcess(Object output, long timestamp, short number) {
					Context ctx = new Context(number, timestamp, process.getSharedState());
					ctx.set(starter.name, output);
					ctx.setLatestActivity(starter.name);
					for (Channel<Context> channel : pubChannels) {
						channel.publish(new Context(ctx));
					}
				}
			});
			
			service.execute(starter);
		}
		
		processes.add(process);
	}
	
	public void stopProcess(Process process) {
		processes.remove(process);
	}

	public boolean isRunning() {
		return processes.size() > 0;
	}
	
//	public static void main(String[] args) {
//		try {			
//			createJaxbContext();
//			Process p = unmarshallProcess(args[0]);
//			Engine engine = new Engine();
//			engine.startProcess(p);
//			
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
	
//	public static Process unmarshallProcess(String file) {
//		Process process = null;
//		try {
//			if (jaxbContext == null) {
//				createJaxbContext();
//			}
//			
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			process = (Process) unmarshaller.unmarshal(new File(file));
//		} catch (JAXBException ex) {
//			ex.printStackTrace();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		
//		return process;
//	}
//
//	private static JAXBContext jaxbContext;
//	
//	private static void createJaxbContext() throws JAXBException {
//		// jaxbContext = JAXBContext.newInstance(MovingAverage.class, MovingAverageConfiguration.class, Join.class, Plotter.class, MicrophoneStarter.class, FileStarter.class, FileStarterConfiguration.class, MicrophoneStarterConfiguration.class, MatlabAdapter.class, MatlabConfiguration.class, InputFilter.class, GroovyInputFilter.class, Process.class, Activity.class, SupportVectorMachine.class, SupportVectorMachineConfiguration.class, EchoStateNetwork.class, EchoStateNetworkConfiguration.class, PrintMessageActivity.class);
//		jaxbContext = JAXBContext.newInstance();
//	}
}
