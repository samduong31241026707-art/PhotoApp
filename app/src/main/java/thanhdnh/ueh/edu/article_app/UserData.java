package thanhdnh.ueh.edu.article_app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.GridView;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserData {
  public static UserList data;
  private Context context;
  private GridView gridview;
  private final ExecutorService executor =
          Executors.newSingleThreadExecutor();

  public UserData(Context context, GridView gridview) {
    this.context = context;
    this.gridview = gridview;
  }

  public static User getUserFromId(int id) {
    for (int i = 0; i < data.getUsers().size(); i++)
      if (data.getUsers().get(i).getId() == id)
        return data.getUsers().get(i);
    return null;
  }

  public void loadData(String url, Activity activity) {
    executor.execute(() -> {
      File file = Downloader.downloadFile(url, context.getCacheDir());
      if (file == null) {
        Log.e("USERDATA", "download failed: " + url);
        return;
      }
      activity.runOnUiThread(() -> {
        Gson gson = new Gson();
        data = gson.fromJson(readText(file), (Type) UserList.class);
        Log.d("USERDATA", "users = " + data.getUsers().size());
        UserAdapter adapter =
                new UserAdapter(data.getUsers(), context);
        gridview.setAdapter(adapter);
      });
    });
  }

  public String readText(File file) {
    BufferedReader reader = null;
    try {
      InputStream stream = new FileInputStream(file);
      reader = new BufferedReader(new InputStreamReader(stream));
      StringBuffer buffer = new StringBuffer();
      String line = "";
      while ((line = reader.readLine()) != null) {
        buffer.append(line + "\n");
      }
      return buffer.toString();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
    return reader.toString();
  }
}