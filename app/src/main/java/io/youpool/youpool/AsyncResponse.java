package io.youpool.youpool;

import java.util.ArrayList;
import java.util.HashMap;

public interface AsyncResponse {
    void processFinish(ArrayList<HashMap<String, String>> output);
}
