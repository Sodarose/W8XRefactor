package ulits;

import model.Store;

import java.io.IOException;

public class SaveJson {
    public static void save() throws IOException {
            JsonUtil.createJson(Store.jsonObjectList,Store.modifyPath);
    }
}
