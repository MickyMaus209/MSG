package com.mickymaus209.msg.spigot.data;

import com.mickymaus209.msg.common.Data;
import com.mickymaus209.msg.spigot.Msg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MySQLData implements Data {
    private final Msg msg;
    private final CustomFile file;
    private static final String FILE_NAME = "mysql.yml";
    private final Map<String, Object> data;
    private boolean isEnabled;
    private String host, port, username, password;

    public MySQLData(Msg msg) {
        this.msg = msg;
        file = new CustomFile(msg, FILE_NAME, this);
        file.setup();
        data = new HashMap<>();
        initialize();
    }

    /**
     * Setting up default data which is set when key is missing
     */
    public void initialize() {
        Map<String, Object> defaultData = new LinkedHashMap<>();
        defaultData.put("enabled", false);
        defaultData.put("host", "localhost");
        defaultData.put("port", "3306");
        defaultData.put("username", "admin");
        defaultData.put("password", "123456");

        for (String key : defaultData.keySet())
            file.getConfig().addDefault(key, defaultData.get(key));

        file.getConfig().options().copyDefaults(true);
        file.save();

        loadData(defaultData);
    }

    public void loadData(Map<String, Object> defaultData) {
        for (String key : defaultData.keySet()) {
            if (file.getConfig().get(key) == null) continue;
            data.put(key, file.getConfig().get(key));
        }

        isEnabled = (boolean) getData("enabled");
        host = (String) getData("host");
        port = (String) getData("port");
        username = (String) getData("username");
        password = (String) getData("password");
    }

    /**
     * This method is used to get data from the data map which was previously filled with the data set in config.
     *
     * @param key is required to find the values in the data. Keys are matching with config keys as the map got its keys & values from the config.
     * @return object that is set in configData map
     */
    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public void onFileCreate() {

    }
    public boolean isEnabled() {
        return isEnabled;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}
