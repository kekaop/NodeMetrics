package dev.kekaop;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public final class NodeMetrics extends JavaPlugin {

    private HttpServer httpServer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        final Component pluginName = MiniMessage.miniMessage().deserialize(
                "<gradient:#6600ff:#f79459>[NodeMetrics]</gradient>"
        );
        getComponentLogger().info(pluginName.append(Component.text(" Plugin enabled")));

        int port = this.getConfig().getInt("port");
        String secret = this.getConfig().getString("secret");
        String endpoint = normalizeEndpoint(this.getConfig().getString("endpoint"));

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                httpServer  = HttpServer.create(new InetSocketAddress(port), 0);

                httpServer.createContext(endpoint, exchange -> {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

                    JsonObject json;
                    try {
                        json = JsonParser.parseString(body).getAsJsonObject();
                    } catch (Exception e) {
                        getComponentLogger().warn(pluginName.append(Component.text("JSON parse error: " + e.getMessage())));
                        exchange.sendResponseHeaders(400, 0);
                        exchange.close();
                        return;
                    }

                    if (!json.has("secret")) {
                        getLogger().warning("NO SECRET FIELD");
                        exchange.sendResponseHeaders(403, 0);
                        exchange.close();
                        return;
                    }

                    String incomingSecret = json.get("secret").getAsString();

                    if (!incomingSecret.equals(secret)) {
                        byte[] err = "forbidden".getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(403, err.length);
                        exchange.getResponseBody().write(err);
                        exchange.close();
                        return;
                    }
                    JsonObject obj = collectData();

                    byte[] out = obj.toString().getBytes(StandardCharsets.UTF_8);

                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, out.length);
                    exchange.getResponseBody().write(out);
                    exchange.close();
                    getComponentLogger().info(pluginName.append(Component.text(" Report sent")));
                });

                httpServer.start();
                getComponentLogger().info(pluginName.append(Component.text(" HTTP server started on port: " + port)));
                getComponentLogger().info(pluginName.append(Component.text(" Endpoint: " + endpoint)));
                getComponentLogger().info(pluginName.append(Component.text(" Monitoring started")));

            } catch (IOException e) {
                getComponentLogger().error(pluginName.append(Component.text("Failed to start HTTP server: " + e.getMessage())));
            }
        });
    }


    @Override
    public void onDisable() {
        if (httpServer != null) {
            httpServer.stop(0);
            getComponentLogger().info(Component.text("HTTP server stopped"));
        }
    }

    private JsonObject collectData(){

        JsonObject report = new JsonObject();

        if (isResponseEnabled("nodeName")) {
            report.addProperty("node", this.getConfig().getString("nodeName"));
        }
        if (isResponseEnabled("node_region")) {
            report.addProperty("node_region", this.getConfig().getString("node-region"));
        }
        if (isResponseEnabled("online")) {
            report.addProperty("online", getServer().getOnlinePlayers().size());
        }
        if (isResponseEnabled("max_online")) {
            report.addProperty("max_online", getServer().getMaxPlayers());
        }
        if (isResponseEnabled("mspt")) {
            report.addProperty("mspt", getServer().getAverageTickTime());
        }
        if (isResponseEnabled("tps")) {
            JsonArray tpsArray = new JsonArray();
            for (double val : getServer().getTPS()) {
                tpsArray.add(val);
            }
            report.add("tps", tpsArray);
        }
        if (isResponseEnabled("total_memory")) {
            report.addProperty("total_memory", Runtime.getRuntime().totalMemory());
        }
        if (isResponseEnabled("free_memory")) {
            report.addProperty("free_memory", Runtime.getRuntime().freeMemory());
        }
        if (isResponseEnabled("max_memory")) {
            report.addProperty("max_memory", Runtime.getRuntime().maxMemory());
        }
        if (isResponseEnabled("timestamp")) {
            report.addProperty("timestamp", System.currentTimeMillis());
        }


        return report;
    }

    private boolean isResponseEnabled(String key) {
        return this.getConfig().getBoolean("response." + key, true);
    }

    private String normalizeEndpoint(String value) {
        if (value == null || value.isBlank()) {
            return "/command";
        }
        if (!value.startsWith("/")) {
            return "/" + value;
        }
        return value;
    }


}
