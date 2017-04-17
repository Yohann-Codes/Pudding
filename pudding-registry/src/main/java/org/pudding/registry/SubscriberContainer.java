package org.pudding.registry;

import org.pudding.common.utils.Lists;
import org.pudding.common.utils.Maps;
import org.pudding.transport.api.Channel;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Hold subscribers.
 *
 * @author Yohann.
 */
public class SubscriberContainer {
    // <serviceName : hosts>
    private ConcurrentMap<String, List<String>> hosts = Maps.newConcurrentHashMap();
    // <serviceName : channels>
    private ConcurrentMap<String, List<Channel>> channels = Maps.newConcurrentHashMap();

    public void putHost(String serviceName, String host) {
        List<String> hostList;
        synchronized (hosts) {
            if (hosts.containsKey(serviceName)) {
                hostList = hosts.get(serviceName);
                hostList.add(host);
            } else {
                hostList = Lists.newArrayList();
                hostList.add(host);
                hosts.put(serviceName, hostList);
            }
        }
    }

    public List<String> getServiceName(String host) {
        List<String> serviceNameList = Lists.newArrayList();
        for (Map.Entry<String, List<String>> entry : hosts.entrySet()) {
            List<String> hosts = entry.getValue();
            for (String h : hosts) {
                if (h.equals(host)) {
                    serviceNameList.add(entry.getKey());
                }
            }
        }
        return serviceNameList;
    }

    public void putChannel(String serviceName, Channel channel) {
        List<Channel> channelList;
        synchronized (channels) {
            if (channels.containsKey(serviceName)) {
                channelList = channels.get(serviceName);
                for (Channel ch : channelList) {
                    if (ch == channel) {
                        return;
                    }
                }
                channelList.add(channel);
            } else {
                channelList = Lists.newArrayList();
                channelList.add(channel);
                channels.put(serviceName, channelList);
            }
        }
    }

    public void clearChannel(String serviceName) {
        if (channels.containsKey(serviceName)) {
            List<Channel> channelList = channels.get(serviceName);
            Iterator<Channel> it = channelList.iterator();
            while (it.hasNext()) {
                Channel ch = it.next();
                if (!ch.isActive()) {
                    it.remove();
                }
            }
            if (channelList.size() == 0) {
                channels.remove(serviceName);
            }
        }
    }

    public List<Channel> getChannels(String serviceName) {
        if (channels.containsKey(serviceName)) {
            return channels.get(serviceName);
        }
        return null;
    }

    @Override
    public String toString() {
        return "SubscriberContainer: hosts:" + hosts + "\nchannels: " + channels;
    }
}
