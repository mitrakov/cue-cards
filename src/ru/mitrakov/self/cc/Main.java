package ru.mitrakov.self.cc;

import com.sleepycat.je.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 2)
            usage();
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

        // open environment
        EnvironmentConfig envCfg = new EnvironmentConfig();
        envCfg.setAllowCreate(true);
        File f = new File(String.format("%s/data", System.getProperty("user.dir")));
        f.mkdir();
        Environment env = new Environment(f, envCfg);

        // open database
        DatabaseConfig dbCfg = new DatabaseConfig();
        dbCfg.setAllowCreate(true);
        Database db = env.openDatabase(null, args[0], dbCfg);

        // key, value placeholders
        DatabaseEntry key = new DatabaseEntry();
        DatabaseEntry value = new DatabaseEntry();

        switch (args[1]) {
            case "list": {
                Cursor cursor = db.openCursor(null, null);
                while (cursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS)
                    System.out.println(new String(key.getData(), "UTF-8") + "         " + new String(value.getData(), "UTF-8"));
                cursor.close();
                break;
            }
            case "insert": {
                if (args.length == 3 || args.length == 4) {
                    key.setData(args[2].getBytes("UTF-8"));
                    value.setData((args.length == 4 ? args[3] : "").getBytes("UTF-8"));
                    db.put(null, key, value);
                    break;
                } else usage();
                break;
            }
            case "remove": {
                if (args.length == 3) {
                    key.setData(args[2].getBytes("UTF-8"));
                    db.delete(null, key);
                } else usage();
                break;
            }
            case "get": {
                if (args.length == 3) {
                    key.setData(args[2].getBytes("UTF-8"));
                    db.get(null, key, value, LockMode.DEFAULT);
                    System.out.println(value.getData() != null ? new String(value.getData(), "UTF-8") : "NULL");
                } else usage();
                break;
            }
            case "show": {
                Gui gui = new Gui();
                List<String> keys = new ArrayList<>();
                Cursor cursor = db.openCursor(null, null);
                while (cursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS)
                    keys.add(new String(key.getData(), "UTF-8"));
                if (args.length == 2)
                    gui.show(keys, 10);
                else if (args.length == 3) {
                    int delay = Integer.parseUnsignedInt(args[2]);
                    gui.show(keys, delay);
                } else usage();
            }
            default: usage();
        }

        db.close();
        env.close();
    }

    private static void usage() {
        System.out.println("Usage java -jar cue_cards.jar <database> <cmd> [<options>]\n");
        System.out.println(" cmd:");
        System.out.println("- list                   - shows database content");
        System.out.println("- insert <key> [<value>] - adds/replaces new key/value pair");
        System.out.println("- get <key>              - finds value by key");
        System.out.println("- remove <key>           - deletes key/value pair");
        System.out.println("- show [<delay>]         - shows gui every <delay> minutes");
        System.exit(0);
    }
}
