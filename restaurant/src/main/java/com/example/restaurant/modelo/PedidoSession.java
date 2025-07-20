package com.example.restaurant.modelo;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PedidoSession {
    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() { return items; }
    public void addItem(Item item) { items.add(item); }
    public void clear() { items.clear(); }
}