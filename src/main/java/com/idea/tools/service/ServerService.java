package com.idea.tools.service;

import com.idea.tools.dto.ConnectionType;
import com.idea.tools.dto.Queue;
import com.idea.tools.dto.Server;
import com.idea.tools.dto.ServerType;
import com.idea.tools.markers.Listener;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.idea.tools.App.settings;
import static com.idea.tools.utils.GuiUtils.showYesNoDialog;

public class ServerService {

    private List<Listener<Server>> listeners = new LinkedList<>();
    private AtomicInteger generator;

    public ServerService() {
        int value = settings().getServersStream()
                              .map(Server::getId)
                              .max(Integer::compareTo)
                              .orElse(0);
        generator = new AtomicInteger(value);

        Listener<Server> listener = Listener.<Server>builder()
                .add(settings().getState()::put)
                .edit(settings().getState()::put)
                .remove(settings().getState()::remove)
                .build();

        listeners.add(listener);
    }

    public static List<Server> getDummies() {

        Server wildfly = new Server();
        wildfly.setId(0);
        wildfly.setName("Wildfly 1");
        wildfly.setQueues(Arrays.asList(new Queue(0, "Q1", wildfly), new Queue(1, "Q2", wildfly)));
        wildfly.setType(ServerType.WILDFLY_11);

        Server activeMq = new Server();
        activeMq.setId(1);
        activeMq.setName("Active MQ 1");
        activeMq.setHost("localhost");
        activeMq.setPort(61616);
        activeMq.setConnectionType(ConnectionType.TCP);
        activeMq.setQueues(Arrays.asList(new Queue(2, "Q1", activeMq), new Queue(3, "Q2", activeMq)));
        activeMq.setType(ServerType.ACTIVE_MQ);

        return Arrays.asList(wildfly, activeMq);
    }

    public void refresh(List<Server> servers) {
        //TODO implement reconnect
        listeners.forEach(listener -> servers.forEach(listener::edit));
    }

    public void saveOrUpdate(Server server) {
        if (server.getId() == null) {
            server.setId(generator.incrementAndGet());
            listeners.forEach(listener -> listener.add(server));
        } else {
            listeners.forEach(listener -> listener.edit(server));
        }
    }

    public boolean remove(Server server) {
        if (server == null) {
            return false;
        }
        boolean delete = showYesNoDialog(String.format("Do you want to delete server %s", server.getName()));

        if (delete) {
            listeners.forEach(listener -> listener.remove(server));
        }
        return delete;
    }

    public void addListener(Listener<Server> listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener<Server> listener) {
        listeners.remove(listener);
    }

}
